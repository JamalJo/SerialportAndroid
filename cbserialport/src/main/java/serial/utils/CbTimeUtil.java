package serial.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CbTimeUtil {

    public static final SimpleDateFormat DEFAULT_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public static String currentTime() {
        Date date = new Date();
        return DEFAULT_FORMAT.format(date);
    }
}
