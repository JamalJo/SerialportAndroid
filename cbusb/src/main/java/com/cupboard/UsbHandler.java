package com.cupboard;

import android.content.Context;
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
 * Created by zhoumao on 2018/10/12.
 * Description:
 */

public class UsbHandler {

    private Context mContext;
    private UsbManager mUsbManager;

    private UsbHandler() {
    }

    public UsbHandler(Context context) {
        mContext = context;
        this.mUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
    }


    public UsbManager getUsbManager() {
        return mUsbManager;
    }


    public List<UsbDevice> getDeviceList() {
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        List<UsbDevice> usbDevices = new ArrayList<>();
        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            usbDevices.add(device);
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
    public UsbDevice getUsbDevice(int vendorId, int productId) {
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            if (device.getVendorId() == vendorId && device.getProductId() == productId) {
                Log.e("USBUtil", "getDeviceList: " + device.getDeviceName());
                return device;
            }
        }
        Toast.makeText(mContext, "没有对应的设备", Toast.LENGTH_SHORT).show();
        return null;
    }


    /**
     * 判断对应 USB 设备是否有权限
     */
    public boolean hasPermission(UsbDevice device) {
        return mUsbManager.hasPermission(device);
    }


    public boolean openPort(UsbDevice device) {
        //获取设备接口，一般只有一个，多个的自己研究去
        UsbInterface usbInterface = device.getInterface(0);

        // 判断是否有权限
        if (hasPermission(device)) {
            // 打开设备，获取 UsbDeviceConnection 对象，连接设备，用于后面的通讯
            UsbDeviceConnection usbConnection = mUsbManager.openDevice(device);

            if (usbConnection == null) {
                return false;
            }
            if (usbConnection.claimInterface(usbInterface, true)) {
                Toast.makeText(mContext, "找到 USB 设备接口", Toast.LENGTH_SHORT).show();
            } else {
                usbConnection.close();
                Toast.makeText(mContext, "没有找到 USB 设备接口", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(mContext, "没有 USB 权限", Toast.LENGTH_SHORT).show();
            return false;
        }

        //获取接口上的两个端点，分别对应 OUT 和 IN
        for (int i = 0; i < usbInterface.getEndpointCount(); ++i) {
            UsbEndpoint end = usbInterface.getEndpoint(i);
            if (end.getDirection() == UsbConstants.USB_DIR_IN) {
                UsbEndpoint usbEndpointIn = end;
            } else {
                UsbEndpoint usbEndpointOut = end;
            }
        }
        return true;
    }
}
