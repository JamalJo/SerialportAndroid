package com.cupboard;

import android.content.Context;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by zhoumao on 2018/10/12.
 * Description:
 */

public class CbUsbService implements CbUsb {

    private Context mContext;
    private UsbManager mUsbManager;

    private CbUsbService() {
    }

    private CbUsbService(Context context) {
        mContext = context;
        this.mUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
    }

    public static CbUsbService newInstance(Context context) {
        return new CbUsbService(context);
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
                return device;
            }
        }
        Logger.error("没有对应的设备");
        return null;
    }


    /**
     * 判断对应 USB 设备是否有权限
     */
    private boolean hasPermission(UsbDevice device) {
        return mUsbManager.hasPermission(device);
    }


    private boolean openPort(UsbDevice device) {
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
                UsbEndpoint usbEndpointIn = end;
            } else {
                UsbEndpoint usbEndpointOut = end;
            }
        }
        return true;
    }
}
