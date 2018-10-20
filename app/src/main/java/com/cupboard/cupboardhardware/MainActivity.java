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
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cupboard.CbUsb;
import com.cupboard.CbUsbService;

import java.io.IOException;

import serial.CbSerialPort;
import serial.CbSerialPortConstants;
import serial.CbSerialPortReceiver;
import serial.CbSerialPortService;
import serial.utils.CbByteUtils;
import serial.utils.CbTimeUtil;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "jamaljo";
    private PendingIntent mPermissionIntent;
    CbSerialPort mCbSerialPort;
    private Context mContext;

    Button btn_listusb;
    Button btn_listserial;
    Button btn_serial_send;
    TextView txt_show;


    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.mContext = this;
        initView();
        initPort();
        initUsb();
        mHandler = new Handler();
    }

    private void initUsb() {
        final CbUsb cbUsb = CbUsbService.newInstance(this);
        btn_listusb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_show.setText("所有usb设备： " + cbUsb.getDeviceList());
            }
        });
        registerReceiver(this);
    }

    private String serialHistory = "";

    private void initPort() {
        mCbSerialPort = CbSerialPortService.newInstance();
        try {
            mCbSerialPort.open(CbSerialPortConstants.PORT2);
            mCbSerialPort.setReceiver(new CbSerialPortReceiver() {
                @Override
                public void onReceive(final byte[] response, final int size) {
                    //接收串口数据
                    Log.d(TAG, "onReceive: " + CbByteUtils.bytes2HexStr(response));
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            serialHistory = serialHistory + CbTimeUtil.currentTime()
                                    + CbByteUtils.bytes2HexStr(
                                    response, 0, size) + "\n";
                            txt_show.setText(serialHistory);
                        }
                    });
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        btn_listserial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String serials = "";
                for (String serial : mCbSerialPort.getAllPortName()) {
                    serials = serials + "   |  " + serial;
                }
                txt_show.setText("所有串口名： " + serials);
            }
        });
        btn_serial_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发送串口数据
                try {
                    byte[] bytes = CbByteUtils.hexStr2bytes("abcd");
                    mCbSerialPort.send(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initView() {
        btn_listusb = findViewById(R.id.btn_usblist);
        btn_listserial = findViewById(R.id.btn_seriallist);
        btn_serial_send = findViewById(R.id.btn_serialsenddata);
        txt_show = findViewById(R.id.txt_show);
    }

    private void registerReceiver(Activity context) {
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
    private void requestPermission(UsbDevice device, UsbManager mUsbManager) {
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mCbSerialPort.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
