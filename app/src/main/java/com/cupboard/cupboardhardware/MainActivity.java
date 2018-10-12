package com.cupboard.cupboardhardware;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.cupboard.serial.SerialPortUtil;

import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "jamaljo";
    SerialPortUtil mSerialPortUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSerialPortUtil = new SerialPortUtil();
        mSerialPortUtil.openSerialPort();
        initView();
    }

    private void initView() {
        findViewById(R.id.btn_seriallist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "" + mSerialPortUtil.getAllSerialPort());
            }
        });
        findViewById(R.id.btn_serialconfiguer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSerialPortUtil.sendSerialPort(new Date().getTime()+"");
            }
        });
    }
}
