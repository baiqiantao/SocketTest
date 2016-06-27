# SocketTest
一个仿照即时通讯软件编写的简洁版Socket项目

![image](https://github.com/baiqiantao/SocketTest/raw/master/1.png)

/**
 *  Activity启动以后就开启服务，服务开启后就通过ConnectorManager调用Connector中的connect方法对客户端进行认证
 *  认证是通过三次握手完成的，服务器为所有认证的客户端新建一个线程，通过此线程和客户端通讯
 *  当点击发送按钮后，通过ConnectorManager调用Connector中的方法把消息发送到一个阻塞队列中，最终发给服务器
 *  服务器收到消息后将其【转发】给指定客户端的Connector的阻塞队列中，客户端又通过listener.pushData(text)转发消息
 *  由于ConnectorManager注册了Connector的回调，ConnectorService又注册了ConnectorManager的回调
 *  所以最终调用的是ConnectorService的回调方法pushData(data) ，而此方法又通过发送一条广播将消息转发出去
 *  此广播会被PushReceiver接收到，因为其是在MainActivity注册的，所以最终MainActivity也收到了服务器转发过来的消息
 */
