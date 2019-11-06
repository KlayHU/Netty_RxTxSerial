package Lora.send;

import Lora.MsgLoraHeader;
import io.netty.buffer.ByteBuf;

/**
 * @description: 2.2.2 LoRa阅读器节点发送数据给设备平台 -> 网络系统中的上行链路
 * @author: KlayHu
 * @create: 2019/11/4 19:42
 **/
public class MsgRevData extends MsgLoraHeader {

    /**
     *数据
     */
    private byte[] data;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }


    /**
     * 消息内容长度

     */
    private static final short msgLength=10;
}
