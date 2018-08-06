import org.junit.Test;

import static org.junit.Assert.*;

import houtbecke.rs.antbytes.*;

public class ValuesArrayAnnotationTest {

    AntBytes impl = AntBytesUtil.getInstance();

    public static class TestArrayOnly {
        public TestArrayOnly() {
        }

        @ValuesArray
        @U8BIT
        public int[] arr;
    }

    public static class TestArrayCount {
        public TestArrayCount() {
        }

        @ValuesArray(2)
        @S8BIT(0)
        public int[] arr;
    }

    public static class TestEndlessArrayWithOtherData {
        public TestEndlessArrayWithOtherData() {
        }

        @Flag(0)
        public boolean flag0;

        @U8BIT(1)
        public int one;

        @ValuesArray
        @U24BIT(2)
        public int[] arr;
    }

    public static class TestArrayCountWithOtherData {
        public TestArrayCountWithOtherData() {
        }

        @Flag(0)
        public boolean flag0;

        @S8BIT(1)
        public int one;

        @ValuesArray(3)
        @S8BIT(2)
        public int[] arr;

        @S8BIT(5)
        public int six;
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
        assertEquals(2, testArrayCount.arr.length);
        assertEquals(highBytes[0], testArrayCount.arr[0]);
        assertEquals(highBytes[1], testArrayCount.arr[1]);
    }

    // {1,1,1} ==  65793
    // {2,2,2} ==  131586

    final static byte[] array24BitInt = {1, 2, 1, 1, 1, 2, 2, 2};

    @Test
    public void testEndlessArrayWithOtherData() {
        TestEndlessArrayWithOtherData testArrayCount = impl.instanceFromAntBytes(TestEndlessArrayWithOtherData.class, array24BitInt);
        assertEquals(true, testArrayCount.flag0);
        assertEquals(array24BitInt[1], testArrayCount.one);
        assertEquals(65793, testArrayCount.arr[0]);
        assertEquals(131586, testArrayCount.arr[1]);
    }

    @Test
    public void testEndlessArrayWithOtherData_toBinary() {
        TestEndlessArrayWithOtherData testArrayCount = new TestEndlessArrayWithOtherData();
        testArrayCount.flag0 = true;
        testArrayCount.arr = new int[]{65793, 131586};
        testArrayCount.one = array24BitInt[1];

        byte[] output = impl.toAntBytes(testArrayCount);
        assertArrayEquals(array24BitInt, output);
    }

    @Test
    public void testArrayCountWithOtherData() {
        byte[] data = {0b0000_1110, 2, 125, -1, 0, 66};
        TestArrayCountWithOtherData testArrayCount = impl.instanceFromAntBytes(TestArrayCountWithOtherData.class, data);
        assertEquals(false, testArrayCount.flag0);

        assertEquals(data[1], testArrayCount.one);
        assertEquals(data[5], testArrayCount.six);
        assertEquals(data[2], testArrayCount.arr[0]);
        assertEquals(data[3], testArrayCount.arr[1]);
        assertEquals(data[4], testArrayCount.arr[2]);
    }


    @Test
    public void testArrayCountWithOtherData_toBinary() {
        byte[] testData = {0, 2, 125, -1, 0, 66, 0, 0};

        TestArrayCountWithOtherData testArrayCount = new TestArrayCountWithOtherData();
        testArrayCount.flag0 = false;
        testArrayCount.one = 2;
        testArrayCount.arr = new int[]{125, -1, 0};
        testArrayCount.six = 66;

        byte[] output = impl.toAntBytes(testArrayCount);
        assertArrayEquals(testData, output);
    }


    @Test(expected = RuntimeException.class)
    public void inputDataContainsLessItemsThenRequired() {
        byte[] testData = {1};
        TestArrayCount testArrayCount = impl.instanceFromAntBytes(TestArrayCount.class, testData);
    }


    @Test(expected = IllegalArgumentException.class)
    public void arrayFieldContainsLessItemsThenRequired() {
        TestArrayCount model = new TestArrayCount();
        model.arr = new int[]{1};
        byte[] data = impl.toAntBytes(model);
    }

    @Test(expected = IllegalArgumentException.class)
    public void arrayFieldContainsMoreItemsThenRequired() {
        TestArrayCount model = new TestArrayCount();
        model.arr = new int[]{1, 2, 3};
        byte[] data = impl.toAntBytes(model);
    }


    @Test(expected = IllegalArgumentException.class)
    public void wrongFieldTypeException() {
        class WrongFieldTypeForArray {
            public WrongFieldTypeForArray() {
            }

            @ValuesArray()
            @U8BIT(1)
            private int byte0;
        }

        WrongFieldTypeForArray model = new WrongFieldTypeForArray();
        model.byte0 = 1;

        byte[] data = impl.toAntBytes(model);
    }


    @Test(expected = IllegalArgumentException.class)
    public void inputArrayExceedsOutputDataCapacity() {
        class BigArray {
            public BigArray() {
            }

            @ValuesArray
            @U8BIT
            public long[] arr;
        }

        BigArray model = new BigArray();
        model.arr = new long[]{1,1,1,1,1,1,1,1,1,1};

        byte[] data = impl.toAntBytes(model, 8);
    }


}
