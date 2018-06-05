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


        // start values with one
        output = new byte[1] ;
        ///////////////////////// clamped | the ten bits |
        BitBytes.output(output,0, -1, 8);
        Assert.assertEquals("1111_1111".replaceAll("_", ""), BitBytes.toPaddedString(output));

        // start values with one
        output = new byte[1] ;
        ///////////////////////// clamped | the ten bits |
        BitBytes.output(output,0, -2, 8);
        Assert.assertEquals("1111_1110".replaceAll("_", ""), BitBytes.toPaddedString(output));

        // start values with one
        output = new byte[2] ;
        ///////////////////////// clamped | the ten bits |
        BitBytes.output(output,0, -1, 16);
        Assert.assertEquals("1111_1111_1111_1111".replaceAll("_", ""), BitBytes.toPaddedString(output));

        // start values with one
        output = new byte[2] ;
        ///////////////////////// clamped | the ten bits |
        BitBytes.output(output,0, -2, 16);
        Assert.assertEquals("1111_1111_1111_1110".replaceAll("_", ""), BitBytes.toPaddedString(output));


        output = new byte[3];
        BitBytes.output(output, 4, 3841, 12);
        Assert.assertEquals("00001111_00000001_00000000".replaceAll("_", ""), BitBytes.toPaddedString(output));


    }


    @Test
    public void testInput() {

        byte[] input = new byte[] { (byte) 0b1111_1111};
        long value = BitBytes.input(input,0 , 8,true);
        Assert.assertEquals(-1,value);


        input = new byte[] { (byte) 0b1111_1111,(byte) 0b1111_1111};
        value = BitBytes.input(input,0 , 16,true);
        Assert.assertEquals(-1,value);

        input = new byte[] { (byte) 0b1111_1111,(byte) 0b1111_1111,(byte) 0b1111_1111,(byte) 0b1111_1111};
        value = BitBytes.input(input,0 , 32,true);
        Assert.assertEquals(-1,value);

        input = new byte[] { (byte) 0b1111_1111,(byte) 0b1111_1110};
        value = BitBytes.input(input,0 , 16,true);
        Assert.assertEquals(-2,value);


        // middle of byte, overlapping into next byte, leading 1
        input = new byte[] { (byte) 0b0000_0000, (byte) 0b0000_1101, (byte) 0b1011_1100};

         value = BitBytes.input(input, 1, 4, 10);

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

        // TODO entire long



    }


    @org.junit.Test
    public void testInputLSB() {
        Assert.assertEquals(1,BitBytes.inputLSB( new byte[]{1, 0, 0,0, 0,0,0,0},0,8,false));
        Assert.assertEquals(-1,BitBytes.inputLSB( new byte[]{-1, 0, 0,0, 0,0,0,0},0,8,true));
        Assert.assertEquals(1,BitBytes.inputLSB( new byte[]{0, 1, 0,0, 0,0,0,0},8,8,false));
        Assert.assertEquals(1,BitBytes.inputLSB( new byte[]{0, 1, 0,0, 0,0,0,0},8,16,false));
        Assert.assertEquals(0xFFFF,BitBytes.inputLSB( new byte[]{(byte)0xFF, (byte) 0xFF, 0,0, 0,0,0,0},0,16,false));
        Assert.assertEquals(-1,BitBytes.inputLSB( new byte[]{(byte)0xFF, (byte) 0xFF, 0,0, 0,0,0,0},0,16,true));





        Assert.assertEquals(4095,BitBytes.inputLSB( new byte[]{(byte)0b11111111, (byte) 0b00001111, 0},0,12,false));
        Assert.assertEquals(4095,BitBytes.inputLSB( new byte[]{(byte)0b11110000, (byte) 0b11111111, 0},4,12,false));
        Assert.assertEquals(4095, BitBytes.inputLSB(new byte[]{0, (byte) 0b11110000, (byte) 0b11111111, 0}, 12, 12, false));

        Assert.assertEquals(1023, BitBytes.inputLSB(new byte[]{(byte) 0b11000000, (byte) 0b11111111, 0}, 6, 10, false));






        Assert.assertEquals(255,BitBytes.inputLSB( new byte[]{(byte)0b11111111, 0, 0},0,8,false));
        Assert.assertEquals(240,BitBytes.inputLSB( new byte[]{(byte)0b11110000, 0, 0},0,8,false));
        Assert.assertEquals(15,BitBytes.inputLSB( new byte[]{(byte)0b00001111, 0, 0},0,4,false));


        Assert.assertEquals(1, BitBytes.inputLSB(new byte[]{(byte) 0b00000001, 0, 0}, 0, 1, false));
        Assert.assertEquals(1,BitBytes.inputLSB(new byte[]{0, (byte) 0b00000001, 0, 0}, 8, 1, false));



        Assert.assertEquals(1,BitBytes.inputLSB( new byte[]{1, 0, 0,0, 0,0,0,0},0,12,false));

        Assert.assertEquals(1,BitBytes.inputLSB( new byte[]{0, 1, 0,0, 0,0,0,0},8,12,false));
        Assert.assertEquals(1,BitBytes.inputLSB( new byte[]{0, 1, (byte)0xF0,0, 0,0,0,0},8,12,false));
        Assert.assertEquals(3841,BitBytes.inputLSB( new byte[]{0, 1, (byte) 0xFF,0, 0,0,0,0},8,12,false));


        Assert.assertEquals(3841, BitBytes.inputLSB(new byte[]{(byte) 0b00010000, (byte) 0b11110000, (byte) 0b00000000}, 4, 12, false));
        Assert.assertEquals(3841,BitBytes.inputLSB(new byte[]{(byte) 0b01000000, (byte) 0b11000000, (byte) 0b00000011}, 6, 12, false));

        Assert.assertEquals(3841,BitBytes.inputLSB(new byte[]{(byte) 0x0, (byte) 0b01000000, (byte) 0b11000000, (byte) 0b00000011}, 14, 12, false));

        Assert.assertEquals(3841,BitBytes.inputLSB(new byte[]{(byte) 0x0, (byte) 0b01000000, (byte) 0b11000000, (byte) 0b00000011,(byte)0x0}, 14, 12, false));



        Assert.assertEquals(1,BitBytes.inputLSB(new byte[]{ (byte) 0b000_0001,(byte) 0x0, (byte) 0b11000000, (byte) 0b00000011,(byte)0x0}, 0, 16, false));

        Assert.assertEquals(1,BitBytes.inputLSB(new byte[]{ (byte) 0b001_0000,(byte) 0x0, (byte) 0b11000000, (byte) 0b00000011,(byte)0x0}, 4, 12, false));

        Assert.assertEquals(1,BitBytes.inputLSB(new byte[]{(byte) 0x0, (byte) 0b0000_0001, (byte) 0b11000000, (byte) 0b00000011,(byte)0x0}, 8, 4, false));




    }


    @org.junit.Test
    public void testOutputLSB() {
        byte[] output = new byte[2];
        BitBytes.outputLSB(output,0,1,16);
        Assert.assertEquals("00000001_00000000".replaceAll("_", ""), BitBytes.toPaddedString(output));
        BitBytes.outputLSB(output, 0, 0xFFFF, 16);
        Assert.assertEquals("11111111_11111111".replaceAll("_", ""), BitBytes.toPaddedString(output));
        BitBytes.outputLSB(output, 0, 256, 16);
        Assert.assertEquals("00000000_00000001".replaceAll("_", ""), BitBytes.toPaddedString(output));

        BitBytes.outputLSB(output, 0, -1, 16);
        Assert.assertEquals("11111111_11111111".replaceAll("_", ""), BitBytes.toPaddedString(output));

        output = new byte[2];
        BitBytes.outputLSB(output, 0, -1, 12);
        Assert.assertEquals("11111111_00001111".replaceAll("_", ""), BitBytes.toPaddedString(output));


        output = new byte[3];
        BitBytes.outputLSB(output, 8, 3841, 12);
        Assert.assertEquals("00000000_00000001_00001111".replaceAll("_", ""), BitBytes.toPaddedString(output));


        output = new byte[3];
        BitBytes.outputLSB(output, 13, 1, 1);
        Assert.assertEquals("00000000_00100000_00000000".replaceAll("_", ""), BitBytes.toPaddedString(output));

        output = new byte[3];
        BitBytes.outputLSB(output, 12, 1, 2);
        Assert.assertEquals("00000000_00010000_00000000".replaceAll("_", ""), BitBytes.toPaddedString(output));


        output = new byte[3];
        BitBytes.outputLSB(output, 12, 2, 2);
        Assert.assertEquals("00000000_00100000_00000000".replaceAll("_", ""), BitBytes.toPaddedString(output));


        output = new byte[3];
        BitBytes.outputLSB(output, 0, 1, 12);
        Assert.assertEquals("00000001_00000000_00000000".replaceAll("_", ""), BitBytes.toPaddedString(output));

        output = new byte[3];
        BitBytes.outputLSB(output, 0, 128, 12);
        Assert.assertEquals("10000000_00000000_00000000".replaceAll("_", ""), BitBytes.toPaddedString(output));

        output = new byte[3];
        BitBytes.outputLSB(output, 0, 2048, 12);
        Assert.assertEquals("00000000_00001000_00000000".replaceAll("_", ""), BitBytes.toPaddedString(output));

        output = new byte[3];
        BitBytes.outputLSB(output, 0, 4095, 12);
        Assert.assertEquals("11111111_00001111_00000000".replaceAll("_", ""), BitBytes.toPaddedString(output));

        output = new byte[3];
        BitBytes.outputLSB(output, 0, 3071, 12);
        Assert.assertEquals("11111111_00001011_00000000".replaceAll("_", ""), BitBytes.toPaddedString(output));



        output = new byte[3];
        BitBytes.outputLSB(output, 4, 4095, 12);
        Assert.assertEquals("11110000_11111111_00000000".replaceAll("_", ""), BitBytes.toPaddedString(output));


        output = new byte[3];
        BitBytes.outputLSB(output, 0, 2048, 12);
        Assert.assertEquals("00000000_00001000_00000000".replaceAll("_", ""), BitBytes.toPaddedString(output));



        output = new byte[3];
        BitBytes.outputLSB(output, 0, 3841, 12);
        Assert.assertEquals("00000001_00001111_00000000".replaceAll("_", ""), BitBytes.toPaddedString(output));


        output = new byte[3];
        BitBytes.outputLSB(output, 6, 3841, 12);
        Assert.assertEquals("01000000_11000000_00000011".replaceAll("_", ""), BitBytes.toPaddedString(output));
        output = new byte[3];
        BitBytes.outputLSB(output, 4, 3841, 12);
        Assert.assertEquals("00010000_11110000_00000000".replaceAll("_", ""), BitBytes.toPaddedString(output));



        output = new byte[1];
        BitBytes.outputLSB(output, 0, 1, 1);
        Assert.assertEquals("00000001".replaceAll("_", ""), BitBytes.toPaddedString(output));


        output = new byte[2];
        BitBytes.outputLSB(output, 8, 1, 1);
        Assert.assertEquals("00000000_00000001".replaceAll("_", ""), BitBytes.toPaddedString(output));


    }

}
