package com.songminju.httpdoor.aio.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.songminju.httpdoor.http.HttpResponse;

/**
 * @author song(mejeesong@qq.com) 2018年2月6日
 *
 */
public class AioHttpResponse implements HttpResponse {
	
	class SocketWriteHandler implements CompletionHandler<Integer,Void> {

		@Override
		public void completed(Integer result, Void attachment) {
			canWrite = true;
		}

		@Override
		public void failed(Throwable exc, Void attachment) {
			canWrite = true;
		}
		
	}

	// 保证单线程写，不必考虑多线程
	private static final HashMap<String, String> baseHeaders = new HashMap<>();
	private static final byte[] rn = "\r\n".getBytes();
	private static final int STATE_END = 0;
	private static final int STATE_RESP_LINE = 1;
	private static final int STATE_HEADER = 2;
	private static final int STATE_BODY = 3;
	private static final String MSG_OK = "OK";

	private Logger logger = null;

	static {
		baseHeaders.put("Server", "httpdoor");
		baseHeaders.put("Content-Type", "text/html; charset=utf-8");
		baseHeaders.put("Transfer-Encoding", "chunked");
	}
	private int code = 200;
	private int state = STATE_RESP_LINE;
	private String msg = MSG_OK;
	private Map<String, String> headers = null;
	private AsynchronousSocketChannel client;
	private int bufferLen = 128;
	private ByteBuffer buffer = ByteBuffer.allocate(bufferLen);
	private ByteArrayOutputStream baos = new ByteArrayOutputStream();
	private SocketWriteHandler writeHandler = new SocketWriteHandler();
	private Boolean canWrite = true;

	

	@SuppressWarnings("unchecked")
	public AioHttpResponse(AsynchronousSocketChannel client) {
		headers = (Map<String, String>) baseHeaders.clone();
		this.client = client;
		try {
			logger = LoggerFactory.getLogger(this.getClass() + client.getRemoteAddress().toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		try {
			flush();
			writeChunk(str.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void write(byte[] data)  throws IOException {
		flush();
		writeChunk(data);
	}

	@Override
	public void flush() throws IOException {
		if (state == STATE_END) {
			return;
		}
		if (state == STATE_RESP_LINE) {
			buffer.clear();
			String respLine = "HTTP/1.1 " + code + " " + msg + "\r\n";
			baos.write(respLine.getBytes());
			state = STATE_HEADER;
		}
		if (state == STATE_HEADER) {
			Set<String> keys = headers.keySet();
			for (String key : keys) {
				String values = headers.get(key);
				baos.write((key + ":" + values + "\r\n").getBytes());

			}
			baos.write(rn);
			state = STATE_BODY;
		}
		int size = baos.size();
		if(size > 0 ) {
			byte[] data = baos.toByteArray();
			baos.reset();
			
			int offset = 0;
			int len = data.length;
			while(offset < len) {
				while(!canWrite) {
					logger.debug("wait canWrite.");
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					logger.debug("canWrite.:{}",canWrite);
				}
				canWrite = false;
				buffer.clear();
				int remain = buffer.remaining();
				if(remain + offset < len) {
					buffer.put(data,offset,remain);
					offset += remain;
					buffer.flip();
					client.write(buffer, null, writeHandler);
				}else {
					buffer.put(data,offset,len - offset);
					buffer.flip();
					client.write(buffer, null, writeHandler);
					break;
				}
			}
		}
	}

	@Override
	public void end()  throws IOException{
		if (state == STATE_END) {
			return;
		}
		baos.write("0\r\n\r\n".getBytes());
		flush();
		state = STATE_END;
	}


	private void writeChunk(byte[] data)  throws IOException {
		byte[] hex = Integer.toHexString(data.length).getBytes();
		baos.write(hex);
		baos.write(rn);
		baos.write(data);
		baos.write(rn);
		flush();
	}
}
