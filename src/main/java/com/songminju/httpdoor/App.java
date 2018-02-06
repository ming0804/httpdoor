package com.songminju.httpdoor;

import java.io.IOException;

import com.songminju.httpdoor.aio.AioHttpServer;
import com.songminju.httpdoor.http.HttpRequest;
import com.songminju.httpdoor.http.HttpResponse;

/**
 * Hello world!
 *
 */
public class App {
	private static Object holder = new Object();
	public static void main(String[] args) {
		HttpServer httpServer = new AioHttpServer(new HttpServerConfig());
		httpServer.setHttpRequestHandler(new HttpRequestHandler() {
			@Override
			public void handle(HttpRequest req, HttpResponse res) {
				res.append("hello this is server.\n");
				res.append("this is next line.");
				res.write("write".getBytes());
				res.end();
			}
		});
		try {
			httpServer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		synchronized (holder) {
			try {
				holder.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
