import org.junit.Test;

import java.util.Arrays;

import houtbecke.rs.antbytes.AntBytes;
import houtbecke.rs.antbytes.AntBytesImpl;
import houtbecke.rs.antbytes.AntBytesUtil;
import houtbecke.rs.antbytes.LSBU16BIT;
import houtbecke.rs.antbytes.LSBU32BIT;
import houtbecke.rs.antbytes.LSBUXBIT;
import houtbecke.rs.antbytes.Page;
import houtbecke.rs.antbytes.Required;
import houtbecke.rs.antbytes.S16BIT;
import houtbecke.rs.antbytes.S32BIT;
import houtbecke.rs.antbytes.S8BIT;
import houtbecke.rs.antbytes.SXBIT;
import houtbecke.rs.antbytes.U16BIT;
import houtbecke.rs.antbytes.U32BIT;
import houtbecke.rs.antbytes.U8BIT;
import houtbecke.rs.antbytes.UXBIT;

import static org.junit.Assert.*;

public class AntBytesTest  {

    public static class TestAntMessage {
        public TestAntMessage() {}

        @Page(123)
        private int page;

        @U8BIT(1)
        int one;

        @U16BIT(2)
        protected int two;

        @U32BIT(4)
        public long four;
        
    }

    public static class TestAntBitMessage {
        public TestAntBitMessage() {}

        @Page(123)
        private int page;

        @U8BIT(value = 1, startBit = 3)
        int one;

        @UXBIT(value = 2, startBit = 3)
        long bit;

        @U16BIT(value = 2, startBit = 4)
        protected int two;

        @UXBIT(value = 4, startBit = 4, bitLength = 2)
        int bits;
    }


    public static class TestRequiredOne{
        public TestRequiredOne() {}

        @Page(4)
        private int page;

        @Required(1)
        @U8BIT(1)
        int one;

        @U16BIT(2)
        protected int two;

        @U32BIT(4)
        public long four;

    }

    public static class TestRequiredTwo {
        public TestRequiredTwo() {}

        @Page(4)
        private int page;

        @Required(2)
        @U8BIT(1)
        int one;

        @U16BIT(2)
        protected int two;

        @U32BIT(4)
        public long four;

    }

    public static class TestRequiredThree {
        public TestRequiredThree() {}
        @Page(4)
        private int page;

        @Required(3)
        @UXBIT(value = 1, startBit = 0, bitLength = 4)
        int bits;


        @Required(2)
        @U16BIT(2)
        protected int two;

        @Required(4)
        @U32BIT(4)
        public long four;

    }

    public static class TestSignedAntMessage {
        public TestSignedAntMessage() {}

        @Page(123)
        private int page=123;

        @S8BIT(1)
        int one;

        @S16BIT(2)
        protected int two;

        @S32BIT(4)
        public long four;
    }

    public static class TestSignedAntMessage2{
        public TestSignedAntMessage2() {}

        @Page(123)
        private int page=123;

        @SXBIT(value = 1, startBit = 4, bitLength = 4)
        int one;

        @SXBIT(value = 2, startBit = 0, bitLength = 16)
        protected int two;

        @SXBIT(value = 4, startBit = 0, bitLength = 32)
        public long four;
    }



    public static class TestLSBMessage {
        public TestLSBMessage() {}

        @Page(123)
        private int page = 123;

        @LSBUXBIT(value = 1, startBit = 7, bitLength = 1)
        protected int one;

        @LSBU16BIT(2)
        protected int two;

        @LSBU32BIT(4)
        public long four;

    }

    public static class TestLSBMessage2 {
        public TestLSBMessage2() {}

        @Page(123)
        private int page = 123;

        @LSBUXBIT(value = 1, startBit = 4, bitLength = 12)
        protected int one;
;

    }


    AntBytes impl = AntBytesUtil.getInstance();

