package com.cupboard.cupboardhardware;

import static com.cupboard.cupboardhardware.UsbReceiver.ACTION_USB_PERMISSION;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.cupboard.UsbHandler;

public class UsbActivity extends AppCompatActivity {
    private PendingIntent mPermissionIntent;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.mContext = this;
        final UsbHandler usbHandler = new UsbHandler(this);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usbHandler.getDeviceList();
            }
        });
        registerReceiver(this);
    }


    public void registerReceiver(Activity context) {
        mPermissionIntent = PendingIntent.getBroadcast(context, 0,
                new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        context.registerReceiver(new UsbReceiver(), filter);
    }

    /**
     * 请求获取指定 USB 设备的权限
     */
    public void requestPermission(UsbDevice device, UsbManager mUsbManager) {
        if (device != null) {
            if (mUsbManager.hasPermission(device)) {
                Toast.makeText(mContext, "已经获取到权限", Toast.LENGTH_SHORT).show();
            } else {
                if (mPermissionIntent != null) {
                    mUsbManager.requestPermission(device, mPermissionIntent);
                    Toast.makeText(mContext, "请求USB权限", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "请注册USB广播", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


}
