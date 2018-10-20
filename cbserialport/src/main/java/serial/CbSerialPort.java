package serial;

import java.io.IOException;

/**
 * Created by zhoumao on 2018/10/20.
 * Description:
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
