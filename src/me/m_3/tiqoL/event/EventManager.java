package me.m_3.tiqoL.event;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import me.m_3.tiqoL.WSServer;
import me.m_3.tiqoL.htmlbuilder.handlers.HTMLCheckboxHandler;
import me.m_3.tiqoL.htmlbuilder.handlers.HTMLClickHandler;
import me.m_3.tiqoL.user.User;

public class EventManager {
	
	WSServer server;
	
	ArrayList<EventHandler> handlers = new ArrayList<EventHandler>();
	
	static org.slf4j.Logger Logger = LoggerFactory.getLogger(WSServer.class);
	
	public EventManager(WSServer server) {
		this.server = server;
	}
	
	public void registerHandler (EventHandler handler) {
		if (!handlers.contains(handler))
			handlers.add(handler);
	}
	
	public void unregisterHandler (EventHandler handler) {
		if (handlers.contains(handler))
			handlers.remove(handler);
	}
	
	public void callEvent(User user , JSONObject paket) {
		//ONLY CALL AFTER SECURITY CHECK
		String id = paket.getString("id");
		String secret = paket.getString("secret");
		//JSONObject data = (JSONObject) paket.get("data");
		
		if (id.equals("c01")) {
			for (EventHandler e : handlers) {
				e.onHandshakeComplete(user, secret);
			}
		}
		else if (id.equals("c100")) {
			this.callHTMLClick(user, paket.getJSONObject("data").getString("clicked_id"),
					paket.getJSONObject("data").getDouble("x"),
					paket.getJSONObject("data").getDouble("y"),
					paket.getJSONObject("data").getDouble("pageX"),
					paket.getJSONObject("data").getDouble("pageY"));
		}
		else {
			Logger.debug("Unknown paket: " + paket);
		}
		
	}
	
	
	public void callConnectionEndEvent(User user , int code , String reason , boolean remote) {
		for (EventHandler e : handlers) {
			e.onConnectionEnd(user , code , reason , remote);
		}
	}
	
	HashMap<String , HTMLClickHandler> clickHandlers = new HashMap<String , HTMLClickHandler>();
	
	public void regsiterClickHandler(String id , HTMLClickHandler clickHandler) {
		clickHandlers.put(id, clickHandler);
	}
	
	public void callHTMLClick(User user , String id , double x , double y , double pageX , double pageY) {
		clickHandlers.get(id).onClick(user, id, x, y, pageX, pageY);
	}
	
	HashMap<String , HTMLCheckboxHandler> checkboxHandlers = new HashMap<String , HTMLCheckboxHandler>();
	
	public void regsiterCheckboxHandler(String id , HTMLCheckboxHandler checkBoxHandler) {
		checkboxHandlers.put(id, checkBoxHandler);
	}
	
	public void callHTMLCheckboxToggle(User user , String id , boolean press) {
		checkboxHandlers.get(id).onToggle(user, id, press);
	}
}