    final static byte[] lowBytes = {123, 1, 0, 2, 0, 0, 0, 4};
    final static byte[] highBytes = {123, -1, -1, -1, -1, -1, -1, -1};
    final static byte[] lowBytesSigned = {123, -1, -1, -2, -1, -1, -1, -4};
    final static byte[] lowBytesSigned2 = {123, (byte)0xF, -1, -2, -1, -1, -1, -4};
    final static byte[] lowLSBBytes = {123, 1, 2, 0, 4, 0, 0,0};
    final static byte[] lowLSBBytes2 = {123, (byte)0b11110000,(byte)0b11111111,0, 0, 0, 0, 0};


    final static byte[] noBytes = {0, 0, 0, 0, 0, 0, 0, 0};
    final static byte[] requiredOneBytes = {4, 1, 0, 2, 0, 0, 0, 4};
    final static byte[] requiredTwoBytes = {4, 2, 0, 2, 0, 0, 0, 4};
    final static byte[] requiredThreeBytes = {4, 48, 0, 2, 0, 0, 0, 4};
    final static byte[] requiredFourBytes = {4, 48, 0, 0, 0, 0, 0, 4};
    final static byte[] requiredFiveBytes = {4, 48, 0, 2, 0, 0, 0, 0};
    final static byte[] requiredSixBytes = {4, 0, 0, 2, 0, 0, 0, 4};


    @Test
    public void toBytesLow() {

        TestAntMessage lowTest = new TestAntMessage();
        lowTest.one = 1;
        lowTest.two = 2;
        lowTest.four = 4L;

        byte[] antBytes = impl.toAntBytes(lowTest);

        assertEquals(lowBytes[0], antBytes[0]);
        assertEquals(lowBytes[1], antBytes[1]);
        assertEquals(lowBytes[2], antBytes[2]);
        assertEquals(lowBytes[3], antBytes[3]);
        assertEquals(lowBytes[4], antBytes[4]);
        assertEquals(lowBytes[5], antBytes[5]);
        assertEquals(lowBytes[6], antBytes[6]);
        assertEquals(lowBytes[7], antBytes[7]);
    }

    @Test
    public void fromBytesLow() {
        TestAntMessage message = impl.instanceFromAntBytes(TestAntMessage.class, lowBytes);
        assertNotNull(message);
        assertEquals(1, message.one);
        assertEquals(2, message.two);
        assertEquals(4, message.four);
        assertEquals(123, message.page);
    }

    @Test
    public void toBytesHigh() {

        TestAntMessage highTest = new TestAntMessage();
        highTest.one = 255;
        highTest.two = 256 * 256 - 1;
        highTest.four = -1;

        byte[] antBytes = impl.toAntBytes(highTest);

        assertEquals(highBytes[0], antBytes[0]);
        assertEquals(highBytes[1], antBytes[1]);
        assertEquals(highBytes[2], antBytes[2]);
        assertEquals(highBytes[3], antBytes[3]);
        assertEquals(highBytes[4], antBytes[4]);
        assertEquals(highBytes[5], antBytes[5]);
        assertEquals(highBytes[6], antBytes[6]);
        assertEquals(highBytes[7], antBytes[7]);
    }


    @Test
    public void fromBytesHigh() {
        TestAntMessage message = impl.instanceFromAntBytes(TestAntMessage.class, highBytes);

        assertEquals(255, message.one);
        assertEquals(256 * 256 - 1, message.two);
        assertEquals(-1L, message.four);
    }

    @Test
    public void toBytesLowSigned() {

        TestSignedAntMessage lowTest = new TestSignedAntMessage();
        lowTest.one = -1;
        lowTest.two = -2;
        lowTest.four = -4L;

        byte[] antBytes = impl.toAntBytes(lowTest);

        assertEquals(lowBytesSigned[0], antBytes[0]);
        assertEquals(lowBytesSigned[1], antBytes[1]);
        assertEquals(lowBytesSigned[2], antBytes[2]);
        assertEquals(lowBytesSigned[3], antBytes[3]);
        assertEquals(lowBytesSigned[4], antBytes[4]);
        assertEquals(lowBytesSigned[5], antBytes[5]);
        assertEquals(lowBytesSigned[6], antBytes[6]);
        assertEquals(lowBytesSigned[7], antBytes[7]);
    }


