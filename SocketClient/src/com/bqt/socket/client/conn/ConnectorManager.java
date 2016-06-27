package com.bqt.socket.client.conn;

import com.bqt.socket.client.request.AuthRequest;
import com.bqt.socket.client.request.Request;

/**管理Connector的方法，目的：隐藏实现细节，简化对外暴露的方法*/
public class ConnectorManager implements ConnectorListener {

	private static ConnectorManager instance;
	private Connector connector;
	private ConnectorListener listener;

	private ConnectorManager() {
	}

	public static ConnectorManager getInstance() {
		if (instance == null) {
			synchronized (ConnectorManager.class) {
				if (instance == null) instance = new ConnectorManager();
			}
		}
		return instance;
	}

	/**连接、注册监听、认证*/
	public void connnect(AuthRequest auth) {
		connector = new Connector();
		connector.setConnectorListener(this);
		connector.connect();
		connector.auth(auth.getData());
	}

	/**发送消息*/
	public void putRequest(Request request) {
		connector.putRequest(request.getData());
	}

	@Override
	public void pushData(String data) {
		if (listener != null) listener.pushData(data);
	}

	public void setConnectorListener(ConnectorListener listener) {
		this.listener = listener;
	}
}