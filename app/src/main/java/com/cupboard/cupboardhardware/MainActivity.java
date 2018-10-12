package com.cupboard.cupboardhardware;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.cupboard.serial.SerialPortUtil;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    SerialPortUtil mSerialPortUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSerialPortUtil = new SerialPortUtil();
        initView();
    }

    private void initView() {
        findViewById(R.id.btn_seriallist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mSerialPortUtil.openSerialPort();
                Log.d(TAG, "SerialPortUtil "+mSerialPortUtil.getAllSerialPort());
            }
        });
    }
}
