import org.junit.Test;

import java.util.Arrays;

import houtbecke.rs.antbytes.AntBytes;
import houtbecke.rs.antbytes.AntBytesImpl;
import houtbecke.rs.antbytes.AntBytesUtil;
import houtbecke.rs.antbytes.Page;
import houtbecke.rs.antbytes.Required;
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







    AntBytes impl = AntBytesUtil.getInstance();

    final static byte[] lowBytes = {123, 1, 0, 2, 0, 0, 0, 4};
    final static byte[] highBytes = {123, -1, -1, -1, -1, -1, -1, -1};
    final static byte[] noBytes = {0, 0, 0, 0, 0, 0, 0, 0};
    final static byte[] requiredOneBytes = {4, 1, 0, 2, 0, 0, 0, 4};
    final static byte[] requiredTwoBytes = {4, 2, 0, 2, 0, 0, 0, 4};


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
    public void fromBytes() {
        TestAntMessage message = impl.instanceFromAntBytes(TestAntMessage.class, lowBytes);
        assertNotNull(message);
        assertEquals(1, message.one);
        assertEquals(2, message.two);
        assertEquals(4, message.four);
        assertEquals(123, message.page);

        message = impl.instanceFromAntBytes(TestAntMessage.class, highBytes);

        assertEquals(255, message.one);
        assertEquals(256 * 256 - 1, message.two);
        assertEquals(-1L, message.four);


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


        Object message =  impl.fromAntBytes(requiredOneBytes);
        assertNotNull(message);
        assertTrue(message instanceof TestRequiredOne);

        Object message2 =  impl.fromAntBytes(requiredTwoBytes);
        assertNotNull(message2);
        assertTrue(message2 instanceof TestRequiredTwo);

    }

}
