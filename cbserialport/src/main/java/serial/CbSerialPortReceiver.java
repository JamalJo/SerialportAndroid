package serial;

/**
 * Created by zhoumao on 2018/10/20.
 * Description: 串口接收器
 */

public interface CbSerialPortReceiver {
    void onReceive(byte[] data, int size);
}
