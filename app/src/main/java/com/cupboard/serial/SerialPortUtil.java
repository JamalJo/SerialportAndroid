package com.cupboard.serial;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android_serialport_api.SerialPort;
import android_serialport_api.SerialPortFinder;

/**
 * @author by AllenJ on 2018/4/20.
 *
 *         通过串口用于接收或发送数据
 */

public class SerialPortUtil {

    private SerialPort serialPort = null;
    private SerialPortFinder mSerialPortFinder;
    private InputStream inputStream = null;
    private OutputStream outputStream = null;
    private ReceiveThread mReceiveThread = null;
    private boolean isStart = false;

    private static final String TAG = "SerialPortUtil";


    public SerialPortUtil() {
        mSerialPortFinder = new SerialPortFinder();
    }


    public String[] getAllSerialPort() {
        return mSerialPortFinder.getAllDevices();
    }

    /**
     * 打开串口，接收数据
     * 通过串口，接收单片机发送来的数据
     */
    public void openSerialPort() {
        try {
            serialPort = new SerialPort(new File("/dev/COM0"), 9600, 0);
            //调用对象SerialPort方法，获取串口中"读和写"的数据流
            inputStream = serialPort.getInputStream();
            outputStream = serialPort.getOutputStream();
            isStart = true;

        } catch (IOException e) {
            e.printStackTrace();
        }
        getSerialPort();
    }

    /**
     * 关闭串口
     * 关闭串口中的输入输出流
     */
    public void closeSerialPort() {
        Log.i("test", "关闭串口");
        try {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            isStart = false;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 发送数据
     * 通过串口，发送数据到单片机
     *
     * @param data 要发送的数据
     */
    public void sendSerialPort(String data) {
        try {
            byte[] sendData = DataUtils.HexToByteArr(data);
            outputStream.write(sendData);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getSerialPort() {
        // TODO 注意回收线程
        if (mReceiveThread == null || !mReceiveThread.isAlive()) {
            mReceiveThread = new ReceiveThread();
        }
        mReceiveThread.start();
    }

    /**
     * 接收串口数据的线程
     */

    private class ReceiveThread extends Thread {
        @Override
        public void run() {
            super.run();
            //条件判断，只要条件为true，则一直执行这个线程
            while (isStart) {
                if (inputStream == null) {
                    return;
                }
                byte[] readData = new byte[1024];
                try {
                    int size = inputStream.read(readData);
                    if (size > 0) {
                        String readString = DataUtils.ByteArrToHex(readData, 0, size);
                        //TODO 发布数据
                        Log.d(TAG, readString);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
