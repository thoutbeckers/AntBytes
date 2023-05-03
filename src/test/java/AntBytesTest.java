import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import houtbecke.rs.antbytes.AntBytes;
import houtbecke.rs.antbytes.AntBytesUtil;
import houtbecke.rs.antbytes.Dynamic;
import houtbecke.rs.antbytes.Flag;
import houtbecke.rs.antbytes.LSBS16BIT;
import houtbecke.rs.antbytes.LSBS24BIT;
import houtbecke.rs.antbytes.LSBS32BIT;
import houtbecke.rs.antbytes.LSBU16BIT;
import houtbecke.rs.antbytes.LSBU24BIT;
import houtbecke.rs.antbytes.LSBU32BIT;
import houtbecke.rs.antbytes.LSBUXBIT;
import houtbecke.rs.antbytes.Page;
import houtbecke.rs.antbytes.Required;
import houtbecke.rs.antbytes.S16BIT;
import houtbecke.rs.antbytes.S32BIT;
import houtbecke.rs.antbytes.S8BIT;
import houtbecke.rs.antbytes.SXBIT;
import houtbecke.rs.antbytes.U16BIT;
import houtbecke.rs.antbytes.U24BIT;
import houtbecke.rs.antbytes.U32BIT;
import houtbecke.rs.antbytes.U8BIT;
import houtbecke.rs.antbytes.UXBIT;

public class AntBytesTest {

    public static class TestAntMessage {
        @U32BIT(4)
        public long four;
        @U16BIT(2)
        protected int two;
        @U8BIT(1)
        int one;
        @Page(123)
        private int page;

        public TestAntMessage() { }

    }

    public static class TestAntBitMessage {
        @U16BIT(value = 2, startBit = 4)
        protected int two;
        @U8BIT(value = 1, startBit = 3)
        int one;
        @UXBIT(value = 2, startBit = 3)
        long bit;
        @UXBIT(value = 4, startBit = 4, bitLength = 2)
        int bits;
        @Page(123)
        private int page;

        public TestAntBitMessage() { }
    }

    public static class TestRequiredOne {
        @U32BIT(4)
        public long four;
        @U16BIT(2)
        protected int two;
        @Required(1)
        @U8BIT(1)
        int one;
        @Page(4)
        private int page;

        public TestRequiredOne() { }

    }

    public static class TestRequiredTwo {
        @U32BIT(4)
        public long four;
        @U16BIT(2)
        protected int two;
        @U8BIT(1)
        @Required(2)
        int one;
        @Page(4)
        private int page;

        public TestRequiredTwo() { }

    }

    public static class TestRequiredThree {
        @Required(4)
        @U32BIT(4)
        public long four;
        @Required(2)
        @U16BIT(2)
        protected int two;
        @Required(3)
        @UXBIT(value = 1, startBit = 0, bitLength = 4)
        int bits;
        @Page(4)
        private int page;

        public TestRequiredThree() { }

    }

    public static class TestSignedAntMessage {
        @S32BIT(4)
        public long four;
        @S16BIT(2)
        protected int two;
        @S8BIT(1)
        int one;
        @Page(123)
        private int page = 123;

        public TestSignedAntMessage() { }
    }

    public static class TestSignedAntMessage2 {
        @SXBIT(value = 4, startBit = 0, bitLength = 32)
        public long four;
        @SXBIT(value = 2, startBit = 0, bitLength = 16)
        protected int two;
        @SXBIT(value = 1, startBit = 4, bitLength = 4)
        int one;
        @Page(123)
        private int page = 123;

        public TestSignedAntMessage2() { }
    }

    public static class TestLSBMessage {
        @LSBU32BIT(4)
        public long four;
        @LSBUXBIT(value = 1, startBit = 0, bitLength = 1)
        protected int one;
        @LSBU16BIT(2)
        protected int two;
        @Page(123)
        private int page = 123;

        public TestLSBMessage() { }

    }

    public static class TestLSBMessage2 {
        @LSBUXBIT(value = 1, startBit = 4, bitLength = 12)
        protected int one;
        @Page(123)
        private int page = 123;

        public TestLSBMessage2() { }
        ;

    }

    public static class TestFlagMessage {
        @Flag(0)
        private boolean flag0;
        @Flag(1)
        private boolean flag1;
        @Flag(2)
        private boolean flag2;
        @Flag(7)
        private boolean flag7;
        @Flag(8)
        private boolean flag8;
        @Flag(value = 0, startByte = 2)
        private boolean flag16;

