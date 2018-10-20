package serial;

import android.os.SystemClock;
import android.serialport.SerialPort;
import android.serialport.SerialPortFinder;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import serial.utils.Logger;


/**
 * @author by zhoumao on 2018/10/20.
 *
 *         通过串口用于接收或发送数据
 */

public class CbSerialPortService implements CbSerialPort {


    @CbSerialPortConstants.SerialPortId
    String portId;
    private SerialPort serialPort = null;
    private SerialPortFinder mSerialPortFinder;
    private BufferedInputStream inputStream = null;
    private OutputStream outputStream = null;
    private ReceiverThread mReceiverThread = null;
    private boolean isStart = false;
    private CbSerialPortReceiver receiver;


    private CbSerialPortService() {
        mSerialPortFinder = new SerialPortFinder();
    }

    public static CbSerialPortService newInstance() {
        return new CbSerialPortService();
    }


    @Override
    public String[] getAllPortName() {
        return mSerialPortFinder.getAllDevices();
    }


    /**
     * 打开串口，接收数据
     * 通过串口，接收单片机发送来的数据
     */
    @Override
    public void open(@CbSerialPortConstants.SerialPortId String portId) throws IOException {
        this.portId = portId;
        serialPort = new SerialPort(new File(portId), 9600, 0);
        //调用对象SerialPort方法，获取串口中"读和写"的数据流
        inputStream = new BufferedInputStream(serialPort.getInputStream());
        outputStream = serialPort.getOutputStream();
        isStart = true;
        initReceiverThread();
    }

    /**
     * 打开串口，接收数据
     * 通过串口，接收单片机发送来的数据
     */
    @Override
    public void open(@CbSerialPortConstants.SerialPortId String portId,
            @CbSerialPortConstants.BaudRate int baudRate) throws IOException {
        serialPort = new SerialPort(new File(portId), baudRate, 0);
        //调用对象SerialPort方法，获取串口中"读和写"的数据流
        inputStream = new BufferedInputStream(serialPort.getInputStream());
        outputStream = serialPort.getOutputStream();
        isStart = true;
        initReceiverThread();
    }

    /**
     * 通过串口，发送数据
     *
     * @param data 要发送的数据
     */
    @Override
    public void send(byte[] data) throws IOException {
        outputStream.write(data);
//        outputStream.flush();
    }

    @Override
    public void setReceiver(CbSerialPortReceiver receiver) {
        this.receiver = receiver;
    }

    /**
     * 关闭串口
     * 关闭串口中的输入输出流
     */
    @Override
    public void close() throws IOException {
        if (inputStream != null) {
            inputStream.close();
        }
        if (outputStream != null) {
            outputStream.close();
        }
        if (mReceiverThread != null) {
            mReceiverThread.interrupt();
        }
        mReceiverThread = null;
        isStart = false;
    }


    private void initReceiverThread() {
        // TODO 注意回收线程
        if (mReceiverThread != null && mReceiverThread.isAlive()) {
            Logger.debug(portId + "对应的接收线程已开启过了");
        } else {
            mReceiverThread = new ReceiverThread();
            mReceiverThread.start();
        }
    }

    /**
     * 接收串口数据的线程
     */
    private class ReceiverThread extends Thread {
        @Override
        public void run() {
            super.run();
            //条件判断，只要条件为true，则一直执行这个线程
            while (isStart) {
                if (inputStream == null) {
                    return;
                }
                try {
                    int available = inputStream.available();
                    if (available > 0) {
                        byte[] readData = new byte[1024];
                        int size = inputStream.read(readData);
                        if (size > 0 && receiver != null) {
                            receiver.onReceive(readData, size);
                        }
                    } else {
                        // 暂停一点时间，免得一直循环造成CPU占用率过高
                        SystemClock.sleep(1);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
