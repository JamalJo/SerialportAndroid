智能柜-硬件SDK Demo


1.所有串口：
 ﻿COM0：console
  COM1:MXC1
  COM2：mxc2
  COM3:mxc3
  COM4:485
  
2.串口发送数据时，只支持发送十六进制，故而输入时，需要控制字符串为双数且为0~F
  真实使用时，串口通信协议采用byte[]