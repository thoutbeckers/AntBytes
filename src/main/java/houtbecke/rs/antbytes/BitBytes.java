package houtbecke.rs.antbytes;

import java.math.BigInteger;

import javax.annotation.Nonnull;

public class BitBytes {

    public static long pow(long l, int exp) {
        return BigInteger.valueOf(l).pow(exp).longValue();
    }

    public static long maskWithLength(int len) {
        return pow(2, len) - 1;
    }

    public static long clamp(long value, int bits) {
        return maskWithLength(bits) & value;
    }

    @Nonnull
    public static String toPaddedString(@Nonnull byte[] content) {
        StringBuilder ret = new StringBuilder();
        for (byte b : content) {
            String byteAsBinary = Integer.toBinaryString(b & 0xFF);
            for (int k = 0, len = 8 - byteAsBinary.length(); k < len; k++)
                ret.append('0');
            ret.append(byteAsBinary);
        }
        return ret.toString();
    }

    public static void output(@Nonnull byte[] output, int bytepos, int relativeBitpos, long value, int bitlength) {
        output(output, bytepos * 8 + relativeBitpos, value, bitlength);
    }

    public static void output(@Nonnull byte[] output, int bitpos, long value, int bitlength) {
        //if (bitpos % 8 != 0 || bitlength % 8 != 0) throw new RuntimeException("not supported yet");

        // calculate start position
        int bytePos = bitpos / 8;
        int bitposInByte = bitpos % 8;
        int writtenBits = 0;

        // clamp the bits we will write to the length indicated
        long bitsToWrite = clamp(value, bitlength);

        // check to see if there are still bits we need to write
        while (writtenBits < bitlength) {
            int bitlenInByte = Math.min(8 - bitposInByte, bitlength - writtenBits);

            // create a mask for the number of bits we are writing, and shift it left to the position where we will write the,
            long mask = maskWithLength(bitlenInByte) << (8 - bitposInByte - bitlenInByte);

            // work with only the bits not yet written
            long maskWritten = ~(maskWithLength(writtenBits) << (bitlength - writtenBits));
            long bitsNotWritten = bitsToWrite & maskWritten;

            // of those bits get the number of left most bits we want to write (bitlength - writtenBits are the bits not masked above)
            long val = bitsNotWritten >> (bitlength - writtenBits - bitlenInByte);

            // shift the bits we want to write to the correct position
            val <<= (8 - bitposInByte - bitlenInByte);

            // clear bits we will write to
            output[bytePos] &= (~mask);

            // OR to write the bits we prepared into the byte over the cleared bits
            output[bytePos] |= val;

            // increment the number of bits we wrote
            writtenBits += bitlenInByte;
            bitposInByte += bitlenInByte;
            if (bitposInByte == 8) {
                bitposInByte = 0;
                bytePos++;
            }
        }
    }

    public static long input(@Nonnull byte[] input, int bytepos, int relativeBitpos, final int bitlength, boolean signed) {
        return input(input, bytepos * 8 + relativeBitpos, bitlength, signed);
    }

    public static long input(@Nonnull byte[] input, int bytepos, int relativeBitpos, final int bitlength) {
        return input(input, bytepos * 8 + relativeBitpos, bitlength);
    }

    public static long input(@Nonnull byte[] input, int bitpos, int bitlength) {
        return input(input, bitpos, bitlength, false);
    }

    public static long input(@Nonnull byte[] input, int bitpos, int bitlength, boolean signed) {
        int bytePos = bitpos / 8;
        int bitposInByte = bitpos % 8;
        int readBits = 0;
        long ret = 0;
        boolean negative = false;
        if (signed) {
            if (bytePos >= input.length) {
                return 0;
            }
            if (((input[bytePos] >> (bitposInByte - 1)) & 1) == 1) {
                negative = true;
            }

            bitpos = bitpos + 1;
            bytePos = bitpos / 8;
            bitposInByte = bitpos % 8;
            bitlength = bitlength - 1;
        }

        while (readBits < bitlength) {
            // see how many bit from this byte we will read
            int bitlenInByte = Math.min(8 - bitposInByte, bitlength - readBits);

            // create a mask for getting the bits out the byte
            long mask = maskWithLength(bitlenInByte) << 8 - bitposInByte - bitlenInByte;

            if (bytePos >= input.length) {
                return 0;
            }
            // read the bits and shift them back again to be right aligned
            long valFromByte = (input[bytePos] & mask) >> 8 - bitposInByte - bitlenInByte;
            if (negative) {
                valFromByte = (~input[bytePos] & mask) >> 8 - bitposInByte - bitlenInByte;
            }

            // shift the bits into the correct place for storing.
            ret |= valFromByte << (bitlength - readBits - bitlenInByte);
            // obviously the above two steps can be combined, for clarity I'll leave it as is for now

            readBits += bitlenInByte;
            bitposInByte += bitlenInByte;
            if (bitposInByte == 8) {
                bitposInByte = 0;
                bytePos++;
            }
        }
        if (negative) {
            ret = -ret - 1;
        }
        return ret;

    }

