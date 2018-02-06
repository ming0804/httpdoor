package com.songminju.httpdoor;

import com.songminju.httpdoor.http.HttpRequest;
import com.songminju.httpdoor.http.HttpResponse;

/**
*@author song(mejeesong@qq.com)
*2018年2月6日
*
*/
public interface HttpRequestHandler {
	void handle(HttpRequest req,HttpResponse res);
}
