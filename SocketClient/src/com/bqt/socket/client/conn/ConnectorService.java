package com.bqt.socket.client.conn;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.bqt.socket.client.PushReceiver;
import com.bqt.socket.client.request.AuthRequest;

/**
 * 作用①：Activity启动以后就开启服务通过ConnectorManager调用Connector中的connect方法对客户端进行认证
 * 作用②：客户端收到服务器转发过来的消息后通过ConnectorService的pushData通过发送一条广播将消息转发出去
 * 注意：为简化代码，若客户端（手机）SDK版本小于4.4则定义为A手机，否则就定义为B手机，请演示时一定注意！
 */
public class ConnectorService extends Service implements ConnectorListener {

	private ConnectorManager connectorManager;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		connectorManager = ConnectorManager.getInstance();
		new Thread(new Runnable() {
			@Override
			public void run() {
				connectorManager.setConnectorListener(ConnectorService.this);

				//认证
				AuthRequest request = null;
				//当前SDK版本大于4.4－－－暂时这么区分两部手机，实际肯定不是这么搞得
				if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) request = new AuthRequest("B", "B");
				else request = new AuthRequest("A", "A");

				connectorManager.connnect(request);
			}
		}).start();
	}

	@Override
	public void pushData(String data) {
		Log.d("coreService", "data : " + data);

		Intent intent = new Intent();
		intent.setAction(PushReceiver.ACTION_TEXT);
		intent.putExtra(PushReceiver.DATA_KEY, data);
		sendBroadcast(intent);
	}
}