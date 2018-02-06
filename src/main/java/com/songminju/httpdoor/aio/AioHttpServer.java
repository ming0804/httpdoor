package com.songminju.httpdoor.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.songminju.httpdoor.HttpRequestHandler;
import com.songminju.httpdoor.HttpServer;
import com.songminju.httpdoor.HttpServerConfig;
import com.songminju.httpdoor.HttpServerState;
import com.songminju.httpdoor.aio.handler.SocketAcceptHandler;

/**
*@author song(mejeesong@qq.com)
*2018年2月6日
*
*/
public class AioHttpServer implements HttpServer{
	private HttpServerConfig config = null;
	private AsynchronousServerSocketChannel serverSocket = null;
	private HttpRequestHandler handler = null;
	
	public AioHttpServer(HttpServerConfig config) {
		this.config = config;
	}

	public void start() throws IOException {
		ExecutorService threadPool = Executors.newFixedThreadPool(20);
		AsynchronousChannelGroup group = AsynchronousChannelGroup.withThreadPool(threadPool);
		serverSocket = AsynchronousServerSocketChannel.open(group);
		serverSocket.bind(new InetSocketAddress(config.address, config.port));
		serverSocket.accept(null, new SocketAcceptHandler(this));
	}

	public void shutdown() {
		
	}

	public void stop() {
	}

	public HttpServerState state() {
		return null;
	}

	public HttpServerConfig getConfig() {
		return config;
	}

	public void setConfig(HttpServerConfig config) {
		this.config = config;
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
