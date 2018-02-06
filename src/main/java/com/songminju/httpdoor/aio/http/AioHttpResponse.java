package com.songminju.httpdoor.aio.http;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.songminju.httpdoor.http.HttpResponse;

/**
*@author song(mejeesong@qq.com)
*2018年2月6日
*
*/
public class AioHttpResponse implements HttpResponse{
	
	//保证单线程写，不必考虑多线程
	private static final HashMap<String,String> baseHeaders = new HashMap<>();
	private static final byte[] rn = "\r\n".getBytes();
	private static final int STATE_END = 0;
	private static final int STATE_RESP_LINE = 1;
	private static final int STATE_HEADER = 2;
	private static final int STATE_BODY = 3;
	private static final String MSG_OK = "OK";
	
	static {
		baseHeaders.put("Server", "httpdoor");
		baseHeaders.put("Content-Type","text/html; charset=utf-8");
		baseHeaders.put("Transfer-Encoding", "chunked");
	}
	private int code = 200;
	private int state = STATE_RESP_LINE;
	private String msg = MSG_OK;
	private Map<String,String> headers = null;
	private AsynchronousSocketChannel client;
	private int bufferLen = 128;
	private ByteBuffer buffer = ByteBuffer.allocate(bufferLen);
	
	private StringBuilder context = new StringBuilder();
	
	@SuppressWarnings("unchecked")
	public AioHttpResponse(AsynchronousSocketChannel client) {
		headers = (Map<String, String>)baseHeaders.clone();
		this.client = client;
	}
	
	@Override
	public String getHeader(String name) {
		return headers.get(name);
	}

	@Override
	public void setHeader(String name, String value) {
		headers.put(name, value);
	}

	@Override
	public int getState() {
		return code;
	}

	@Override
	public void setState(int code) {
		this.code = code;
	}

	@Override
	public void append(String str) {
		context.append(str);
	}

	@Override
	public void write(byte[] data) {
		flushNow();
		writeChunk(data);
	}

	@Override
	public void end() {
		flushNow();
		buffer.clear();
		buffer.put("0\r\n\r\n".getBytes());
		buffer.flip();
		client.write(buffer);
		state = STATE_END;
	}
	
	private void flushNow() {
		if(state == STATE_RESP_LINE) {
			state = STATE_HEADER;
			buffer.clear();
			String respLine = "HTTP/1.1 "+code+" "+msg+"\r\n";
			buffer.put(respLine.getBytes());
			buffer.flip();
			client.write(buffer);
		}
		if(state == STATE_HEADER) {
			state = STATE_BODY;
			Set<String> keys = headers.keySet();
			for(String key:keys) {
				String values = headers.get(key);
				writeBytes((key+":"+values+"\r\n").getBytes());
			}
			writeBytes(rn);
		}
		if(context.length() > 0) {
			writeChunk(context.toString().getBytes());
			context.setLength(0);
		}
	}
	private void writeBytes(byte[] data) {
		if(data.length <= bufferLen) {
			buffer.clear();
			buffer.put(data);
			buffer.flip();
			client.write(buffer);
		}else {
			ByteBuffer buf = ByteBuffer.allocate(data.length);
			buf.clear();
			buf.put(data);
			buf.flip();
			client.write(buf);
		}
	}
	private void writeChunk(byte[] data) {
		ByteBuffer buf = ByteBuffer.allocate(data.length+6);
		buf.clear();
		String hex = Integer.toHexString(data.length);
		buf.put(hex.getBytes());
		buf.put(rn);
		buf.flip();
		client.write(buf);
		buf.clear();
		buf.put(data);
		buf.put(rn);
		buf.flip();
		client.write(buf);
	}

}
