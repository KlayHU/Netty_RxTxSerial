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

    /**
     * 长度:六个字节
     * 指对协议前六个字节的处理
     */
    private int wordlength=6;

    /**
     * 消息长度所在位置
     */
    private int msglengthindex=1;


    private static ChannelGroup channelGroup=new serverInnitializer().channelGroup;
    public static AttributeKey<Node> ATTR_GATEWAY_KEY = AttributeKey.valueOf("gateway");

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> list) throws Exception {
        System.out.println("服务器收到消息!");
        int canReadCount = in.readableBytes();

        if (canReadCount < wordlength) {
            return;
        }

        int acredindex = in.readerIndex();
        int frameLength = in.getByte(acredindex + msglengthindex);    //在可读取空间取前两个字节
        System.out.println("解析到的长度：" + frameLength);

        if (canReadCount == frameLength) {
            byte[] dest = new byte[canReadCount];
            in.getBytes(acredindex, dest, 0, canReadCount);
            String string = bytesToHexString(dest);
            System.out.println("打印:");
            System.out.println("收到的消息是：" + string);
            //ctx.flush();
        }
        if(canReadCount < frameLength )
        {
            byte [] dust = new byte[frameLength];
            in.getBytes(acredindex,dust,0,frameLength);
            String string = bytesToHexString(dust);
            System.out.println("收到的消息小于实际接收的长度!");
            System.out.println("打印:\n"+string);
            ctx.flush();
        }
        if(canReadCount > frameLength )
        {
            byte [] dust = new byte[frameLength];
            in.getBytes(acredindex,dust,0,frameLength);
            String string = bytesToHexString(dust);
            System.out.println("收到的消息大于实际解析的长度！");
            System.out.println("打印：\n"+string);
//            ctx.flush();
//            acredindex =frameLength+1;
//
//            while(canReadCount-frameLength!=frameLength){
//                byte [] dusts = new byte[frameLength];
//                in.getBytes(acredindex+frameLength,dusts,0,frameLength);
//                String strings = bytesToHexString(dusts);
//                if(canReadCount-frameLength>=frameLength){
//                    System.out.println("消息大于解析时再次解析后的数据是\n:"+strings);
//                    canReadCount=canReadCount-frameLength;
//
//                    }
//                    ctx.flush();
//                if(canReadCount<frameLength) {
//
//                    frameLength=canReadCount;
//                    byte [] dustss = new byte[frameLength];
//                    in.getBytes(acredindex+canReadCount,dustss,0,frameLength);
//
//                    String stringss = bytesToHexString(dustss);
//                    System.out.println("消息小于解析时再次解析后的数据是\n"+stringss);
//
//                }
//                ctx.flush();
//            }
//
//                in.clear();
        }
            acredindex += 2;


            MsgRevData MsgRevData = new MsgRevData();

            in.readShort();

            //消息编号
            MsgRevData.setMsgID(in.readShort());
            //Lora编号
            MsgRevData.setLora_numb(in.readShort());

            //消息
            byte[] msg = new byte[canReadCount - 6];
            in.getBytes(acredindex + 4, msg);
            in.readerIndex(0);

            in.clear();

            MsgRevData.setData(msg);
            System.out.println();

            //将数据封装并发送到串口
            GetSerialPorts getSerialPorts = null;
            Map<String, GetSerialPorts> portMap = PortManager.portMap;
            for (String key : portMap.keySet()) {
                if ((MsgRevData.getLora_numb() == 0) && key.equals("COM1")) {
                    getSerialPorts = portMap.get(key);
                } else if ((MsgRevData.getLora_numb() == 1) && key.equals("COM3")) {
                    getSerialPorts = portMap.get(key);
                }
            }
            System.out.println(getSerialPorts);

            GetSerialPorts.sendData(getSerialPorts.serialPort1, msg);
            ctx.flush();

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
        ByteBuf heapBuf = ByteBufAllocator.DEFAULT.buffer(64, 256);
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
