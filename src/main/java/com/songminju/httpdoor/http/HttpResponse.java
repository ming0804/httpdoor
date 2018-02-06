package com.songminju.httpdoor.http;

/**
*@author song(mejeesong@qq.com)
*2018年2月6日
*
*/
public interface HttpResponse {
	String getHeader(String name);
	void setHeader(String name,String value);
	int getState();
	void setState(int code);
	void append(String str);
	void write(byte[] data);
	void end();
}
