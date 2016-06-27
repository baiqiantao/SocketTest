package com.bqt.socket.client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class PushReceiver extends BroadcastReceiver {
	/**发送文本信息的事件*/
	public static final String ACTION_TEXT = "com.bqt.action.text";
	public static final String DATA_KEY = "data";

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.d("activity", "receive");
		if (PushReceiver.ACTION_TEXT.equals(action)) {
			String text = intent.getStringExtra(PushReceiver.DATA_KEY);
			Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
		}
	}
}