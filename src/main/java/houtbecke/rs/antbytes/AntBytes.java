package houtbecke.rs.antbytes;

public interface AntBytes {
    <T>byte[] toAntBytes(T o);
    <T>T instanceFromAntBytes(Class<? extends T> clazz, byte[] antBytes);
    <T>T fromAntBytes(T object, byte[] antBytes);

    void register(Class clazz);
    Object fromAntBytes(byte[] antBytes);


}
