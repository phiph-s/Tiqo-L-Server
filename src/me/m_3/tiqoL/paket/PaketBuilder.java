package me.m_3.tiqoL.paket;

import java.util.UUID;

import org.json.JSONObject;

public class PaketBuilder {
	
	public static String createPaket(String id , JSONObject data) {
		JSONObject obj = new JSONObject();
		obj.put("id", id);
		obj.put("data", data);
		return obj.toString();
	}
	
	public static String createPaketResponse(String id , JSONObject data , UUID uuid) {
		JSONObject obj = new JSONObject();
		obj.put("id", id);
		obj.put("data", data);
		obj.put("respone_key", uuid.toString());
		return obj.toString();
	}

}
