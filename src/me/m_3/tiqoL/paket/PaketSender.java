package me.m_3.tiqoL.paket;

import org.json.JSONArray;
import org.json.JSONObject;

import me.m_3.tiqoL.WSServer;
import me.m_3.tiqoL.htmlbuilder.HTMLCanvas;
import me.m_3.tiqoL.htmlbuilder.HTMLObject;
import me.m_3.tiqoL.user.User;

public class PaketSender {
	
	public static void sendRebuildHTMLPaket(WSServer server , User user , JSONObject data) {
		String send = PaketBuilder.createPaket("s01", data);
		user.getSocket().send(send);
	}
	
	public static void sendUpdateHTMLPaket(WSServer server , User user , JSONObject data) {
		String send = PaketBuilder.createPaket("s02", data);
		user.getSocket().send(send);
	}
	
	public static void sendHeaderTagPaket(WSServer server , User user , String tag) {
		JSONObject obj = new JSONObject();
		obj.put("tag", tag);
		String send = PaketBuilder.createPaket("s03", obj);
		user.getSocket().send(send);
	}
	
	public static void clearHeaderTagsPaket(WSServer server , User user) {
		JSONObject obj = new JSONObject();
		String send = PaketBuilder.createPaket("s04", obj);
		user.getSocket().send(send);
	}
	
	public static void sendCanvasBase64RequestPaket(WSServer server , User user , String objectID) {
		JSONObject obj = new JSONObject();
		obj.put("object", objectID);
		String send = PaketBuilder.createPaket("s05", obj);
		user.getSocket().send(send);
	}
	
	public static void sendUpdateCustomDataPaket(WSServer server , User user , HTMLObject object) {
		JSONObject obj = new JSONObject();
		obj.put("object", object.getObjectID());
		obj.put("custom_data", object.getCustomData());
		String send = PaketBuilder.createPaket("s06", obj);
		user.getSocket().send(send);
	}
	
	public static void sendRemoveObjectPaket(WSServer server, User user, HTMLObject object) {
		JSONObject obj = new JSONObject();
		obj.put("object", object.getObjectID());
		String send = PaketBuilder.createPaket("s07", obj);
		user.getSocket().send(send);
	}
	
	public static void sendAddObjectToBodyPaket(WSServer server, User user, HTMLObject object) {
		//s08
		String send = PaketBuilder.createPaket("s08", object.toJSON(true));
		user.getSocket().send(send);
	}
	
	public static void sendAddChildPaket(WSServer server, User user, String addToObjectID, HTMLObject object) {
		JSONObject obj = new JSONObject();
		obj.put("addToObjectID", addToObjectID);
		obj.put("object", object.toJSON(true));
		String send = PaketBuilder.createPaket("s09", obj);
		user.getSocket().send(send);
	}
	
	public static void sendCanvasDrawPathPaket(WSServer server, User user, HTMLCanvas canvas, JSONArray path, String color, int width) {
		JSONObject obj = new JSONObject();
		
		obj.put("object", canvas.getObjectID());
		obj.put("path", path);
		obj.put("color", color);
		obj.put("width", width);
		
		String send = PaketBuilder.createPaket("s10", obj);
		user.getSocket().send(send);
	}
	
	public static void sendTitlePaket(WSServer server , User user , String message) {
		JSONObject obj = new JSONObject();
		String send = PaketBuilder.createPaket("s100", obj.put("title", message));
		user.getSocket().send(send);
	}
	
	public static void sendAlertPaket(WSServer server , User user , String message) {
		JSONObject obj = new JSONObject();
		String send = PaketBuilder.createPaket("s101", obj.put("message", message));
		user.getSocket().send(send);
	}
	
	public static void sendVibratePaket(WSServer server , User user , Integer[] rythm) {
		JSONObject obj = new JSONObject();
		obj.put("rythm", rythm);
		String send = PaketBuilder.createPaket("s102", obj);
		user.getSocket().send(send);
	}
	
	public static void sendAudioPaket(WSServer server , User user , String path , double volume) {
		JSONObject obj = new JSONObject();
		obj.put("audiosrc", path);
		obj.put("volume", volume);
		String send = PaketBuilder.createPaket("s103", obj);
		user.getSocket().send(send);
	}
	
	public static void sendExecuteJavaScriptPaket(WSServer server , User user , String exec) {
		JSONObject obj = new JSONObject();
		obj.put("code", exec);
		String send = PaketBuilder.createPaket("s104", obj);
		user.getSocket().send(send);
	}
	
	public static void sendSetLocalStorageKey(WSServer server , User user , String key, String value) {
		JSONObject obj = new JSONObject();
		obj.put("key", key);
		obj.put("value", value);
		String send = PaketBuilder.createPaket("s105", obj);
		user.getSocket().send(send);
	}
}
