package Lora;

/**
 * @description: 设备平台消息公共部分
 * @author: KlayHu
 * @create: 2019/11/3 21:18
 **/
public class MsgLoraHeader {
    /**
     * 消息内容长度
     */
    private short msgLength ;

    /**
     * 消息id
     */
    private short msgID ;

    /**
     * Lora编号
     */
    private short Lora_numb ;

    public short getMsgLength() {
        return msgLength;
    }

    public void setMsgLength(short msgLength) {
        this.msgLength = msgLength;
    }

    public short getMsgID() {
        return msgID;
    }

    public void setMsgID(short msgID) {
        this.msgID = msgID;
    }

    public short getLora_numb() {
        return Lora_numb;
    }

    public void setLora_numb(short lora_numb) {
        Lora_numb = lora_numb;
    }

    @Override
    public String toString() {
        return "MsgLoraHandler{" +
                "msgLength=" + msgLength +
                ", msgID=" + msgID +
                ", Lora_numb=" + Lora_numb +
                '}';
    }
}
