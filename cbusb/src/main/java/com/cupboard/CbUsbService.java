package com.cupboard;

import static com.cupboard.CbUsbReceiver.ACTION_USB_PERMISSION;

import android.app.Activity;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


/**
 * Created by zhoumao on 2018/10/20.
 * Description:
 */

public class CbUsbService implements CbUsb {

    private PendingIntent mPermissionIntent;
    private UsbManager usbManager;
    private Context context;
    private CbUsbReceiver mCbUsbReceiver;
    private UsbInterface usbInterface;
    private UsbEndpoint usbEndpointIn;
    private UsbEndpoint usbEndpointOut;
    private UsbDeviceConnection usbConnection;
    private boolean isStart = false;

    private ReceiverThread mReceiverThread = null;


    private CbUsbService(Application context) {
        this.context = context;
        usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        mCbUsbReceiver = new CbUsbReceiver();
    }

    public static CbUsb newInstance(Application context) {
        return new CbUsbService(context);
    }

    public UsbManager getUsbManager() {
        return usbManager;
    }

    /**
     * 获取 USB 设备列表
     */
    @Override
    public List<UsbDevice> getDeviceList() {
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        List<UsbDevice> usbDevices = new ArrayList<>();
        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            usbDevices.add(device);
            Log.e("CbUsbService", "getDeviceList: " + device.getDeviceName());
        }
        return usbDevices;
    }

    /**
     * mVendorId=1137,mProductId=85  佳博 3150T 标签打印机
     *
     * @param vendorId  厂商ID
     * @param productId 产品ID
     * @return device
     */
    @Override
    public UsbDevice getUsbDevice(int vendorId, int productId) {
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            if (device.getVendorId() == vendorId && device.getProductId() == productId) {
                Log.e("CbUsbService", "getDeviceList: " + device.getDeviceName());
                return device;
            }
        }
        Toast.makeText(context, "没有对应的设备", Toast.LENGTH_SHORT).show();
        return null;
    }

    /**
     * 判断对应 USB 设备是否有权限
     */
    public boolean hasPermission(UsbDevice device) {
        return usbManager.hasPermission(device);
    }

    /**
     * 请求获取指定 USB 设备的权限
     */
    public void requestPermission(UsbDevice device) {
        if (device != null) {
            if (usbManager.hasPermission(device)) {
                Logger.error("已经获取到权限");
            } else {
                if (mPermissionIntent != null) {
                    usbManager.requestPermission(device, mPermissionIntent);
                    Logger.error("请求USB权限");
                } else {
                    Logger.error("请注册USB广播");
                }
            }
        }
    }

    /**
     * 打开通信端口
     */
    public boolean open(UsbDevice device) {
        //获取设备接口，一般只有一个，多个的自己研究去
        usbInterface = device.getInterface(0);

        // 判断是否有权限
        if (hasPermission(device)) {
            // 打开设备，获取 UsbDeviceConnection 对象，连接设备，用于后面的通讯
            usbConnection = usbManager.openDevice(device);

            if (usbConnection == null) {
                return false;
            }
            if (usbConnection.claimInterface(usbInterface, true)) {
                Logger.error("找到 USB 设备接口");
            } else {
                usbConnection.close();
                Logger.error("没有找到 USB 设备接口");
                return false;
            }
        } else {
            Logger.error("没有 USB 权限");
            return false;
        }

        //获取接口上的两个端点，分别对应 OUT 和 IN
        for (int i = 0; i < usbInterface.getEndpointCount(); ++i) {
            UsbEndpoint end = usbInterface.getEndpoint(i);
            if (end.getDirection() == UsbConstants.USB_DIR_IN) {
                usbEndpointIn = end;
            } else {
                usbEndpointOut = end;
            }
        }
        initReceiverThread();
        return true;
    }

    public int send(byte[] bytes) {
        return usbConnection.bulkTransfer(usbEndpointOut, bytes, bytes.length, 500);
    }

    @Override
    public void close() throws Exception {
        if (usbConnection == null) {
            return;
        }
        if (mReceiverThread != null) {
            mReceiverThread.interrupt();
        }
        mReceiverThread = null;
        isStart = false;
        usbConnection.close();
        usbConnection.releaseInterface(usbInterface);
        usbConnection = null;
        usbEndpointIn = null;
        usbEndpointOut = null;
        usbManager = null;
        usbInterface = null;
        Logger.debug("Device closed. ");
    }

    /**
     * 注册广播
     */
    public void registerReceiver(Activity context) {
        mPermissionIntent = PendingIntent.getBroadcast(context, 0,
                new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        context.registerReceiver(mCbUsbReceiver, filter);
    }

    public void unRegisterReceiver(Activity context) {
        context.unregisterReceiver(mCbUsbReceiver);
        mPermissionIntent = null;
    }


    private void initReceiverThread() {
        isStart = true;
        if (mReceiverThread != null && mReceiverThread.isAlive()) {
            Logger.debug("usb的接收线程已开启过了");
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
                byte[] readData = new byte[1024];
                int size = usbConnection.bulkTransfer(usbEndpointIn, readData, readData.length,
                        500);
                if (size > 0) {
                    Logger.debug(CbByteUtils.bytes2HexStr(readData, 0, size));
                } else {
                    Logger.debug("data is null");
                }
            }

        }
    }
}
