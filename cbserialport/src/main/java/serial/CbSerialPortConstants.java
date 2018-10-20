package serial;

import android.support.annotation.IntDef;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by zhoumao on 2018/10/20.
 * Description:
 */

public class CbSerialPortConstants {
    public final static String PORT1 = "/dev/COM1";   //MXC1
    public final static String PORT2 = "/dev/COM2";   //MXC2
    public final static String PORT3 = "/dev/COM3";   //MXC3

    public final static int BAUD9600 = 9600;

    @StringDef({CbSerialPortConstants.PORT1, CbSerialPortConstants.PORT2,
            CbSerialPortConstants.PORT3})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SerialPortId {
    }

    @IntDef({CbSerialPortConstants.BAUD9600})
    @Retention(RetentionPolicy.SOURCE)
    public @interface BaudRate {
    }

}
