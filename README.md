# Netty_RxTxSerial
通过java编写，基于Netty框架的串口和Socket通信消的息处理服务器。
已成功部署在Linux服务器虚拟串口

需要添加的外部jar包：RXTXcomm.jar

/*11.8 08:54更新*/

应对串口发送速率大于10ms时会出现的多条消息由一个包发出的问题，根据netty提供的Decoder解决，
这样存放和解析消息的数组就不会越界了。


/*11.09 15：:32更新*/

适配了linux下的虚拟串口，精简了部分代码，
