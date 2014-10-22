package houtbecke.rs.antbytes;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AntBytesImpl implements AntBytes {



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
                        U8BIT u8bit = (U8BIT) anon;
                        BitBytes.output(output, u8bit.value(), u8bit.startBit(), getLongFromField(f, o), 8);
                    } else if (type == U16BIT.class) {
                        U16BIT u16bit = (U16BIT) anon;
                        BitBytes.output(output, u16bit.value(), u16bit.startBit(), getLongFromField(f, o), 16);
                    } else if (type == U32BIT.class) {
                        U32BIT u32bit = (U32BIT)anon;
                        BitBytes.output(output, u32bit.value(), u32bit.startBit(), getLongFromField(f, o), 32);
                    } else if (type == UXBIT.class) {
                        UXBIT uxbit = ((UXBIT)anon);
                        BitBytes.output(output, uxbit.value(), uxbit.startBit(), getLongFromField(f, o), uxbit.bitLength());
                    } else if (type == Page.class) {
                        BitBytes.output(output, 0, ((Page)anon).value(), 8);
                    }

                } catch (IllegalAccessException ignore) {}
        return output;
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
                    U8BIT u8bit = (U8BIT) anon;
                    setIntOnField(f, object, BitBytes.input(antBytes, u8bit.value(), u8bit.startBit(), 8));
                } else if (type == U16BIT.class) {
                    U16BIT u16bit = (U16BIT) anon;
                    setIntOnField(f, object, BitBytes.input(antBytes, u16bit.value(), u16bit.startBit(), 16));
                } else if (type == U32BIT.class) {
                    U32BIT u32bit = (U32BIT)anon;
                    setLongOnField(f, object, BitBytes.input(antBytes, u32bit.value(), u32bit.startBit(), 32));
                } else if (type == UXBIT.class) {
                    UXBIT uxbit = (UXBIT) anon;
                    setLongOnField(f, object, BitBytes.input(antBytes, uxbit.value(), uxbit.startBit(), uxbit.bitLength()));
                } else if (type == Page.class) {
                    setIntOnField(f, object, BitBytes.input(antBytes, 0, 8));
                }
            }

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

                } else if (type == Page.class) {
                    if ( required.value() != BitBytes.input(antBytes, 0, 8))
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
}
