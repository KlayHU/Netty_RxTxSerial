package netty;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import netty.Innitializer.serverInnitializer;


public class nettyConfig implements Runnable {

    public void run(){
        NioEventLoopGroup boss=new NioEventLoopGroup();
        NioEventLoopGroup work=new NioEventLoopGroup();
        ServerBootstrap b=new ServerBootstrap();

        b.group(boss,work)
                .channel(NioServerSocketChannel.class)
                .childHandler(new serverInnitializer());
        try {
            ChannelFuture sync = b.bind(9989).sync();
            sync.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }
    }
}
