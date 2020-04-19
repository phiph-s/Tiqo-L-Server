package me.m_3.tiqoL.htmlbuilder;

import org.json.JSONArray;

import me.m_3.tiqoL.WSServer;
import me.m_3.tiqoL.paket.PaketSender;
import me.m_3.tiqoL.user.User;

public class HTMLCanvas extends HTMLObject{

	public HTMLCanvas() {
		super("canvas");
		this.tiqo_object = "canvas";
	}
	
	public void setBase64(String string) {
		this.getCustomData().put("image", string);
	}
	
	public void requestBase64(WSServer server , User user) {
		PaketSender.sendCanvasBase64RequestPaket(server, user, this.id);
	}
	
	public HTMLCanvas setDrawable() {
		this.getCustomData().put("drawable", true);
		this.drawableSetColor("black");
		this.drawableSetWidth(2);
		return this;
	}
	
	public HTMLCanvas setDrawableSendPathUpdates(int resolution , int max_lenght) {
		this.getCustomData().put("drawable_send_paths", true);
		this.getCustomData().put("drawable_path_resolution", resolution);
		this.getCustomData().put("drawable_path_lenght", max_lenght);
		return this;
	}
	
	public HTMLCanvas drawableSetColor(String color) {
		this.getCustomData().put("color" , color);
		return this;
	}
	
	public HTMLCanvas drawableSetWidth(int pixels) {
		this.getCustomData().put("width" , pixels);
		return this;
	}
	
	public void updateDrawableSettings(WSServer server, User user) {
		PaketSender.sendUpdateCustomDataPaket(server, user, this);
	}
	
	public void sendPathToDraw(WSServer server, User user , JSONArray path, String color, int width) {
		PaketSender.sendCanvasDrawPathPaket(server, user, this, path, color, width);
	}
	
}
