package com.cupboard;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import java.util.List;

/**
 * Created by zhoumao on 2018/10/20.
 * Description:
 */

public interface CbUsb {
    UsbDevice getUsbDevice(int vendorId, int productId);

    List<UsbDevice> getDeviceList();

    UsbManager getUsbManager();
}
