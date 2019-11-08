package netty.Innitializer;


import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.util.concurrent.GlobalEventExecutor;
import netty.handler.msgHandler;

public class serverInnitializer extends ChannelInitializer<Channel> {

    public static ChannelGroup channelGroup=new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    @Override
    protected void initChannel(Channel channel) throws Exception {

        ChannelPipeline pipeline = channel.pipeline();//1
        pipeline.addLast(new LengthFieldBasedFrameDecoder(64*1024,0,2,-2,0));

        pipeline.addLast(new msgHandler());

    }
}
