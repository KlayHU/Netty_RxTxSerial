package netty.handler;

import Lora.recive.MsgSendData;
import Lora.send.MsgRevData;
import Test.GetSerialPorts;
import Test.PortManager;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import netty.Innitializer.serverInnitializer;
import org.apache.log4j.Logger;

import java.util.Map;

public class msghandler extends SimpleChannelInboundHandler<ByteBuf> {
    private static Logger logger = Logger.getLogger(msghandler.class);
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        logger.info("================================================");
        logger.info("**********   节点  》》》》》》  串口   **********");

        int canReadCount = in.readableBytes();

        int acredindex = in.readerIndex();

//        int frameLength = in.getByte(acredindex + msglengthindex);    //在可读取空间取前两个字节

        short frameLength = in.getShort(acredindex);

        logger.info("      解析到的长度：" + frameLength+"      ");

        if (canReadCount == frameLength) {
            byte[] dest = new byte[canReadCount];
            in.getBytes(acredindex, dest, 0, canReadCount);
            String string = bytesToHexString(dest);

            logger.info("      收到的消息是：" + string+"      ");
            logger.info("================================================");
            //ctx.flush();

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
//          System.out.println();

            //将数据封装并发送到串口
            GetSerialPorts getSerialPorts = null;
            Map<String, GetSerialPorts> portMap = PortManager.portMap;
            for (String key : portMap.keySet()) {
                if ((MsgRevData.getLora_numb() == 0) && key.equals("/dev/ttyUSB02")) {
                    getSerialPorts = portMap.get(key);
                } else if ((MsgRevData.getLora_numb() == 1) && key.equals("/dev/ttyUSB04")) {
                    getSerialPorts = portMap.get(key);
                }
            }

            logger.info("=====发送到串口   "+"     "+getSerialPorts.getSerialPortName()+"     ======");
            logger.info("           数据为   ："+bytesToHexString(msg)+"           ");
            logger.info("==================================================");

//          Thread.sleep(30);
            GetSerialPorts.sendData(getSerialPorts.serialPort1, msg);
            ctx.flush();
        }else {
            byte[] dest = new byte[in.readableBytes()];
            in.getBytes(acredindex, dest, 0, in.readableBytes());
            String string = bytesToHexString(dest);
            logger.error("消息错误！！！！！！>>>>>>>>>>>"+string+"<<<<<<<<<<<");
        }
    }

    private static ChannelGroup channelGroup=new serverInnitializer().channelGroup;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        byte[] bytes = new byte[1];
        bytes[0]=6;
        channelGroup.add(ctx.channel());
        MsgSendData msgRevData = new MsgSendData();
//        msgRevData.setLora_numb((short) 0);
        msgRevData.setMsgID((short) 1);
        msgRevData.setLora_numb((short)1);
        msgRevData.setMsgLength((short) (bytes.length+6));
    }

    public static void sendMsg(MsgSendData msgRevData,byte[] bytes){

        //分配capacity为16，maxCapacity为256的byteBuf
        ByteBuf heapBuf = ByteBufAllocator.DEFAULT.buffer(64,320);

        heapBuf.writeShort(msgRevData.getMsgLength());//改长度为加上消息的所有长度
        heapBuf.writeShort(msgRevData.getMsgID());
        heapBuf.writeShort(msgRevData.getLora_numb());
        heapBuf.writeBytes(bytes);

        byte[] dest = new byte[msgRevData.getMsgLength()];

        heapBuf.getBytes(0,dest,0,msgRevData.getMsgLength());
        String string = bytesToHexString(dest);
        logger.info("**********   节点  《《《《《《  串口   **********");
        logger.info("       发送的消息为："+string+"     ");
        logger.info("===============================================");
//        System.out.println(string);
        for (Channel channel:channelGroup) {
            channel.writeAndFlush(heapBuf);
        }

    }

    public static String bytesToHexString(byte[] src){
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
