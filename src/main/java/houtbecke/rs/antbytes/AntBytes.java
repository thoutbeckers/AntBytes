package houtbecke.rs.antbytes;

public interface AntBytes {
    public <T>byte[] toAntBytes(T o);
    public <T>T instanceFromAntBytes(Class<? extends T> clazz, byte[] antBytes);
    public <T>T fromAntBytes(T object, byte[] antBytes);

    //public void register(Class )

}
