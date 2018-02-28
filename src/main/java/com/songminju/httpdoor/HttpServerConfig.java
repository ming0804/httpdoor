package com.songminju.httpdoor;

/**
*@author song(mejeesong@qq.com)
*2018年2月6日
*
*/
public class HttpServerConfig {
	public int port = 9000;
	public String address = "0.0.0.0";
	public int maxConnection = 3000;
	public int maxConcurrent = 1000;
	public int readWait = 3;
	public int requestWait = 300;
	public final Object connectionLock = new Object();
	public final Object concurrentLock = new Object();
}