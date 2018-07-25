package houtbecke.rs.antbytes;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class AntBytesImpl implements AntBytes {

    class SortedField implements Comparable{

        SortedField(Field field,boolean flag, boolean dynamic, int order){
            this.field = field;
            this.order = order;
            this.flag = flag;
            this.dynamic = dynamic;
        }

        Field field;
        boolean flag;
        boolean dynamic;

        int order;

        @Override
        public int compareTo(Object o) {
            if (o instanceof SortedField ){
                if (o == this) return 0;
                SortedField otherField = (SortedField)o;
                if (otherField.flag && !this.flag) return 1;
                if (!otherField.flag && this.flag) return -1;
                if (otherField.dynamic && !this.dynamic) return 1;
                if ((otherField.dynamic && this.dynamic) || (otherField.flag && this.flag)){
                    if (otherField.order > this.order) return -1;
                    if (otherField.order == this.order) throw new RuntimeException("order has to be unique");
                    if (otherField.order < this.order) return 1;

                }
            }

            return -1;
        }
    }


    protected long getLongFromField(Field f, Object o) throws IllegalAccessException {
        boolean changed = false;
        if (!f.isAccessible())
            f.setAccessible(changed = true);

        long ret = f.getLong(o);
        if (changed)
            f.setAccessible(false);
        return ret;
    }

    protected boolean getBooleanFromField(Field f, Object o) throws IllegalAccessException {
        boolean changed = false;
        if (!f.isAccessible())
            f.setAccessible(changed = true);

        boolean ret = f.getBoolean(o);
        if (changed)
            f.setAccessible(false);
        return ret;
    }

    @Override
    public <T>byte[] toAntBytes(T o) {
        return toAntBytes(o,8);
    }


    SortedSet<SortedField> sortFields(Field[] fields){
        SortedSet<SortedField> sortedSet = new TreeSet<>();
        for (Field f: fields) {
            Dynamic dynamic = f.getAnnotation(Dynamic.class);
            Flag flag = f.getAnnotation(Flag.class);
            boolean hasFlag = (flag != null);
            boolean hasDynamic = (dynamic != null);
            int order = 0;
            if (hasDynamic)
                order = dynamic.order();
            else if (hasFlag)
                    order =flag.value() + flag.startByte()*8;
            sortedSet.add(new SortedField(f, hasFlag, hasDynamic, order));
        }
        return  sortedSet;
    }

    @Override
    public <T> byte[] toAntBytes(T o, int size) {
        byte[] output = new byte[size];
        int dynamicByte = 0;

        HashMap<Integer,Boolean> flags = new HashMap<>();

        for (SortedField sortedField: sortFields(o.getClass().getDeclaredFields())) {
            final Field f =sortedField.field;
            for (Annotation anon : f.getAnnotations()){
                try {
                    Class type = anon.annotationType();
                    if (type == Flag.class) {
                        Flag flag = (Flag) anon;
                        boolean flagValue = getBooleanFromField(f, o);
                        if (flag.startByte() ==0 )
                             flags.put(flag.value(),flagValue);
                    }

                } catch (IllegalAccessException ignore) {
                }

            }

            Dynamic dynamic = f.getAnnotation(Dynamic.class);
            int moveByte=0;

            if (dynamic!=null){
                if (flags.containsKey(dynamic.value()) &&  (flags.get(dynamic.value()) == !dynamic.inverse()) ) {
                    moveByte = dynamicByte;
                } else {
                    continue;
                }
            }

            for (Annotation anon : f.getAnnotations())
                try {
                    Class type = anon.annotationType();

                    if (type == Flag.class) {
                        Flag flag = (Flag) anon;
                        int positionInByte = 7 - (flag.value() % 8);
                        int byteNr = flag.startByte() + (flag.value() / 8);
                        BitBytes.output(output, byteNr, positionInByte, getBooleanFromField(f, o) ? 1 : 0, 1);
                        continue;
                    } else if (type == LSBUXBIT.class) {
                        LSBUXBIT uxbit = ((LSBUXBIT) anon);
                        BitBytes.outputLSB(output, uxbit.value(), uxbit.startBit(), getLongFromField(f, o), uxbit.bitLength());
                        continue;
                    } else if (type == UXBIT.class) {
                        UXBIT uxbit = ((UXBIT) anon);
                        BitBytes.output(output, uxbit.value(), uxbit.startBit(), getLongFromField(f, o), uxbit.bitLength());
                        continue;
                    } else if (type == SXBIT.class) {
                        SXBIT sxbit = ((SXBIT) anon);
                        BitBytes.output(output, sxbit.value(), sxbit.startBit(), getLongFromField(f, o), sxbit.bitLength());
                        continue;
                    } else if (type == Page.class) {
                        BitBytes.output(output, 0, ((Page) anon).value(), 8);
                        continue;
                    }

                    ValueConversionParameters parameters = new ValueConversionParameters(anon, moveByte);

                    if (!parameters.isValid()) {
                        continue;
                    }

                    if (dynamic != null) {
                        dynamicByte = dynamicByte + parameters.byteLength;
                    }

                    Array arrayAnnotation = f.getAnnotation(Array.class);

                    if (arrayAnnotation != null) {
                        writeIntArrayFromField(output, o, f, parameters, arrayAnnotation);
                    } else {
                        writeIntWithConversionParameters(output, parameters, getLongFromField(f, o));
                    }


                } catch (IllegalAccessException ignore) {
                }
        }
        return output;
    }

    private void writeIntArrayFromField(byte[] output, Object object, Field field, ValueConversionParameters parameters, Array arrayAnnotation) {

        if (!field.getType().isArray()) {
            throw new IllegalArgumentException(String.format("Field %s, marked as an array, is not of an array type", field.getName()));
        }

        int dataLength = 0;
        Object arrayObject = null;
        boolean changed = false;

        try {
            if (!field.isAccessible()) {
                field.setAccessible(changed = true);
            }
            arrayObject = field.get(object);
        } catch (Exception e) {
            
        }

        if (changed) {
            field.setAccessible(false);
        }

        if (null == arrayObject) {
            return;
        }

        dataLength = java.lang.reflect.Array.getLength(arrayObject);

        if (0 == dataLength) {
            return;
        }

        int expectedLength = arrayAnnotation.value();


        if (expectedLength > 0 && expectedLength != dataLength) {
            throw new IllegalArgumentException(String.format("Data length (%d) doesn't match expected length (%d)", dataLength, expectedLength));
        }

        int remainingLength = (output.length - parameters.bytePos) / parameters.byteLength;

        if (remainingLength < dataLength) {
            throw new IllegalArgumentException(String.format("Remaining length (%d) is less then data length (%d)", remainingLength, dataLength));
        }

        for (int i = 0; i < dataLength; i++) {
            int value = (int) java.lang.reflect.Array.get(arrayObject, i);
            writeIntWithConversionParameters(output, parameters, value, i * parameters.byteLength);
        }
    }

    private void writeIntWithConversionParameters(byte[] output, ValueConversionParameters parameters, long value) {
        writeIntWithConversionParameters(output, parameters, value, 0);
    }

    private void writeIntWithConversionParameters(byte[] output, ValueConversionParameters parameters, long value, int byteShift) {
        if (parameters.isLSB) {
            BitBytes.outputLSB(output, parameters.bytePos + byteShift, parameters.relativeBitPos, value, 8 * parameters.byteLength);
        } else {
            BitBytes.output(output, parameters.bytePos + byteShift, parameters.relativeBitPos, value, 8 * parameters.byteLength);
        }
    }


    protected void setBooleanOnField(Field field, Object object, boolean value)  {
        boolean changed = false;
        if (!field.isAccessible())
            field.setAccessible(changed = true);
        try {
            field.setBoolean(object, value);
        } catch (IllegalAccessException ignore) {
        }

        if (changed)
            field.setAccessible(false);
    }

    protected void setLongOnField(Field field, Object object, long value)  {
        setOnField(true, field, object, value);
    }

    protected void setIntOnField(Field field, Object object, long value) {
        setOnField(false, field, object, value);
    }

    protected void setOnField(boolean asLong, Field field, Object object, long value) {
        boolean changed = false;
        if (!field.isAccessible())
            field.setAccessible(changed = true);

        try {
            if (!asLong)
                field.setInt(object, (int)value);
            else
                field.setInt(object, (int)value);
        } catch (IllegalAccessException ignore) {
        }

        if (changed)
            field.setAccessible(false);
    }

    protected void setIntArrayToField(Field field, Object object, long[] inputArray) {
        boolean changed = false;
        if (!field.isAccessible())
            field.setAccessible(changed = true);

        try {
            Object arrayObject = java.lang.reflect.Array.newInstance(field.getType().getComponentType(), inputArray.length);
            for (int i = 0; i < inputArray.length; i++) {
                java.lang.reflect.Array.setInt(arrayObject, i, (int) inputArray[i]);
            }
            field.set(object, arrayObject);
        } catch (IllegalAccessException ignore) {
        }

        if (changed)
            field.setAccessible(false);
    }

    @Override
    public <T>T instanceFromAntBytes(Class<? extends T> clazz, byte[] antBytes) {
        try {
            T result = clazz.newInstance();
            fromAntBytes(result, clazz, antBytes);

            return result;
        } catch (InstantiationException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        }

    }

    @Override
    public <T>T fromAntBytes(T object, byte[] antBytes) {
        fromAntBytes(object, object.getClass(), antBytes);

        return object;
    }

    Map<Integer, Object> mapping = Collections.synchronizedMap(new HashMap<Integer, Object>());



    protected int findPage(Class clazz) {
        for (Field f: clazz.getDeclaredFields()) {
            if (f.isAnnotationPresent(Page.class)) {
                Page page = f.getAnnotation(Page.class);
                return page.value();
            }
        }
        return -1;
    }


    protected boolean hasRequired(Class clazz) {
        for (Field f: clazz.getDeclaredFields()) {
            if (f.isAnnotationPresent(Required.class)) {
               return true;

            }
        }
        return false;
    }

    public  boolean hasAllRequired(Class clazz, byte[] antBytes) {
        for (Field f: clazz.getDeclaredFields()) {
            if (!f.isAnnotationPresent(Required.class))  continue;

            Required required = f.getAnnotation(Required.class);

            for (Annotation anon : f.getAnnotations()) {
                Class type = anon.annotationType();
                if (type == U8BIT.class) {
                    U8BIT u8bit = (U8BIT) anon;
                    if ( required.value() != BitBytes.input(antBytes, u8bit.value(), u8bit.startBit(), 8))
                        return false;

                } else if (type == U16BIT.class) {
                    U16BIT u16bit = (U16BIT) anon;
                    if ( required.value() !=BitBytes.input(antBytes, u16bit.value(), u16bit.startBit(), 16))
                        return false;

                } else if (type == U32BIT.class) {
                    U32BIT u32bit = (U32BIT) anon;
                    if ( required.value() !=BitBytes.input(antBytes, u32bit.value(), u32bit.startBit(), 32))
                        return false;

                } else if (type == UXBIT.class) {
                    UXBIT uxbit = (UXBIT) anon;
                    if ( required.value() != BitBytes.input(antBytes, uxbit.value(), uxbit.startBit(), uxbit.bitLength()))
                        return false;
                }
            }



        }
        return true;
    }



    @Override
    public void register(Class clazz) {
        int page = findPage(clazz);
        if (page == -1)
            return;
        if(!hasRequired(clazz)){
            mapping.put(page, clazz);
        }else{
            ArrayList<Class> subpages;
          if (mapping.containsKey(page) && (mapping.get(page) instanceof ArrayList))
            {
                subpages =  (ArrayList<Class>)mapping.get(page);
            }else{
                subpages = new ArrayList<Class>();
            }
            subpages.add(clazz);
            mapping.put(page, subpages);

        }

    }

    @Override
    public Object fromAntBytes(byte[] antBytes) {
        int page = antBytes[0] & 0xFF;
       Object o = mapping.get(page);
        Class clazz = null;
        if (o instanceof ArrayList){
            ArrayList<Class> subpages =   (ArrayList<Class>) o;
            for(Class c : subpages){
                if (hasAllRequired(c,antBytes))
                    return instanceFromAntBytes(c, antBytes);
            }

        }else{
            clazz = (Class) mapping.get(page);
        }

        if (clazz == null)
            return null;
        return instanceFromAntBytes(clazz, antBytes);
    }

    private <T> void fromAntBytes(final T object, final Class<? extends T> clazz, final byte[] antBytes) {
        final HashMap<Integer,Boolean> flags = new HashMap<>();
        int dynamicByte = 0;

        SortedSet<SortedField> fields = sortFields(clazz.getDeclaredFields());
        for (SortedField sortedField:fields ) {
            final Field f =sortedField.field;

            for (Annotation anon : f.getAnnotations()) {
                Class type = anon.annotationType();
                if (type == Flag.class) {
                    Flag flag = (Flag) anon;
                    int positionInByte = 7 - (flag.value() % 8);
                    int byteNr = flag.startByte() + (flag.value() / 8);
                    boolean flagValue = BitBytes.input(antBytes, byteNr, positionInByte, 1) == 1;
                    setBooleanOnField(f, object, flagValue);
                    if (flag.startByte() == 0)
                        flags.put(flag.value(), flagValue);
                }
            }

            Dynamic dynamic = f.getAnnotation(Dynamic.class);
            int moveByte = 0;



            if (dynamic != null) {
                if (flags.containsKey(dynamic.value()) &&  (flags.get(dynamic.value()) == !dynamic.inverse()) ) {
                    moveByte = dynamicByte;
                } else {
                    continue;
                }
            }


            for (Annotation anon : f.getAnnotations()) {
                Class type = anon.annotationType();

                if (type == Page.class) {
                    setIntOnField(f, object, BitBytes.input(antBytes, 0, 8));
                    continue;
                } else if (type == SXBIT.class) {
                    SXBIT sxbit = (SXBIT) anon;
                    setIntOnField(f, object, BitBytes.input(antBytes, sxbit.value(), sxbit.startBit(), sxbit.bitLength(), true));
                    continue;
                } else if (type == UXBIT.class) {
                    UXBIT uxbit = (UXBIT) anon;
                    setIntOnField(f, object, BitBytes.input(antBytes, uxbit.value(), uxbit.startBit(), uxbit.bitLength()));
                    continue;
                } else if (type == LSBUXBIT.class) {
                    LSBUXBIT uxbit = (LSBUXBIT) anon;
                    setIntOnField(f, object, BitBytes.inputLSB(antBytes, uxbit.value() + moveByte, uxbit.startBit(), uxbit.bitLength()));
                    continue;
                }


                ValueConversionParameters parameters = new ValueConversionParameters(anon, moveByte);

                if (!parameters.isValid()) {
                    continue;
                }

                if (dynamic != null)
                    dynamicByte += parameters.byteLength;


                Array array = f.getAnnotation(Array.class);

                if (array != null) {
                    int count = array.value() > 0 ? array.value() : (antBytes.length - parameters.bytePos) / parameters.byteLength;
                    if (count > 0) {
                        long[] result = new long[count];
                        for (int i = 0; i < count; i++) {
                            result[i] = parseValueForAnnotationParameters(antBytes, parameters, i * parameters.byteLength);
                        }
                        setIntArrayToField(f, object, result);
                    }
                } else {
                    long value = parseValueForAnnotationParameters(antBytes, parameters);
                    setIntOnField(f, object, value);
                }
            }
        }
    }

    private long parseValueForAnnotationParameters(byte[] antBytes, ValueConversionParameters parameters) {
        return parseValueForAnnotationParameters(antBytes, parameters, 0);
    }

    private long parseValueForAnnotationParameters(byte[] antBytes, ValueConversionParameters parameters, int byteShift) {
        if (parameters.isLSB) {
            return BitBytes.inputLSB(antBytes, parameters.bytePos + byteShift, parameters.relativeBitPos, 8 * parameters.byteLength, parameters.signed);
        }
        else {
            return BitBytes.input(antBytes, parameters.bytePos + byteShift, parameters.relativeBitPos, 8 * parameters.byteLength, parameters.signed);
        }
    }

}