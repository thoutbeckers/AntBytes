import org.junit.Assert;
import org.junit.Test;

import houtbecke.rs.antbytes.BitBytes;

public class BitBytesTest {


    @Test
    public void testOutput() {
        //// test starting in the middle of a byte and overflowing into the next

        // start values zero
        byte[] output = { (byte) 0b0000_0000, (byte) 0b0000_0000, (byte) 0b0000_0000};
        ///////////////////////// clamped | the ten bits |
        BitBytes.output(output, 1, 4, 0b10_11__1001_0111, 10);

        Assert.assertEquals("000000000000_1110010111_00".replaceAll("_", ""), BitBytes.toPaddedString(output));


        // start values with one
        output = new byte[] { (byte) 0b1111_1111, (byte) 0b1111_1111, (byte) 0b1111_1111};
        ///////////////////////// clamped | the ten bits |
        BitBytes.output(output, 1, 4, 0b10_11__1001_0111, 10);

        Assert.assertEquals("111111111111_1110010111_11".replaceAll("_", ""), BitBytes.toPaddedString(output));


        //// test starting in the middle of a byte without overflowing

        // start values zero
        output = new byte[] { (byte) 0b0000_0000, (byte) 0b0000_0000, (byte) 0b0000_0000};
        ///////////////////////// clamped | 5 bits |
        BitBytes.output(output, 1, 2, 0b11_10101, 5);

        Assert.assertEquals("0000000000_10101_000000000".replaceAll("_", ""), BitBytes.toPaddedString(output));


        // start values with one
        output = new byte[] { (byte) 0b1111_1111, (byte) 0b1111_1111, (byte) 0b1111_1111};
        ///////////////////////// clamped | the ten bits |
        BitBytes.output(output, 1, 2, 0b10_10101, 5);

        Assert.assertEquals("1111111111_10101_111111111".replaceAll("_", ""), BitBytes.toPaddedString(output));

        // single bit set
        output = new byte[] { (byte) 0b0000_0000, (byte) 0b0000_0000, (byte) 0b0000_0000};
        BitBytes.output(output, 12, 1, 1);

        Assert.assertEquals("000000000000_1_00000000000".replaceAll("_", ""), BitBytes.toPaddedString(output));

        // set all bits
        output = new byte[] { (byte) 0b0000_0000, (byte) 0b0000_0000, (byte) 0b0000_0000};
        BitBytes.output(output, 0, 0b1111_1111_1111_1111_1111_1111, 24);

        Assert.assertEquals("1111_1111_1111_1111_1111_1111".replaceAll("_", ""), BitBytes.toPaddedString(output));

        // Long.MAX_VALUE
        // TODO last bit missing.
        output = new byte[9];
        BitBytes.output(output, 3, Long.MAX_VALUE, 64);
        Assert.assertEquals("0000_11111111_11111111_11111111_11111111_11111111_11111111_11111111_11111110_0000".replaceAll("_", ""), BitBytes.toPaddedString(output));

        // TODO negative values

    }


    @Test
    public void testInput() {

        // middle of byte, overlapping into next byte, leading 1
        byte[] input = { (byte) 0b0000_0000, (byte) 0b0000_1101, (byte) 0b1011_1100};

        long value = BitBytes.input(input, 1, 4, 10);

        Assert.assertEquals("1101101111", Long.toBinaryString(value));

        // middle of byte, overlapping into next byte, leading 1
        input = new byte[] { (byte) 0b0000_0000, (byte) 0b0000_0101, (byte) 0b1011_1100};

        value = BitBytes.input(input, 1, 4, 10);

        Assert.assertEquals("101101111", Long.toBinaryString(value));

        // middle of byte
        input = new byte[] { (byte) 0b0000_1111, (byte) 0b1111_0101, (byte) 0b1011_1111};

        value = BitBytes.input(input, 1, 2, 3);

        Assert.assertEquals("110", Long.toBinaryString(value));

        // single bit

        input = new byte[] { (byte) 0b0000_0000, (byte) 0b0000_0101, (byte) 0b1111_1100};

        value = BitBytes.input(input, 17, 1);

        Assert.assertEquals("1", Long.toBinaryString(value));

        value = BitBytes.input(input, 8, 1);
        Assert.assertEquals("0", Long.toBinaryString(value));

        // TODO entire long, negative values
    }
}
