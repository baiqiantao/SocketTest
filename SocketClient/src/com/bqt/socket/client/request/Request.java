package com.bqt.socket.client.request;

/**请求的数据有各种各样的格式，我们为其定义一个统一的接口，所有发送的信息都可以用其子类进行封装*/
public interface Request {
	String getData();
}