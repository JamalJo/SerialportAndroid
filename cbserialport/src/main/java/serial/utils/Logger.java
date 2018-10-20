package serial.utils;

import android.util.Log;

import com.cupboard.cbserialport.BuildConfig;

/**
 * Created by zhoumao on 2018/10/20.
 * Description: 日志打印
 */

public class Logger {

    private static final String TAG = "Cb_SerialPort";


    public static void debug(String tag, String info) {
        if (BuildConfig.DEBUG) {
            Log.d(tag + "", info + "");
        }
    }

    public static void debug(String info) {
        debug(TAG, info);
    }

    public static void error(String tag, String info) {
        Log.e(tag + "", info);
    }

    public static void error(String info) {
        error(TAG, info);
    }
}