        public TestFlagMessage() { }

    }

    public static class TestFlagDynamicMessage {
        @Flag(0)
        private boolean flag0;
        @Flag(1)
        private boolean flag1;
        @Flag(2)
        private boolean flag2;
        @Dynamic(value = 0, order = 0)
        @U8BIT(1)
        private int byte0;
        @Dynamic(value = 2, order = 2)
        @U32BIT(1)
        private int byte2;
        @Dynamic(value = 1, order = 1)
        @U16BIT(1)
        private int abyte1;

        public TestFlagDynamicMessage() { }

    }

    public static class TestFlagDynamicMessage2 {
        @Flag(0)
        private boolean flag0;
        @Flag(1)
        private boolean flag1;
        @Flag(2)
        private boolean flag2;
        @Dynamic(value = 0, order = 0)
        @S8BIT(1)
        private int byte0;
        @Dynamic(value = 1, order = 1, inverse = true)
        @S16BIT(1)
        private int byte1;
        @Dynamic(value = 2, order = 2)
        @S32BIT(1)
        private int byte2;

        public TestFlagDynamicMessage2() { }
    }

    public static class TestFlagDynamicMessage3 {
        @Flag(0)
        private boolean flag0;
        @Flag(1)
        private boolean flag1;
        @Flag(2)
        private boolean flag2;
        @Dynamic(value = 0, order = 0)
        @S8BIT(1)
        private int byte0;
        @Dynamic(value = 1, order = 1)
        @LSBU16BIT(1)
        private int byte1;
        @Dynamic(value = 2, order = 2)
        @LSBU32BIT(1)
        private int byte2;

        public TestFlagDynamicMessage3() { }
    }

    public static class TestFlagDynamicMessage24 {
        @Flag(0)
        private boolean flag0;
        @Flag(1)
        private boolean flag1;
        @Dynamic(value = 0, order = 0)
        @U24BIT(1)
        private int byte0;
        @Dynamic(value = 1, order = 1)
        @LSBU24BIT(1)
        private int byte1;

        public TestFlagDynamicMessage24() { }

    }

    public static class TestFlagDynamicMessageSigned {
        @Flag(0)
        private boolean flag0;
        @Flag(1)
        private boolean flag1;
        @Dynamic(value = 0, order = 0)
        @LSBS16BIT(1)
        private int byte0;
        @Dynamic(value = 1, order = 1)
        @LSBS24BIT(1)
        private int byte1;

        public TestFlagDynamicMessageSigned() { }
    }

    public static class TestFlagDynamicMessageSigned2 {
        @Flag(0)
        private boolean flag0;
        @Dynamic(value = 0, order = 0)
        @LSBS32BIT(1)
        private int byte0;

        public TestFlagDynamicMessageSigned2() { }

    }

    public static class TestLSBSMessage {
        @LSBS32BIT(3)
        public long three;
        @LSBS16BIT(1)
        protected int one;
        @LSBS24BIT(7)
        protected int seven;
        @Page(123)
        private int page = 123;

