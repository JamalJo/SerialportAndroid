package serial;

/**
 * Created by zhoumao on 2018/10/20.
 * Description:
 */

public interface CbSerialPortReceiver {
    void onReceive(byte[] data);
}
