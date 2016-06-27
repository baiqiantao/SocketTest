package com.bqt.socket.client.request;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.google.gson.Gson;

/**认证消息类型，具体的格式需要和服务器同事对接*/
public class AuthRequest implements Request {
	private Map<String, String> map = new HashMap<String, String>();

	public AuthRequest(String sender, String token) {
		map.put("type", "request");
		map.put("sequence", UUID.randomUUID().toString());
		map.put("action", "auth");
		map.put("sender", sender);
		map.put("token", token);
	}

	@Override
	public String getData() {
		return new Gson().toJson(map);
	}
}