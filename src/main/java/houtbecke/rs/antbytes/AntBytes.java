package houtbecke.rs.antbytes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface AntBytes {

    @Nonnull
    <T> byte[] toAntBytes(@Nonnull T o);

    @Nonnull
    <T> byte[] toAntBytes(@Nonnull T o,
                          int size);

    <T> T instanceFromAntBytes(@Nonnull Class<? extends T> clazz,
                               @Nonnull byte[] antBytes);

    <T> T fromAntBytes(@Nonnull T object,
                       @Nonnull byte[] antBytes);

    void register(@Nonnull Class clazz);

    @Nullable
    Object fromAntBytes(@Nonnull byte[] antBytes);

}
