package houtbecke.rs.antbytes;

import java.math.BigInteger;

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

    public static String toPaddedString(byte[] content) {
        StringBuilder ret = new StringBuilder();
        for (byte b: content) {
            String byteAsBinary = Integer.toBinaryString(b & 0xFF);
            for (int k = 0, len = 8 - byteAsBinary.length(); k < len; k++)
                ret.append('0');
            ret.append(byteAsBinary);
        }
        return ret.toString();
    }

    public static void output(byte[] output, int bytepos, int relativeBitpos, long value, int bitlength) {
        output(output, bytepos * 8 + relativeBitpos, value, bitlength);
    }

    public static void output(byte[] output, int bitpos, long value, int bitlength) {
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
            writtenBits+=bitlenInByte;
            bitposInByte += bitlenInByte;
            if (bitposInByte == 8) {
                bitposInByte = 0;
                bytePos++;
            }
        }
    }


    public static long input(byte[] input, int bytepos, int relativeBitpos, final int bitlength,boolean signed) {
        return input(input, bytepos * 8 + relativeBitpos, bitlength,signed);
    }

    public static long input(byte[] input, int bytepos, int relativeBitpos, final int bitlength) {
        return input(input, bytepos * 8 + relativeBitpos, bitlength);
    }


    public static long input(byte[] input, int bitpos, int bitlength) {
        return input(input,bitpos,bitlength,false);
    }

    public static long input(byte[] input, int bitpos, int bitlength,boolean signed) {



        int bytePos = bitpos / 8;
        int bitposInByte = bitpos % 8;
        int readBits = 0;
        long ret = 0;
        boolean negative=false;
        if (signed){
            if   (((input[bytePos]  >> bitpos) & 1)  == 1)
            {
                negative =true;
            }

            bitpos = bitpos +1;
            bytePos = bitpos / 8;
            bitposInByte = bitpos % 8;
            bitlength= bitlength-1;
        }


        while (readBits < bitlength) {
            // see how many bit from this byte we will read
            int bitlenInByte = Math.min(8 - bitposInByte, bitlength - readBits);



            // create a mask for getting the bits out the byte
            long mask = maskWithLength(bitlenInByte) << 8 - bitposInByte - bitlenInByte;

            // read the bits and shift them back again to be right aligned
            long valFromByte = (input[bytePos] & mask) >> 8 - bitposInByte - bitlenInByte;
            if (negative){
                valFromByte = (~input[bytePos] & mask) >> 8 - bitposInByte - bitlenInByte;
                valFromByte =valFromByte;
            }


            // shift the bits into the correct place for storing.
            ret |=  valFromByte << (bitlength - readBits - bitlenInByte);
            // obviously the above two steps can be combined, for clarity I'll leave it as is for now



            readBits+=bitlenInByte;
            bitposInByte+=bitlenInByte;
            if (bitposInByte == 8) {
                bitposInByte = 0;
                bytePos++;
            }
        }
        if (negative){
            ret =  -ret -1;
        }
        return ret;
    }



    public static long inputLSB(byte[] input, int bytepos, int relativeBitpos, final int bitlength,boolean signed) {
        return inputLSB(input, bytepos * 8 + relativeBitpos, bitlength, signed);
    }

    public static long inputLSB(byte[] input, int bytepos, int relativeBitpos, final int bitlength) {
        return inputLSB(input, bytepos * 8 + relativeBitpos, bitlength);
    }


    public static long inputLSB(byte[] input, int bitpos, int bitlength) {
        return inputLSB(input, bitpos, bitlength, false);
    }

    public static long inputLSB(byte[] input, int bitpos, int bitlength,boolean signed) {
        if (bitpos % 8 != 0 ) throw new RuntimeException("not supported yet");
        if (bitlength % 8 != 0  && signed) throw new RuntimeException("not supported yet");


        int byteOffSet = (bitpos/8);
        int byteLength = (bitlength/8);
        int lastBitLength = bitlength % 8;
        if (lastBitLength==0)lastBitLength=8;
        int bitShift = 8 -lastBitLength;

        if (bitlength % 8 !=0) byteLength++;
        byte[]  msbInput = new byte[byteLength];

        for(int i =0+byteOffSet; i< byteLength+byteOffSet;i++){
            int msbPos = (byteLength -1 -i + byteOffSet);

            if (msbPos==0){

                byte lastByte  = (byte)   clamp( (input[i] >> bitShift),lastBitLength);
                msbInput[msbPos] = lastByte;

            } else{
                msbInput[msbPos] = input[i];
            }
        }

        return input(msbInput,0,bitShift,bitlength,signed);
    }


    public static void outputLSB(byte[] output, int bytepos, int relativeBitpos, long value, int bitlength) {
        outputLSB(output, bytepos * 8 + relativeBitpos, value, bitlength);
    }

    public static void outputLSB(byte[] output, int bitpos, long value, int bitlength) {
                if (bitpos % 8 != 0 ) throw new RuntimeException("not supported yet");
                int lastBitLength = bitlength % 8;
                if (lastBitLength==0)lastBitLength=8;
                int bitShift = 8 -lastBitLength;
                int mask =   (8-lastBitLength);
                int byteLength = (bitlength/8);
                if (bitlength % 8 !=0) byteLength++;



        for (int i = 0 ; i < byteLength ; i++) {
                    int pos = bitpos / 8+i;

                    if (i ==  (byteLength-1)){
                       long lastValue = clamp(value,lastBitLength);
                        output[pos] = (byte)clamp(output[pos],mask);

                        output[pos] |=  (lastValue <<bitShift);
                    }else{
                        output[pos] = (byte) (value & 0xffL);
                        value >>= 8;
                    }


                 }
     }
}
