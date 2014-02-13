import org.junit.Test;

import java.util.Arrays;

import houtbecke.rs.antbytes.AntBytes;
import houtbecke.rs.antbytes.AntBytesImpl;
import houtbecke.rs.antbytes.AntBytesUtil;
import houtbecke.rs.antbytes.Page;
import houtbecke.rs.antbytes.U16BIT;
import houtbecke.rs.antbytes.U32BIT;
import houtbecke.rs.antbytes.U8BIT;

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

    AntBytes impl = AntBytesUtil.getInstance();

    final static byte[] lowBytes = {123, 1, 0, 2, 0, 0, 0, 4};
    final static byte[] highBytes = {123, -1, -1, -1, -1, -1, -1, -1};
    final static byte[] noBytes = {0, 0, 0, 0, 0, 0, 0, 0};

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

    @Test
    public void registration() {
        impl.register(TestAntMessage.class);

        impl.register(Object.class); // no side effects

        Object message =  impl.fromAntBytes(lowBytes);
        assertNotNull(message);
        assertTrue(message instanceof TestAntMessage);

        message = impl.fromAntBytes(noBytes);
        assertNull(message);

        assertNull(message);
    }

}