    @Test
    public void fromBytesLowSigned() {
        TestSignedAntMessage message = impl.instanceFromAntBytes(TestSignedAntMessage.class, lowBytesSigned);

        assertEquals(-1, message.one);
        assertEquals(-2, message.two);
        assertEquals(-4, message.four);
    }


    @Test
    public void toBytesLowSigned2() {

        TestSignedAntMessage2 lowTest = new TestSignedAntMessage2();
        lowTest.one = -1;
        lowTest.two = -2;
        lowTest.four = -4L;

        byte[] antBytes = impl.toAntBytes(lowTest);

        assertEquals(lowBytesSigned2[0], antBytes[0]);
        assertEquals(lowBytesSigned2[1], antBytes[1]);
        assertEquals(lowBytesSigned2[2], antBytes[2]);
        assertEquals(lowBytesSigned2[3], antBytes[3]);
        assertEquals(lowBytesSigned2[4], antBytes[4]);
        assertEquals(lowBytesSigned2[5], antBytes[5]);
        assertEquals(lowBytesSigned2[6], antBytes[6]);
        assertEquals(lowBytesSigned2[7], antBytes[7]);
    }

    @Test
    public void fromBytesLowSigned2() {

        TestSignedAntMessage2 message = impl.instanceFromAntBytes(TestSignedAntMessage2.class, lowBytesSigned2);

        assertEquals(-1, message.one);
        assertEquals(-2, message.two);
        assertEquals(-4, message.four);

    }

    @Test
    public void toBytesSignedHigh() {

        TestSignedAntMessage highTest = new TestSignedAntMessage();
        highTest.one = -1;
        highTest.two = -1;
        highTest.four = -1;

        byte[] antBytes = impl.toAntBytes(highTest);

        assertEquals(highBytes[0], antBytes[0]);
        assertEquals(highBytes[1], antBytes[1]);
        assertEquals(highBytes[2], antBytes[2]);
        assertEquals(highBytes[3], antBytes[3]);
        assertEquals(highBytes[4], antBytes[4]);
        assertEquals(highBytes[5], antBytes[5]);
        assertEquals(highBytes[6], antBytes[6]);
        assertEquals(highBytes[7], antBytes[7]);
    }

    @Test
    public void fromBytesSignedHigh() {
        TestSignedAntMessage message = impl.instanceFromAntBytes(TestSignedAntMessage.class, highBytes);

        assertEquals(-1, message.one);
        assertEquals(-1, message.two);
        assertEquals(-1, message.four);
    }





    @Test
    public void fromBytesSigned() {
        TestSignedAntMessage message = impl.instanceFromAntBytes(TestSignedAntMessage.class, lowBytes);
        assertNotNull(message);
        assertEquals(1, message.one);
        assertEquals(2, message.two);
        assertEquals(4, message.four);
        assertEquals(123, message.page);




        TestLSBMessage message2 = impl.instanceFromAntBytes(TestLSBMessage.class, lowLSBBytes);
        assertNotNull(message2);
        assertEquals(1, message2.one);
        assertEquals(2, message2.two);
        assertEquals(4, message2.four);
        assertEquals(123, message2.page);


        TestLSBMessage2 message3 = impl.instanceFromAntBytes(TestLSBMessage2.class, lowLSBBytes2);
        assertNotNull(message3);
        assertEquals(4095, message3.one);


    }

    // 129 = 10000001
    final static byte[] bitBytes = {123, 0b000_10000, 0b001_1_1111, (byte)0b1111_1111, (byte)0b1111_10_00, 0, 0, 0};

