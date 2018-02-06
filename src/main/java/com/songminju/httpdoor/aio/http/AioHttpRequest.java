package com.songminju.httpdoor.aio.http;

import java.util.HashMap;
import java.util.Map;

import com.songminju.httpdoor.http.HttpRequest;

/**
*@author song(mejeesong@qq.com)
*2018年2月6日
*
*/
public class AioHttpRequest implements HttpRequest {
	public static final String METHOD_GET = "GET";
	public static final String METHOD_POST = "POST";
	public static final String METHOD_PUT = "PUT";
	public static final String METHOD_DELETE = "DELETE";
	
	public String method = METHOD_GET;
	public String uri;
	public String query = "";
	private String posts;
	public final Map<String,String> headers = new HashMap<>();
	@Override
	public String toString() {
		return "AioHttpRequest [method=" + method + ", uri=" + uri + ", query=" + query + ", posts=" + posts
				+ ", headers=" + headers + "]";
	}
	
}