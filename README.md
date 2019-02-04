# 串口SDK

模块说明
---
* cbserialport ： 串口sdk

* app ： 示例用法

用法
---
* 初始化串口
```
mCbSerialPort = CbSerialPortService.newInstance();
mCbSerialPort.open(CbSerialPortConstants.PORT2);
```

* 设置波特率
```
mCbSerialPort.open(CbSerialPortConstants.PORT2,BAUD9600);
```

* 接收串口数据
```
mCbSerialPort.setReceiver(new CbSerialPortReceiver() {
    @Override
    public void onReceive(final byte[] response, final int size) {
        //接收串口数据
        Log.d(TAG, "onReceive: " + CbByteUtils.bytes2HexStr(response));
    }
});
```
* 发送串口数据
```
mCbSerialPort.send(bytes);  // bytes是byte数组
```