    @Test
    public void fromBitBytes() {
        TestAntBitMessage message = impl.instanceFromAntBytes(TestAntBitMessage.class, bitBytes);
        assertEquals(123, message.page);
        assertEquals(129, message.one);
        assertEquals(1, message.bit);
        assertEquals(0b10, message.bits);
        assertEquals(65535, message.two);

    }

    @Test public void toBitBytes() {
        TestAntBitMessage bitMessage = new TestAntBitMessage();
        bitMessage.one = 129;
        bitMessage.bit = 1;
        bitMessage.two = 65535;
        bitMessage.bits = 0b10;
        byte[] antBytes = impl.toAntBytes(bitMessage);


        assertEquals(bitBytes[0], antBytes[0]);
        assertEquals(bitBytes[1], antBytes[1]);
        assertEquals(bitBytes[2], antBytes[2]);
        assertEquals(bitBytes[3], antBytes[3]);
        assertEquals(bitBytes[4], antBytes[4]);
        assertEquals(bitBytes[5], antBytes[5]);
        assertEquals(bitBytes[6], antBytes[6]);
        assertEquals(bitBytes[7], antBytes[7]);

    }

    @Test
    public void registration() {
        impl.register(TestAntMessage.class);

        impl.register(Object.class); // no side effects

        Object message =  impl.fromAntBytes(lowBytes);
        assertNotNull(message);
        assertTrue(message instanceof TestAntMessage);

        message = impl.fromAntBytes(noBytes);
        assertNull(message);

    }


    @Test
    public void registrationRequired() {
        impl.register(TestRequiredOne.class);
        impl.register(TestRequiredTwo.class);
        impl.register(TestRequiredThree.class);


        Object message =  impl.fromAntBytes(requiredOneBytes);
        assertNotNull(message);
        assertTrue(message instanceof TestRequiredOne);

        Object message2 =  impl.fromAntBytes(requiredTwoBytes);
        assertNotNull(message2);
        assertTrue(message2 instanceof TestRequiredTwo);

        Object message3 =  impl.fromAntBytes(requiredThreeBytes);
        assertNotNull(message3);
        assertTrue(message3 instanceof TestRequiredThree);

        Object message4 =  impl.fromAntBytes(requiredFourBytes);
        assertNull(message4);

        Object message5 =  impl.fromAntBytes(requiredFiveBytes);
        assertNull(message5);

        Object message6 =  impl.fromAntBytes(requiredSixBytes);
        assertNull(message6);
    }
    @Test
    public void toLSBytesLow() {

        TestLSBMessage lowTest = new TestLSBMessage();
        lowTest.one = 1;
        lowTest.two = 2;
        lowTest.four = 4L;

        byte[] antBytes = impl.toAntBytes(lowTest);

        assertEquals(lowLSBBytes[0], antBytes[0]);
        assertEquals(lowLSBBytes[1], antBytes[1]);
        assertEquals(lowLSBBytes[2], antBytes[2]);
        assertEquals(lowLSBBytes[3], antBytes[3]);
        assertEquals(lowLSBBytes[4], antBytes[4]);
        assertEquals(lowLSBBytes[5], antBytes[5]);
        assertEquals(lowLSBBytes[6], antBytes[6]);
        assertEquals(lowLSBBytes[7], antBytes[7]);
    }



    @Test
    public void toLSBytesLow2() {

        TestLSBMessage2 lowTest = new TestLSBMessage2();

        lowTest.one = 4095;

        byte[] antBytes = impl.toAntBytes(lowTest);

        assertEquals(lowLSBBytes2[0], antBytes[0]);
        assertEquals(lowLSBBytes2[1], antBytes[1]);
        assertEquals(lowLSBBytes2[2], antBytes[2]);
        assertEquals(lowLSBBytes2[3], antBytes[3]);
        assertEquals(lowLSBBytes2[4], antBytes[4]);
        assertEquals(lowLSBBytes2[5], antBytes[5]);
        assertEquals(lowLSBBytes2[6], antBytes[6]);
        assertEquals(lowLSBBytes2[7], antBytes[7]);
    }
}
