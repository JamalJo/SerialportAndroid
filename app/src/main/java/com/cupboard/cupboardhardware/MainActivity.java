package com.cupboard.cupboardhardware;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cupboard.CbUsb;
import com.cupboard.CbUsbService;
import com.cupboard.Logger;

import java.io.IOException;

import serial.CbSerialPort;
import serial.CbSerialPortConstants;
import serial.CbSerialPortReceiver;
import serial.CbSerialPortService;
import serial.utils.CbByteUtils;
import serial.utils.CbTimeUtil;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "jamaljo";
    CbSerialPort mCbSerialPort;

    Button btn_listusb;
    Button btn_listserial;
    Button btn_serial_send;
    Button btn_serial_clear;
    TextView txt_show;
    EditText mEditText;

    private CbUsb mCbUsb;

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initPort();
        initUsb();
        mHandler = new Handler();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Logger.error("KeyCode: " + keyCode);  // 支持PS2设备如扫码枪、键盘的数据读取，具体去"KeyEvent"里查询；
        txt_show.setText("USB 输入: " + keyCode);
        return super.onKeyDown(keyCode, event);
    }

    private void initUsb() {
        mCbUsb = CbUsbService.newInstance(this.getApplication());
        Logger.error(mCbUsb.getDeviceList().size() + "size:");
        btn_listusb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_show.setText("所有usb设备： " + mCbUsb.getDeviceList());
            }
        });
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
                            serialHistory = serialHistory + CbTimeUtil.currentTime() + " : "
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
                    // 为能将字符正确转换为16进制，此处输入需为偶数个，即字符串字符需为为0~F
                    byte[] bytes = CbByteUtils.hexStr2bytes(mEditText.getText().toString());
                    // 建议给串口设备发送命令时，data写成类似如右：new byte[]{0x1B, 0x69, 0x00};
                    mCbSerialPort.send(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        btn_serial_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serialHistory = "";
                txt_show.setText(serialHistory);
            }
        });
    }

    private void initView() {
        btn_listusb = findViewById(R.id.btn_usblist);
        btn_listserial = findViewById(R.id.btn_seriallist);
        btn_serial_send = findViewById(R.id.btn_serialsenddata);
        txt_show = findViewById(R.id.txt_show);
        mEditText = findViewById(R.id.edit_serial);
        btn_serial_clear = findViewById(R.id.btn_serialcleardata);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mCbUsb.close();
            mCbSerialPort.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
