package houtbecke.rs.antbytes;

public class AntBytesUtil {

    private static AntBytes instance;
    public static AntBytes getInstance() {
        return instance == null ? instance = new AntBytesImpl() : instance;
    }

}
