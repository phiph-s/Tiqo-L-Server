package me.m_3.tiqoL.event;

import org.json.JSONArray;
import org.json.JSONObject;

import me.m_3.tiqoL.user.User;

public interface EventHandler {
	
	public default void onHandshakeComplete(User user , String secret) {
		
	}
	
	public default void onConnectionEnd(User user , int code , String reason , boolean remote) {
		
	}
	
	public default void onCanvasBase64Received(User user , String objectID , String base64) {
		
	}
	
	public default void onCustomDataReceived(User user , JSONObject data) {
		
	}
	
	public default void onCanvasPathReceived(User user , String objectID, JSONArray path, String color, int width) {
		
	}
	
	
}
