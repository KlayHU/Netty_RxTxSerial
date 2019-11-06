package netty.handler;
import Lora.recive.MsgSendData;
import Lora.recive.Node;
import Lora.send.MsgRevData;
import Test.GetSerialPorts;
import Test.PortManager;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import netty.Innitializer.serverInnitializer;

import java.util.List;
import java.util.Map;

public class msgHandler extends ByteToMessageDecoder {

    private static ChannelGroup channelGroup=new serverInnitializer().channelGroup;
    public static AttributeKey<Node> ATTR_GATEWAY_KEY = AttributeKey.valueOf("gateway");

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> list) throws Exception {
        System.out.println("服务器收到消息!");
        int canReadCount=in.readableBytes();
        System.out.println("经过这里");
        int Acredindex=in.readerIndex();
        int frameLength=in.getShort(Acredindex);
        System.out.println("到达这里");
        byte[] dest=new byte[frameLength];
        in.getBytes(Acredindex,dest,0,canReadCount);

        String string = bytesToHexString(dest);
        System.out.println("打印");
        System.out.println("收到的消息是："+string);


        Acredindex+=2;


        MsgRevData MsgRevData = new MsgRevData();

        in.readShort();

        //消息编号
        MsgRevData.setMsgID(in.readShort());
        //Lora编号
        MsgRevData.setLora_numb(in.readShort());

        //消息
        byte[] msg=new byte[canReadCount-6];
        in.getBytes(Acredindex+4,msg);
        in.readerIndex(0);

        in.clear();

        MsgRevData.setData(msg);
        System.out.println();

        //将数据封装并发送到串口
        GetSerialPorts getSerialPorts=null;
        Map<String, GetSerialPorts> portMap = PortManager.portMap;
        for(String key : portMap.keySet()){
            if((MsgRevData.getLora_numb()==0)&&key.equals("COM1")){
                getSerialPorts= portMap.get(key);
            }else if((MsgRevData.getLora_numb()==1)&&key.equals("COM3")){
                getSerialPorts=portMap.get(key);
            }
        }
        System.out.println(getSerialPorts);

        GetSerialPorts.sendData(getSerialPorts.serialPort1,msg);

    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        byte[] bytes = new byte[1];
        bytes[0]=6;
        channelGroup.add(ctx.channel());
        MsgSendData msgRevData = new MsgSendData();
        msgRevData.setLora_numb((short) 0);
        msgRevData.setMsgID((short) 1);
        msgRevData.setLora_numb((short)0);
        msgRevData.setMsgLength((short) (bytes.length+6));
    }



    /**
     * 保存eui到Channel
     * @param ctx
     * @param eui
     */
    public static  void updateChannel(ChannelHandlerContext ctx,String eui){
        //保存网关信息到Channel
        Attribute<Node> gatewayattr = ctx.channel().attr(ATTR_GATEWAY_KEY);
        Node node = gatewayattr.get();
        System.out.println(node);

        if (node == null){
            node = new Node();
            gatewayattr.set(node);
        }
        node.setCtx(ctx);
        node.setId(eui);
        gatewayattr.set(node);

    }


    public static void sendMsg(MsgSendData msgRevData,byte[] bytes){

        //分配capacity为16，maxCapacity为256的byteBuf
        ByteBuf heapBuf = ByteBufAllocator.DEFAULT.buffer(16, 256);
        //返回可写字节数
        System.out.println(heapBuf.writableBytes());
        //判断是否可写
        System.out.println(heapBuf.isWritable());
        heapBuf.writeShort(msgRevData.getMsgLength());//改长度为加上消息的所有长度
        heapBuf.writeShort(msgRevData.getMsgID());
        heapBuf.writeShort(msgRevData.getLora_numb());
        heapBuf.writeBytes(bytes);

        byte[] dest = new byte[msgRevData.getMsgLength()];

        heapBuf.getBytes(0,dest,0,msgRevData.getMsgLength());
        String string = bytesToHexString(dest);
        System.out.println(string);
        for (Channel channel:channelGroup) {
            channel.writeAndFlush(heapBuf);
        }

    }





    private static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            hv += " ";
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
}
