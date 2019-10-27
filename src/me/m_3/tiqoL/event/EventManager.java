package me.m_3.tiqoL.event;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import me.m_3.tiqoL.WSServer;
import me.m_3.tiqoL.htmlbuilder.handlers.HTMLCheckboxHandler;
import me.m_3.tiqoL.htmlbuilder.handlers.HTMLClickHandler;
import me.m_3.tiqoL.htmlbuilder.handlers.HTMLTextInputHandler;
import me.m_3.tiqoL.user.User;

public class EventManager {
	
	WSServer server;
	
	ArrayList<EventHandler> handlers = new ArrayList<EventHandler>();
	
	static org.slf4j.Logger Logger = LoggerFactory.getLogger(EventManager.class);
	
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
			user.setParameters(paket.getJSONObject("data").getJSONObject("parameters"));
			for (EventHandler e : handlers) {
				try {
					e.onHandshakeComplete(user, secret);
				}
				catch(Exception ex) {
					Logger.error("Error in EventHandler " + e.getClass().getName() + " on onHandshakeComplete:");
					ex.printStackTrace();
				}
			}
		}
		else if (id.equals("c100")) {
			this.callHTMLClick(user, paket.getJSONObject("data").getString("clicked_id"),
					paket.getJSONObject("data").getDouble("x"),
					paket.getJSONObject("data").getDouble("y"),
					paket.getJSONObject("data").getDouble("pageX"),
					paket.getJSONObject("data").getDouble("pageY"));
		}
		else if (id.equals("c101")) {
			this.callHTMLCheckboxToggle(user, paket.getJSONObject("data").getString("clicked_id"), paket.getJSONObject("data").getBoolean("checked"));
		}
		else if (id.equals("c102")) {
			this.callHTMLTextInput(user, paket.getJSONObject("data").getString("clicked_id"), paket.getJSONObject("data").getString("text"));
		}
		else {
			Logger.debug("Unknown paket: " + paket);
		}
		
	}
	
	
	public void callConnectionEndEvent(User user , int code , String reason , boolean remote) {
		for (EventHandler e : handlers) {
			try {
				e.onConnectionEnd(user , code , reason , remote);
			}
			catch(Exception ex) {
				Logger.error("Error in EventHandler " + e.getClass().getName() + " on onConnectionEnd:");
				ex.printStackTrace();
			}
		}
	}
	
	HashMap<String , HTMLClickHandler> clickHandlers = new HashMap<String , HTMLClickHandler>();
	
	public void regsiterClickHandler(String id , HTMLClickHandler clickHandler) {
		clickHandlers.put(id, clickHandler);
	}
	
	public void callHTMLClick(User user , String id , double x , double y , double pageX , double pageY) {
		if (!clickHandlers.containsKey(id)) {
			Logger.warn("Unused incoming click event on id " + id);
			return;
		}
		try {
			clickHandlers.get(id).onClick(user, id, x, y, pageX, pageY);
		}
		catch(Exception ex) {
			Logger.error("Error in EventHandler " + clickHandlers.get(id).getClass().getName() + " on HTMLClick (id: "+id+"):");
			ex.printStackTrace();
		}
	}
	
	//Checkbox
	
	HashMap<String , HTMLCheckboxHandler> checkboxHandlers = new HashMap<String , HTMLCheckboxHandler>();
	
	public void regsiterCheckboxHandler(String id , HTMLCheckboxHandler checkBoxHandler) {
		checkboxHandlers.put(id, checkBoxHandler);
	}
	
	public void callHTMLCheckboxToggle(User user , String id , boolean press) {
		if (!checkboxHandlers.containsKey(id)) {
			Logger.warn("Unused incoming checkbox input event on id " + id);
			return;
		}
		
		try {
			checkboxHandlers.get(id).onToggle(user, id, press);
		}
		catch(Exception ex) {
			Logger.error("Error in EventHandler " + checkboxHandlers.get(id).getClass().getName() + " on HTMLCheckboxToggle (id: "+id+"):");
			ex.printStackTrace();
		}
	}
	
	//Text
	
	HashMap<String , HTMLTextInputHandler> textInputHandlers = new HashMap<String , HTMLTextInputHandler>();
	
	public void regsiterTextInputHandler(String id , HTMLTextInputHandler textInputHandler) {
		textInputHandlers.put(id, textInputHandler);
	}
	
	public void callHTMLTextInput(User user , String id , String text) {
		if (!textInputHandlers.containsKey(id)) {
			Logger.warn("Unused incoming text input event on id " + id);
			return;
		}
		try {
			textInputHandlers.get(id).onInput(user, id, text);
		}
		catch(Exception ex) {
			Logger.error("Error in EventHandler " + checkboxHandlers.get(id).getClass().getName() + " on HTMLTextInput (id: "+id+"):");
			ex.printStackTrace();
		}
	}
}
