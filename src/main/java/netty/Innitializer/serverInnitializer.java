package netty.Innitializer;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.util.concurrent.GlobalEventExecutor;
import netty.handler.msghandler;

public class serverInnitializer extends ChannelInitializer<Channel> {

    public static ChannelGroup channelGroup=new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    @Override
    protected void initChannel(Channel channel) throws Exception {

        ChannelPipeline pipeline = channel.pipeline();//1
//        pipeline.addLast(new LineBasedFrameDecoder(64*1024,true,true));
        ByteBuf delimiter = Unpooled.copiedBuffer("\r\n".getBytes());
        pipeline.addLast(new DelimiterBasedFrameDecoder(2048, delimiter));
//        pipeline.addLast(new LengthFieldBasedFrameDecoder(64*1024,0,2,-2,0));
//        pipeline.addLast(new msgHandler());
        pipeline.addLast(new msghandler());
    }
}
