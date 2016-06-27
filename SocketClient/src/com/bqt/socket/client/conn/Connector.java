package com.bqt.socket.client.conn;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;

/**用一个类把与Socket相关的连接、发送消息、断开连接等方法抽离出来，并通过回调方式把结果返回*/
public class Connector {
	public static final String DST_NAME = "192.168.31.165";
	public static final int DST_PORT = 10002;
	private Socket client;
	//有界阻塞队列，当容量满时往BlockingQueue中添加数据时会阻塞，当容量为空时取元素操作会阻塞。
	private ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<String>(8);
	private ConnectorListener listener;

	/**连接*/
	public void connect() {
		try {
			// 三次握手
			if (client == null || client.isClosed()) client = new Socket(DST_NAME, DST_PORT);

			new Thread(new Runnable() {
				@Override
				public void run() {
					// 数据通讯
					OutputStream os;
					try {
						os = client.getOutputStream();
						// os.write(content.getBytes());
						while (true) {
							String content = queue.take();
							os.write(content.getBytes());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();

			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						InputStream is = client.getInputStream();
						byte[] buffer = new byte[1024];
						int len = -1;
						while ((len = is.read(buffer)) != -1) {
							final String text = new String(buffer, 0, len);
							System.out.println("服务器转发的消息 : " + text);
							//获取服务器向客户端转发的消息
							if (listener != null) listener.pushData(text);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**认证*/
	public void auth(String auth) {
		putRequest(auth);
	}

	/**发送消息*/
	public void putRequest(String content) {
		try {
			queue.put(content);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**断开连接*/
	public void disconnect() {
		try {
			if (client != null && !client.isClosed()) {
				client.close();
				client = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setConnectorListener(ConnectorListener listener) {
		this.listener = listener;
	}
}