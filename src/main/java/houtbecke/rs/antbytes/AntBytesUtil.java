package houtbecke.rs.antbytes;

import javax.annotation.Nonnull;

public class AntBytesUtil {

    private static AntBytes instance;

    @Nonnull
    public static AntBytes getInstance() {
        return instance == null ? instance = new AntBytesImpl() : instance;
    }

}
