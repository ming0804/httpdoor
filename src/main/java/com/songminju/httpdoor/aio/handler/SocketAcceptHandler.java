package com.songminju.httpdoor.aio.handler;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;

import com.songminju.httpdoor.aio.AioHttpServer;

/**
*@author song(mejeesong@qq.com)
*2018年2月6日
*
*/
public class SocketAcceptHandler implements CompletionHandler<AsynchronousSocketChannel,Void> {

	private AsynchronousServerSocketChannel serverSocket = null;
	private AioHttpServer httpServer = null;
	
	public SocketAcceptHandler(AioHttpServer httpServer) {
		this.serverSocket = httpServer.getServerSocket();
		this.httpServer = httpServer;
	}
	
	public void completed(AsynchronousSocketChannel client, Void attachment) {
		serverSocket.accept(attachment, this);
		ByteBuffer byteBuf = ByteBuffer.allocate(256);
		client.read(byteBuf,3L,TimeUnit.SECONDS,byteBuf, new SocketReadHandler(httpServer,client));
	}
	
	public void failed(Throwable e, Void attachment) {
		e.printStackTrace();
	}
}
