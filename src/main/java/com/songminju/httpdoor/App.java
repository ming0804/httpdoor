package com.songminju.httpdoor;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.songminju.httpdoor.aio.AioHttpServer;
import com.songminju.httpdoor.http.HttpRequest;
import com.songminju.httpdoor.http.HttpResponse;

/**
 * Hello world!
 *
 */
public class App {
	private static Object holder = new Object();
	private static Logger logger = LoggerFactory.getLogger(App.class);
	public static void main(String[] args) {
		HttpServer httpServer = new AioHttpServer(new HttpServerConfig());
		httpServer.setHttpRequestHandler(new HttpRequestHandler() {
			private String dir = "/home/song/temp";
			@Override
			public void handle(HttpRequest req, HttpResponse res) {
				res.append("123");
				try {
					res.end();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}	
		});
		try {
			httpServer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		while(true) {
			logger.debug(httpServer.getState().getInfo());
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
