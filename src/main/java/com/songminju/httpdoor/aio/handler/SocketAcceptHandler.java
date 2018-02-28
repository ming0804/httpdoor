package com.songminju.httpdoor.aio.handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.songminju.httpdoor.HttpServerConfig;
import com.songminju.httpdoor.HttpServerState;
import com.songminju.httpdoor.aio.AioHttpServer;

/**
*@author song(mejeesong@qq.com)
*2018年2月6日
*
*/
public class SocketAcceptHandler implements CompletionHandler<AsynchronousSocketChannel,Void>,Cloneable {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private AsynchronousServerSocketChannel serverSocket = null;
	private AioHttpServer httpServer = null;
	private HttpServerState state = null;
	private HttpServerConfig config = null;
	
	public SocketAcceptHandler(AioHttpServer httpServer) {
		this.serverSocket = httpServer.getServerSocket();
		this.httpServer = httpServer;
		state = httpServer.getState();
		config = httpServer.getConfig();
	}
	@Override
	public void completed(AsynchronousSocketChannel client, Void attachment) {
		try {
			logger.debug("accept a socket:"+client.getRemoteAddress().toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		int n = state.incrementAndGet(HttpServerState.FIELD_CONNECTION);
		
		ByteBuffer byteBuf = ByteBuffer.allocate(256);
		client.read(byteBuf,30L,TimeUnit.SECONDS,byteBuf, new SocketReadHandler(httpServer,client));
		
		if(n < config.maxConnection) {
			serverSocket.accept(attachment, this);
		}else {
			//
		}
	}
	@Override
	public void failed(Throwable e, Void attachment) {
		e.printStackTrace();
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
