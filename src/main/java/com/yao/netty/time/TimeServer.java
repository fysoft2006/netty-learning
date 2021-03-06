package com.yao.netty.time;

import com.yao.netty.echo.EchoServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created by root on 15-2-11.
 */
public class TimeServer {
    private int port;

    public TimeServer(int port) {
        this.port = port;
    }

    public void run(){
        EventLoopGroup bossGroup=new NioEventLoopGroup();
        EventLoopGroup workerGroup=new NioEventLoopGroup();
        try {


            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new TimeServerHandler());
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture future = b.bind(port).sync();

            future.channel().closeFuture().sync();
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }


    }

    /**
     * rdate -o 9999 -p localhost
     * @param args
     */
    public static void main(String[]args){
        int port;
        if(args.length>0){
            port=Integer.parseInt(args[0]);
        }else{
            port=9999;
        }
        new TimeServer(port).run();
    }
}
