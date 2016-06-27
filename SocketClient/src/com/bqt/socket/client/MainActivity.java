package com.bqt.socket.client;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.bqt.socket.client.conn.ConnectorManager;
import com.bqt.socket.client.conn.ConnectorService;
import com.bqt.socket.client.request.Request;
import com.bqt.socket.client.request.TextRequest;

/**
 *  Activity启动以后就开启服务，服务开启后就通过ConnectorManager调用Connector中的connect方法对客户端进行认证
 *  认证是通过三次握手完成的，服务器为所有认证的客户端新建一个线程，通过此线程和客户端通讯
 *  当点击发送按钮后，通过ConnectorManager调用Connector中的方法把消息发送到一个阻塞队列中，最终发给服务器
 *  服务器收到消息后将其【转发】给指定客户端的Connector的阻塞队列中，客户端又通过listener.pushData(text)转发消息
 *  由于ConnectorManager注册了Connector的回调，ConnectorService又注册了ConnectorManager的回调
 *  所以最终调用的是ConnectorService的回调方法pushData(data) ，而此方法又通过发送一条广播将消息转发出去
 *  此广播会被PushReceiver接收到，因为其是在MainActivity注册的，所以最终MainActivity也收到了服务器转发过来的消息
 */
public class MainActivity extends Activity implements OnClickListener {
	private EditText et;
	private Button send;
	private PushReceiver receiver = new PushReceiver();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		et = (EditText) findViewById(R.id.et);
		send = (Button) findViewById(R.id.send);
		send.setOnClickListener(this);

		//Activity启动以后就开启服务
		startService(new Intent(this, ConnectorService.class));

		//在代码中动态注册广播，这种类型的广播不是常驻型广播，也就是说广播跟随程序的生命周期
		IntentFilter filter = new IntentFilter();
		filter.addAction(PushReceiver.ACTION_TEXT);
		registerReceiver(receiver, filter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.send:
			sendMessage();
			break;

		default:
			break;
		}
	}

	public void sendMessage() {
		final String content = et.getText().toString().trim();
		if (TextUtils.isEmpty(content)) return;

		String sender = null;
		String token = null;
		String receiver = null;
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
			sender = "B";
			receiver = "A";
			token = "B";
		} else {
			sender = "A";
			token = "A";
			receiver = "B";
		}

		Request request = new TextRequest(sender, token, receiver, content);
		ConnectorManager.getInstance().putRequest(request);
	}
}