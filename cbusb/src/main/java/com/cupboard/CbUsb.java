package com.cupboard;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import java.util.List;

/**
 * Created by zhoumao on 2018/10/20.
 * Description:  提供给业务方的接口
 */

public interface CbUsb {
    UsbDevice getUsbDevice(int vendorId, int productId);

    List<UsbDevice> getDeviceList();

    UsbManager getUsbManager();

    void close() throws Exception;
}
