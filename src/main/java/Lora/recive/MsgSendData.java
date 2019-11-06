package Lora.recive;

import Lora.MsgLoraHeader;
import io.netty.buffer.ByteBuf;

/**
 * @description: MsgSendData2.2.1 设备平台发送数据给LoRa阅读器  -> 网络系统中的下行链路
 * @author: KlayHu
 * @create: 2019/11/4 19:19
 **/
public class MsgSendData extends MsgLoraHeader{
    /**
     *数据
     */
    private ByteBuf byteBuf;

    /**
     * 消息长度
     */
    public static final short msgLength=7;

    public ByteBuf getByteBuf() {
        return byteBuf;
    }

    public void setByteBuf(ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
    }

}
