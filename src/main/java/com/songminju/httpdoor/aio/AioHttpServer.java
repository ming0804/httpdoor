package com.songminju.httpdoor.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.songminju.httpdoor.HttpRequestHandler;
import com.songminju.httpdoor.HttpServer;
import com.songminju.httpdoor.HttpServerConfig;
import com.songminju.httpdoor.HttpServerState;
import com.songminju.httpdoor.aio.handler.SocketAcceptHandler;
import com.songminju.httpdoor.aio.handler.SocketReadHandler;

/**
*@author song(mejeesong@qq.com)
*2018年2月6日
*
*/
public class AioHttpServer implements HttpServer{
	private static Logger LOGGER = LoggerFactory.getLogger(AioHttpServer.class);
	private HttpServerConfig config = null;
	private AsynchronousServerSocketChannel serverSocket = null;
	private HttpRequestHandler handler = null;
	private HttpServerState state = null;
	
	public AioHttpServer(HttpServerConfig config) {
		this.config = config;
		state = new HttpServerState(this);
		init();
	}
	
	public void init() {

	}

	public void start() throws IOException {
		ExecutorService threadPool = Executors.newFixedThreadPool(20);
		AsynchronousChannelGroup group = AsynchronousChannelGroup.withThreadPool(threadPool);
		serverSocket = AsynchronousServerSocketChannel.open(group);
		serverSocket.bind(new InetSocketAddress(config.address, config.port));
		serverSocket.accept(null, new SocketAcceptHandler(this));
		LOGGER.info("Httpdoor had started,listening {}:{}",config.address,config.port);
	}

	public void shutdown() {
		
	}

	public void stop() {
		
	}

	public HttpServerState getState() {
		return state;
	}
	
	@Override
	public HttpServerConfig getConfig() {
		return config;
	}


	public AsynchronousServerSocketChannel getServerSocket() {
		return serverSocket;
	}

	public void setServerSocket(AsynchronousServerSocketChannel serverSocket) {
		this.serverSocket = serverSocket;
	}

	@Override
	public void setHttpRequestHandler(HttpRequestHandler handler) {
		this.handler = handler;
	}

	@Override
	public HttpRequestHandler getHttpRequestHandler() {
		return handler;
	}
	
}
