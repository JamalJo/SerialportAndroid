package com.cupboard.cupboardhardware;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.IOException;

import serial.CbSerialPort;
import serial.CbSerialPortConstants;
import serial.CbSerialPortReceiver;
import serial.CbSerialPortService;
import serial.utils.SerialPortDataUtils;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "jamaljo";
    CbSerialPort mCbSerialPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCbSerialPort = new CbSerialPortService();
        try {
            mCbSerialPort.open(CbSerialPortConstants.PORT1);
            mCbSerialPort.setReceiver(new CbSerialPortReceiver() {
                @Override
                public void onReceive(byte[] response) {
                    Log.d(TAG, "onReceive: " + SerialPortDataUtils.ByteArrToHex(response));
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        initView();
    }

    private void initView() {
        findViewById(R.id.btn_seriallist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "" + mCbSerialPort.getAllPortName());
            }
        });
        findViewById(R.id.btn_serialconfiguer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mCbSerialPort.send(SerialPortDataUtils.HexToByteArr("12"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
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
