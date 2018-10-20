package serial;

import java.io.IOException;

/**
 * Created by zhoumao on 2018/10/20.
 * Description: 串口相关操作函数的接口
 */

public interface CbSerialPort {
    String[] getAllPortName();

    void open(@CbSerialPortConstants.SerialPortId String portId) throws IOException;

    void open(@CbSerialPortConstants.SerialPortId String portId,
            @CbSerialPortConstants.BaudRate int baudRate) throws IOException;

    void send(byte[] data) throws IOException;

    void setReceiver(CbSerialPortReceiver receiver);

    void close() throws IOException;
}
