package houtbecke.rs.antbytes;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class AntBytesImpl implements AntBytes {


    protected void output(byte[] output, int bitpos, long value, int bitlength) {
        if (bitpos % 8 != 0 || bitlength % 8 != 0) throw new RuntimeException("not supported yet");
        int pos = bitpos / 8;

        for (int i = bitlength / 8 - 1; i >= 0; i--) {
            output[bitpos / 8 + i] = (byte) (value & 0xffL);
            value >>= 8;

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

    @Override
    public <T>byte[] toAntBytes(T o) {
        byte[] output = new byte[8];
        for (Field f: o.getClass().getDeclaredFields())
            for (Annotation anon: f.getAnnotations())
                try {
                    Class type = anon.annotationType();
                    if (type == U8BIT.class) {
                        output(output, ((U8BIT) anon).value() * 8, getLongFromField(f, o), 8);
                    } else if (type == U16BIT.class) {
                        output(output, ((U16BIT)anon).value() * 8, getLongFromField(f, o), 16);
                    } else if (type == U32BIT.class) {
                        output(output, ((U32BIT)anon).value() * 8, getLongFromField(f, o), 32);
                    } else if (type == Page.class) {
                        output(output, 0, ((Page)anon).value(), 8);
                    }
                } catch (IllegalAccessException ignore) {}
        return output;
    }

    protected long input(byte[] input, int bitpos, int bitlength) {
        if (bitpos % 8 != 0 || bitlength % 8 != 0) throw new RuntimeException("not supported yet");

        long result = 0;
        for (int i = bitpos / 8; i < bitpos / 8 + bitlength / 8; i++) {
            result <<= 8;
            result += input[i] & 0xFF;
        }
        return result;
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

    @Override
    public <T>T instanceFromAntBytes(Class<? extends T> clazz, byte[] antBytes) {
        try {
            T result = clazz.newInstance();
            return fromAntBytes(result, antBytes);
        } catch (InstantiationException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        }

    }

    @Override
    public <T>T fromAntBytes(T object, byte[] antBytes) {
        for (Field f: object.getClass().getDeclaredFields())
            for (Annotation anon: f.getAnnotations()) {
                Class type = anon.annotationType();
                if (type == U8BIT.class) {
                    setIntOnField(f, object, input(antBytes, ((U8BIT) anon).value() * 8, 8));
                } else if (type == U16BIT.class) {
                    setIntOnField(f, object, input(antBytes, ((U16BIT) anon).value() * 8, 16));
                } else if (type == U32BIT.class) {
                    setLongOnField(f, object, input(antBytes, ((U32BIT) anon).value() * 8, 32));
                } else if (type == Page.class) {
                    setIntOnField(f, object, input(antBytes, 0, 8));
                }
            }

        return object;
    }

//    public Object fromAntBytes(byte[] antBytes) {
//
//    }
}