        public TestLSBSMessage() { }
    }
    final static byte[] lowBytes = {123, 1, 0, 2, 0, 0, 0, 4};
    final static byte[] lowBytesIncomplete = {123, 1, 0, 2, 0};
    final static byte[] highBytes = {123, -1, -1, -1, -1, -1, -1, -1};
    final static byte[] lowBytesSigned = {123, -1, -1, -2, -1, -1, -1, -4};
    final static byte[] lowBytesSigned2 = {123, (byte) 0xF, -1, -2, -1, -1, -1, -4};
    final static byte[] lowLSBBytes = {123, 1, 2, 0, 4, 0, 0, 0};
    final static byte[] lowLSBBytes2 = {123, (byte) 0b11110000, (byte) 0b11111111, 0, 0, 0, 0, 0};
    final static byte[] flagBytes = {(byte) 0b10000011, (byte) 0b00000001, (byte) 0b00000001, 0, 0, 0, 0, 0};
    final static byte[] dynamicBytes1 = {(byte) 0b00000111, 1, 0, 2, 0, 0, 0, 3};
    final static byte[] dynamicBytes2 = {(byte) 0b00000100, 0, 0, 0, 3, 0, 0, 0};
    final static byte[] dynamicBytes3 = {(byte) 0b00000111, 1, 2, 0, 3, 0, 0, 0};
    final static byte[] dynamicBytes4 = {(byte) 0b00000101, 1, 0, 2, 0, 0, 0, 3};
    final static byte[] dynamicBytes24 = {(byte) 0b00000011, 0, 0, 1, 2, 0, 0, 0};
    final static byte[] dynamicBytes24False = {0, 0, 0, 0, 0, 0, 0, 0};
    final static byte[] dynamicSignedBytes = {(byte) 0b00000011, 1, 0, 2, 0, 0, 0, 0};
    final static byte[] dynamicSignedBytes2 = {(byte) 0b0000001, 1, 0, 0, 0, 0, 0, 0};
    final static byte[] lowLSBSBytes = {123, -1, -1, -3, -1, -1, -1, -7, -1, -1};
    final static byte[] lowLSBSBytesIncomplete = {123, -1, -1, -3, -1, -1, -1};
    final static byte[] noBytes = {0, 0, 0, 0, 0, 0, 0, 0};
    final static byte[] requiredOneBytes = {4, 1, 0, 2, 0, 0, 0, 4};
    final static byte[] requiredTwoBytes = {4, 2, 0, 2, 0, 0, 0, 4};
    final static byte[] requiredThreeBytes = {4, 48, 0, 2, 0, 0, 0, 4};
    final static byte[] requiredFourBytes = {4, 48, 0, 0, 0, 0, 0, 4};
    final static byte[] requiredFiveBytes = {4, 48, 0, 2, 0, 0, 0, 0};
    final static byte[] requiredSixBytes = {4, 0, 0, 2, 0, 0, 0, 4};
    // 129 = 10000001
    final static byte[] bitBytes = {123, 0b000_10000, 0b001_1_1111, (byte) 0b1111_1111, (byte) 0b1111_10_00, 0, 0, 0};
    AntBytes impl = AntBytesUtil.getInstance();

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
    public void fromBytesLowInComplete() {
        TestAntMessage message = impl.instanceFromAntBytes(TestAntMessage.class, lowBytesIncomplete);
        assertNotNull(message);
        assertEquals(1, message.one);
        assertEquals(2, message.two);
        assertEquals(0, message.four);
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
    public void toBytesSignedLSB() {
        TestLSBSMessage message = new TestLSBSMessage();
        message.one = -1;
        message.three = -3;
        message.seven = -7;
        byte[] antBytes = impl.toAntBytes(message, 10);

        assertArrayEquals(lowLSBSBytes, antBytes);
    }

    @Test
    public void fromBytesSignedLSB() {
        TestLSBSMessage message = impl.instanceFromAntBytes(TestLSBSMessage.class, lowLSBSBytes);
        assertEquals(-1, message.one);
        assertEquals(-3, message.three);
        assertEquals(-7, message.seven);
    }

    @Test
    public void fromBytesSignedLSBIncomplete() {
        TestLSBSMessage message = impl.instanceFromAntBytes(TestLSBSMessage.class, lowLSBSBytesIncomplete);
        assertEquals(-1, message.one);
        assertEquals(-3, message.three);
        assertEquals(0, message.seven);
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

    @Test
    public void fromBitBytes() {
        TestAntBitMessage message = impl.instanceFromAntBytes(TestAntBitMessage.class, bitBytes);
        assertEquals(123, message.page);
        assertEquals(129, message.one);
        assertEquals(1, message.bit);
        assertEquals(0b10, message.bits);
        assertEquals(65535, message.two);

    }

    @Test
    public void toBitBytes() {
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

        Object message = impl.fromAntBytes(lowBytes);
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

        Object message = impl.fromAntBytes(requiredOneBytes);
        assertNotNull(message);
        assertTrue(message instanceof TestRequiredOne);

        Object message2 = impl.fromAntBytes(requiredTwoBytes);
        assertNotNull(message2);
        assertTrue(message2 instanceof TestRequiredTwo);

        Object message3 = impl.fromAntBytes(requiredThreeBytes);
        assertNotNull(message3);
        assertTrue(message3 instanceof TestRequiredThree);

        Object message4 = impl.fromAntBytes(requiredFourBytes);
        assertNull(message4);

        Object message5 = impl.fromAntBytes(requiredFiveBytes);
        assertNull(message5);

        Object message6 = impl.fromAntBytes(requiredSixBytes);
        assertNull(message6);
    }

    @Test
    public void testRequired() {
        TestRequiredOne object1 = new TestRequiredOne();
        byte[] antBytes = impl.toAntBytes(object1);

        TestRequiredOne object2 = impl.instanceFromAntBytes(TestRequiredOne.class, antBytes);
        assertEquals(4, object2.page);
        assertEquals(1, object2.one);

        TestRequiredTwo object3 = new TestRequiredTwo();
        antBytes = impl.toAntBytes(object3);

        TestRequiredTwo object4 = impl.instanceFromAntBytes(TestRequiredTwo.class, antBytes);
        assertEquals(4, object4.page);
        assertEquals(2, object4.one);
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

    @Test
    public void fromFlagBytes() {

        TestFlagMessage message = impl.instanceFromAntBytes(TestFlagMessage.class, flagBytes);

        assertEquals(true, message.flag0);
        assertEquals(true, message.flag1);
        assertEquals(false, message.flag2);
        assertEquals(true, message.flag7);
        assertEquals(true, message.flag8);
        assertEquals(true, message.flag16);
    }

    @Test
    public void toFlagBytes() {

        TestFlagMessage testFlagMessage = new TestFlagMessage();

        testFlagMessage.flag0 = true;
        testFlagMessage.flag1 = true;
        testFlagMessage.flag2 = false;
        testFlagMessage.flag7 = true;
        testFlagMessage.flag8 = true;
        testFlagMessage.flag16 = true;

        byte[] antBytes = impl.toAntBytes(testFlagMessage, 8);

        assertArrayEquals(flagBytes, antBytes);
    }

    @Test
    public void fromDynamicBytes() {

        TestFlagDynamicMessage message = impl.instanceFromAntBytes(TestFlagDynamicMessage.class, dynamicBytes1);

        assertEquals(true, message.flag0);
        assertEquals(true, message.flag1);
        assertEquals(true, message.flag2);
        assertEquals(1, message.byte0);
        assertEquals(2, message.abyte1);
        assertEquals(3, message.byte2);

        TestFlagDynamicMessage message2 = impl.instanceFromAntBytes(TestFlagDynamicMessage.class, dynamicBytes2);

        assertEquals(false, message2.flag0);
        assertEquals(false, message2.flag1);
        assertEquals(true, message2.flag2);
        assertEquals(0, message2.byte0);
        assertEquals(0, message2.abyte1);
        assertEquals(3, message2.byte2);

        TestFlagDynamicMessage2 message3 = impl.instanceFromAntBytes(TestFlagDynamicMessage2.class, dynamicBytes4);

        assertEquals(true, message3.flag0);
        assertEquals(false, message3.flag1);
        assertEquals(true, message3.flag2);
        assertEquals(1, message3.byte0);
        assertEquals(2, message3.byte1);
        assertEquals(3, message3.byte2);

        TestFlagDynamicMessage3 message4 = impl.instanceFromAntBytes(TestFlagDynamicMessage3.class, dynamicBytes3);

        assertEquals(true, message4.flag0);
        assertEquals(true, message4.flag1);
        assertEquals(true, message4.flag2);
        assertEquals(1, message4.byte0);
        assertEquals(2, message4.byte1);
        assertEquals(3, message4.byte2);

        TestFlagDynamicMessage24 message5 = impl.instanceFromAntBytes(TestFlagDynamicMessage24.class, dynamicBytes24);
        assertEquals(true, message5.flag0);
        assertEquals(true, message5.flag1);
        assertEquals(1, message5.byte0);
        assertEquals(2, message5.byte1);

        TestFlagDynamicMessage24 message6 = impl.instanceFromAntBytes(TestFlagDynamicMessage24.class, dynamicBytes24False);
        assertEquals(false, message6.flag0);
        assertEquals(false, message6.flag1);
        assertEquals(0, message6.byte0);
        assertEquals(0, message6.byte1);

        TestFlagDynamicMessageSigned message7 = impl.instanceFromAntBytes(TestFlagDynamicMessageSigned.class, dynamicSignedBytes);
        assertEquals(true, message7.flag0);
        assertEquals(true, message7.flag1);
        assertEquals(1, message7.byte0);
        assertEquals(2, message7.byte1);

        TestFlagDynamicMessageSigned2 message8 = impl.instanceFromAntBytes(TestFlagDynamicMessageSigned2.class, dynamicSignedBytes2);
        assertEquals(true, message8.flag0);
        assertEquals(1, message8.byte0);

    }

    @Test
    public void toDynamicBytes() {

        TestFlagDynamicMessage dynamicMessage1 = new TestFlagDynamicMessage();

        dynamicMessage1.flag0 = true;
        dynamicMessage1.flag1 = true;
        dynamicMessage1.flag2 = true;
        dynamicMessage1.byte0 = 1;
        dynamicMessage1.abyte1 = 2;
        dynamicMessage1.byte2 = 3;

        byte[] antBytes1 = impl.toAntBytes(dynamicMessage1, 8);

        assertArrayEquals(dynamicBytes1, antBytes1);

        TestFlagDynamicMessage dynamicMessage2 = new TestFlagDynamicMessage();

        dynamicMessage2.flag0 = false;
        dynamicMessage2.flag1 = false;
        dynamicMessage2.flag2 = true;
        dynamicMessage2.byte0 = 0;
        dynamicMessage2.abyte1 = 0;
        dynamicMessage2.byte2 = 3;

        byte[] antBytes2 = impl.toAntBytes(dynamicMessage2, 8);

        assertArrayEquals(dynamicBytes2, antBytes2);

        TestFlagDynamicMessage3 dynamicMessage3 = new TestFlagDynamicMessage3();

        dynamicMessage3.flag0 = true;
        dynamicMessage3.flag1 = true;
        dynamicMessage3.flag2 = true;
        dynamicMessage3.byte0 = 1;
        dynamicMessage3.byte1 = 2;
        dynamicMessage3.byte2 = 3;

        byte[] antBytes3 = impl.toAntBytes(dynamicMessage3, 8);

        assertArrayEquals(dynamicBytes3, antBytes3);

        TestFlagDynamicMessage2 dynamicMessage4 = new TestFlagDynamicMessage2();

        dynamicMessage4.flag0 = true;
        dynamicMessage4.flag1 = false;
        dynamicMessage4.flag2 = true;
        dynamicMessage4.byte0 = 1;
        dynamicMessage4.byte1 = 2;
        dynamicMessage4.byte2 = 3;

        byte[] antBytes4 = impl.toAntBytes(dynamicMessage4, 8);

        assertArrayEquals(dynamicBytes4, antBytes4);

        TestFlagDynamicMessage24 dynamicMessage24 = new TestFlagDynamicMessage24();
        dynamicMessage24.flag0 = true;
        dynamicMessage24.flag1 = true;
        dynamicMessage24.byte0 = 1;
        dynamicMessage24.byte1 = 2;

        byte[] antBytes24 = impl.toAntBytes(dynamicMessage24, 8);
        assertArrayEquals(dynamicBytes24, antBytes24);

        TestFlagDynamicMessage24 dynamicMessage24False = new TestFlagDynamicMessage24();
        dynamicMessage24False.flag0 = false;
        dynamicMessage24False.flag1 = false;
        dynamicMessage24False.byte0 = 0;
        dynamicMessage24False.byte1 = 0;

        byte[] antBytes24false = impl.toAntBytes(dynamicMessage24False, 8);
        assertArrayEquals(dynamicBytes24False, antBytes24false);

        TestFlagDynamicMessageSigned dynamicMessageSigned = new TestFlagDynamicMessageSigned();
        dynamicMessageSigned.flag0 = true;
        dynamicMessageSigned.flag1 = true;
        dynamicMessageSigned.byte0 = 1;
        dynamicMessageSigned.byte1 = 2;
        byte[] antBytesSigned = impl.toAntBytes(dynamicMessageSigned, 8);
        assertArrayEquals(dynamicSignedBytes, antBytesSigned);

        TestFlagDynamicMessageSigned2 dynamicMessageSigned2 = new TestFlagDynamicMessageSigned2();
        dynamicMessageSigned2.flag0 = true;
        dynamicMessageSigned2.byte0 = 1;
        byte[] antBytesSigned2 = impl.toAntBytes(dynamicMessageSigned2, 8);
        assertArrayEquals(dynamicSignedBytes2, antBytesSigned2);
    }
}
