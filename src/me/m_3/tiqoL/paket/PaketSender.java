package me.m_3.tiqoL.paket;

import org.json.JSONObject;

import me.m_3.tiqoL.WSServer;
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
	
	public static void sendCanvasBase64Request(WSServer server , User user , String objectID) {
		JSONObject obj = new JSONObject();
		obj.put("object", objectID);
		String send = PaketBuilder.createPaket("s05", obj);
		user.getSocket().send(send);
	}
}
