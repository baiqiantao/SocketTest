package com.heima.socket.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class TCPServer {
	private static final int port = 10002;
	/**保存并标记所有建立连接的客户端*/
	private static Map<String, Socket> clients = new LinkedHashMap<String, Socket>();

	public static void main(String[] args) {
		try {
			ServerSocket server = new ServerSocket(port);

			while (true) {
				System.out.println("准备阻塞...");
				// 获得客户端连接，阻塞式方法
				final Socket client = server.accept();
				System.out.println("阻塞完成...");
				//每连接一个客户端就新建一个线程
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							// 获取客户端的输入流，也即客户端发送的数据。
							//注意输入流和输出流相对于内存设备而言，将外设中的数据读取到内存中就是输入
							InputStream is = client.getInputStream();
							// 输出流，给客户端写数据
							OutputStream os = client.getOutputStream();
							byte[] buffer = new byte[1024];
							int len = -1;
							System.out.println("准备read...");
							while ((len = is.read(buffer)) != -1) {
								System.out.println("read完成...");
								String text = new String(buffer, 0, len);
								System.out.println(text);
								//将客户端发送的json串转换为map
								Map<String, String> map = new Gson().fromJson(text, new TypeToken<Map<String, String>>() {
								}.getType());
								String type = map.get("type");
								if ("request".equals(type)) {
									String action = map.get("action");
									if ("auth".equals(action)) {
										// 认证消息处理
										String sender = map.get("sender");
										System.out.println(sender + "认证");
										// 放到容器当中
										clients.put(sender, client);
									} else if ("text".equals(action)) {
										// 文本消息
										String sender = map.get("sender");//客户端写死了
										String receiver = map.get("receiver");
										String content = map.get("content");

										Socket s = clients.get(receiver);
										if (s != null) {// 在线
											OutputStream output = s.getOutputStream();
											output.write(content.getBytes());
										} else {	// 离线
										}
									}
								} else System.out.println("格式错误");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}