    public static long inputLSB(@Nonnull byte[] input, int bytepos, int relativeBitposFromRight, final int bitlength, boolean signed) {
        return inputLSB(input, bytepos * 8 + relativeBitposFromRight, bitlength, signed);
    }

    public static long inputLSB(@Nonnull byte[] input, int bytepos, int relativeBitposFromRight, final int bitlength) {
        return inputLSB(input, bytepos * 8 + relativeBitposFromRight, bitlength);
    }

    public static long inputLSB(@Nonnull byte[] input, int bitpos, int bitlength) {
        return inputLSB(input, bitpos, bitlength, false);
    }

    public static long inputLSB(@Nonnull byte[] input, int bitpos, int bitlength, boolean signed) {

        int byteLength = 1;
        int lastBitLength = 0;
        int firstBitposFromRight = bitpos % 8;

        int firstBitLength = 8 - firstBitposFromRight;
        firstBitLength = Math.min(firstBitLength, bitlength);

        if (firstBitLength == 0) { firstBitLength = 8; }
        int firstBitposFromLeft = 8 - firstBitLength - firstBitposFromRight;

        int remainingBitLength = bitlength - firstBitLength;
        if (remainingBitLength > 0 && remainingBitLength % 8 != 0) {
            byteLength++;
            lastBitLength = remainingBitLength % 8;
            remainingBitLength = remainingBitLength - lastBitLength;
        }

        if (lastBitLength == 0) { lastBitLength = 8; }

        byteLength = byteLength + (remainingBitLength / 8);

        int lastBitPos = 8 - lastBitLength;

        int byteOffSet = (bitpos / 8);

        byte[] msbInput = new byte[byteLength];

        for (int i = 0 + byteOffSet; i < byteLength + byteOffSet; i++) {
            int msbPos = (byteLength - 1 - i + byteOffSet);

            if (i >= input.length || msbPos >= msbInput.length) {
                return 0;
            }
            msbInput[msbPos] = input[i];
        }
        if (byteLength == 1) {
            return input(msbInput, 0, firstBitposFromLeft, bitlength, signed);
        } else {
            return input(msbInput, 0, lastBitPos, bitlength, signed);
        }
    }

    public static void outputLSB(@Nonnull byte[] output, int bytepos, int relativeBitposFromRight, long value, int bitlength) {
        outputLSB(output, bytepos * 8 + relativeBitposFromRight, value, bitlength);
    }

    public static void outputLSB(@Nonnull byte[] output, int bitpos, long value, int bitlength) {
        if (bitlength == 0) { return; }
        int byteLength = 1;
        int lastBitLength = 0;
        int firstBitposFromRight = bitpos % 8;

        int firstBitLength = 8 - firstBitposFromRight;
        firstBitLength = Math.min(firstBitLength, bitlength);
        if (firstBitLength == 0) { firstBitLength = 8; }
        int firstBitposFromLeft = 8 - firstBitLength - firstBitposFromRight;

        int remainingBitLength = bitlength - firstBitLength;
        if (remainingBitLength > 0 && remainingBitLength % 8 != 0) {
            byteLength++;
            lastBitLength = remainingBitLength % 8;
            remainingBitLength = remainingBitLength - lastBitLength;
        }

        if (lastBitLength == 0) { lastBitLength = 8; }

        int lastBitPos = 8 - lastBitLength;
        byteLength = byteLength + (remainingBitLength / 8);
        if (byteLength == 1) { lastBitPos = firstBitposFromLeft; }

        byte[] msbOutput = new byte[byteLength];
        int byteOffSet = (bitpos / 8);

        for (int i = 0 + byteOffSet; i < byteLength + byteOffSet; i++) {
            int msbPos = (byteLength - 1 - i + byteOffSet);

            msbOutput[msbPos] = output[i];
        }

        output(msbOutput, 0, lastBitPos, value, bitlength);

        for (int i = 0 + byteOffSet; i < byteLength + byteOffSet; i++) {
            int msbPos = (byteLength - 1 - i + byteOffSet);

            output[i] = msbOutput[msbPos];
        }
    }
}
