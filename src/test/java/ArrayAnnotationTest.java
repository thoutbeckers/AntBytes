import org.junit.Test;
import static org.junit.Assert.*;
import houtbecke.rs.antbytes.*;

public class ArrayAnnotationTest {

    AntBytes impl = AntBytesUtil.getInstance();

    public static class TestArrayOnly {
        public TestArrayOnly() {}

        @Array
        @U8BIT
        public int[] arr;
    }

    public static class TestArrayCount {
        public TestArrayCount() {}

        @Array(2)
        @S8BIT
        private int[] byte0;
    }

    public static class TestEndlessArrayWithOtherData {
        public TestEndlessArrayWithOtherData() {}

        @Flag(0)
        private boolean flag0;

        @U8BIT(1)
        protected int one;

        @Array
        @LSBS24BIT(2)
        private int[] arr;
    }

    public static class TestArrayCountWithOtherData {
        public TestArrayCountWithOtherData() {}

        @Flag(0)
        private boolean flag0;

        @S8BIT(1)
        protected int one;

        @Array(3)
        @S8BIT(2)
        private int[] arr;

        @S8BIT(5)
        private int six;
    }

    final static byte[] lowBytes = {123, 1, 0, 2, 0, 0, 0, 4};

    @Test
    public void testArrayOnly() {
        TestArrayOnly atestArrayOnlyr = impl.instanceFromAntBytes(TestArrayOnly.class, lowBytes);
        assertEquals(lowBytes.length, atestArrayOnlyr.arr.length);
        assertEquals(lowBytes[0], atestArrayOnlyr.arr[0]);
        assertEquals(lowBytes[4], atestArrayOnlyr.arr[4]);
        assertEquals(lowBytes[6], atestArrayOnlyr.arr[6]);
        assertEquals(lowBytes[7], atestArrayOnlyr.arr[7]);

    }

    @Test
    public void testArrayOnly_toBinary() {
        TestArrayOnly model = new TestArrayOnly();
        model.arr = new int[]{123, 1, 0, 2, 0, 0, 0, 4};
        byte[] output = impl.toAntBytes(model);
        assertEquals(output.length, model.arr.length);
        assertEquals(output[0], model.arr[0]);
        assertEquals(output[4], model.arr[4]);
        assertEquals(output[6], model.arr[6]);
        assertEquals(output[7], model.arr[7]);

    }

    final static byte[] highBytes = {123, -1, -1, -1, -1, -1, -1, -1};

    @Test
    public void testArrayCount() {
        TestArrayCount testArrayCount = impl.instanceFromAntBytes(TestArrayCount.class, highBytes);
        assertEquals(2, testArrayCount.byte0.length);
        assertEquals(highBytes[0], testArrayCount.byte0[0]);
        assertEquals(highBytes[1], testArrayCount.byte0[1]);
    }

    @Test
    public void testEndlessArrayWithOtherData() {
        byte[] data = {0b0000_1111,2,1,1,1,2,2,2,3};
        TestEndlessArrayWithOtherData testArrayCount = impl.instanceFromAntBytes(TestEndlessArrayWithOtherData.class, data);
        assertEquals(true, testArrayCount.flag0);
        assertEquals(data[1], testArrayCount.one);
        assertEquals(2, testArrayCount.arr.length);
        assertEquals(65793, testArrayCount.arr[0]); // {1,1,1} ==  65793
        assertEquals(131586, testArrayCount.arr[1]); // {2,2,2} ==  131586
    }

    @Test
    public void testArrayCountWithOtherData() {
        byte[] data = {0b0000_1110, 2, 125, -1, 0, 66};
        TestArrayCountWithOtherData testArrayCount = impl.instanceFromAntBytes(TestArrayCountWithOtherData.class, data);
        assertEquals(false, testArrayCount.flag0);

        assertEquals(data[1], testArrayCount.one);
        assertEquals(data[5], testArrayCount.six);
        assertEquals(3, testArrayCount.arr.length);
        assertEquals(data[2], testArrayCount.arr[0]);
        assertEquals(data[3], testArrayCount.arr[1]);
        assertEquals(data[4], testArrayCount.arr[2]);
    }

}
