package me.m_3.tiqoL.paket;

import org.json.JSONObject;

public class PaketBuilder {
	
	public static String createPaket(String id , JSONObject data) {
		JSONObject obj = new JSONObject();
		obj.put("id", id);
		obj.put("data", data);
		return obj.toString();
	}

}
