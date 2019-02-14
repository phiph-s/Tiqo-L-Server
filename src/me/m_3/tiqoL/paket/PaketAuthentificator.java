package me.m_3.tiqoL.paket;

import org.json.JSONObject;

import me.m_3.tiqoL.WSServer;
import me.m_3.tiqoL.user.User;

public class PaketAuthentificator {

	WSServer server;
	
	public PaketAuthentificator(WSServer server) {
		this.server = server;
	}
	
	public void handlePakage(User user , JSONObject json) {
		if (json.getString("secret").equals(user.getSecretKey())) {
			//ACCESS AUTHENTICATED
			server.getEventManager().callEvent(user, json);
		}
		else {
			//ACCESS DENIED
		}
	}
	
}
