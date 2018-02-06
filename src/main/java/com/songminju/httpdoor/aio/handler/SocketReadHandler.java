package com.songminju.httpdoor.aio.handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import com.songminju.httpdoor.aio.AioHttpServer;
import com.songminju.httpdoor.aio.http.HttpRequestResolver;

/**
*@author song(mejeesong@qq.com)
*2018年2月6日
*
*/
public class SocketReadHandler implements CompletionHandler<Integer, ByteBuffer> {

	private HttpRequestResolver resolver = null;
	private AsynchronousSocketChannel client = null;
	
	public SocketReadHandler(AioHttpServer httpServer,AsynchronousSocketChannel client) {
		this.client = client;
		resolver = new HttpRequestResolver(httpServer,client);
	}
	
	public void completed(Integer result, ByteBuffer attachment) {
		if(result == -1) {
			try {
				resolver = null;
				this.client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
		attachment.flip();
		byte[] buffer = new byte[result];
		attachment.get(buffer, 0, result);
		attachment.clear();
		try {
			resolver.append(buffer);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				client.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return;
		}
		this.client.read(attachment,attachment,this);
	}

	public void failed(Throwable exc, ByteBuffer attachment) {
		
	}

	
}
