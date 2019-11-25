package me.m_3.tiqoL.htmlbuilder;

import me.m_3.tiqoL.WSServer;
import me.m_3.tiqoL.paket.PaketSender;
import me.m_3.tiqoL.user.User;

public class HTMLCanvas extends HTMLObject{

	public HTMLCanvas() {
		super("canvas");
		this.tiqo_object = "canvas";
	}
	
	public void requestBase64(WSServer server , User user) {
		PaketSender.sendCanvasBase64Request(server, user, this.id);
	}
	
	public HTMLCanvas setDrawable() {
		this.setHtmlAttribute("__drawable", "true");
		return this;
	}
	
}
