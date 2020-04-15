package me.m_3.tiqoL.htmlbuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.json.JSONObject;

import me.m_3.tiqoL.event.EventManager;
import me.m_3.tiqoL.htmlbuilder.handlers.HTMLClickHandler;
import me.m_3.tiqoL.paket.PaketSender;
import me.m_3.tiqoL.user.User;

public class HTMLObject {
	
	String type;
	protected String tiqo_object = "object";
	String id = UUID.randomUUID().toString();
	
	HashMap<String,String> css = new HashMap<String,String>();
	HashMap<String,String> attributes = new HashMap<String,String>();
	
	protected HashMap<String,String> protected_attributes = new HashMap<String,String>();
	
	ArrayList<HTMLObject> children = new ArrayList<HTMLObject>();
	
	private JSONObject customData = new JSONObject();
	
	public String insideText = "";
	
	HTMLClickHandler clickHandler = null;
	
	public HTMLObject (String type) {
		this.type = type;
	}
	
	public JSONObject toJSON(boolean doChildren) {
		JSONObject obj = new JSONObject();
		
		obj.put("type", type);
		obj.put("tiqoL-type", tiqo_object);
		obj.put("id", id);
		obj.put("insideText", insideText);
		obj.put("css", css);
		
		obj.put("customData", this.getCustomData());
		
		attributes.remove("id");
		attributes.remove("onclick");
		
		@SuppressWarnings("unchecked")
		HashMap<String,String> attr = (HashMap<String, String>) this.attributes.clone();
		attr.putAll(this.protected_attributes);
		
		obj.put("attributes", attr);
		
		if (this.clickHandler != null) {
			obj.put("onclick_action", true);
		}
		
		if (doChildren) {
			ArrayList<JSONObject> children_json = new ArrayList<JSONObject>();
			for (HTMLObject ho : children) {
				if (ho == this) continue;
				children_json.add(ho.toJSON(true));
			}
		
			obj.put("children", children_json);
		}
		
		return obj;
	}
	
	public boolean equals (String htmlObject) {
		if (this.id.equals(htmlObject)) {
			return true;
		}
		return false;
	}
	
	public String getObjectID() {
		return this.id;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<HTMLObject> getChildren(){
		return (ArrayList<HTMLObject>) this.children.clone();
	}
	
	public int getChildIndex(HTMLObject object) {
		if (this.children.contains(object)) {
			return this.children.indexOf(object);
		}
		return -1;
	}
	
	//Self-returning Set-Methods
	
	public HTMLObject setObjectID(String id) {
		this.id = id;
		return this;
	}
	
	public HTMLObject setJavaScriptCSS(String key , String value) {
		this.css.put(key, value);
		return this;
	}
	
	public HTMLObject unsetJavaScriptCSS(String key) {
		this.css.remove(key);
		return this;
	}
	
	public HTMLObject setHtmlAttribute(String key , String value) {
		this.attributes.put(key, value);
		return this;
	}
	
	public HTMLObject unsetHtmlAttribute(String key) {
		this.attributes.remove(key);
		return this;
	}
	
	public HTMLObject addChild(HTMLObject o) {
		if (!children.contains(o)) {
			children.add(o);
		}
		return this;
	}
	
	public HTMLObject addChildAndSend(User user, HTMLObject o) {
		this.addChild(o);
		PaketSender.sendAddChildPaket(user.getHtmlBox().getServer() , user, this.getObjectID(), o);
		user.getHtmlBox().buildDirectAccess(user.getHtmlBox().getBody());
		return this;
	}
	
	public HTMLObject putChild(int index , HTMLObject o) {
		if (!children.contains(o)) {
			children.add(index, o);
		}
		return this;
	}
	
	public HTMLObject removeChild(HTMLObject o) {
		if (children.contains(o)) {
			children.remove(o);
		}
		return this;
	}
	
	public HTMLObject setClickHandler(EventManager eventManager , HTMLClickHandler handler) {
		this.clickHandler = handler;
		eventManager.regsiterClickHandler(this.id , handler);
		return this;
	}
	
	public HTMLObject removesetClickHandler() {
		this.clickHandler = null;
		return this;
	}
	
	public HTMLObject setInnerText(String text) {
		this.insideText = text;
		return this;
	}
	
	public void enable() {
		this.protected_attributes.put("disabled" , "false");
	}
	
	public void disable() {
		this.protected_attributes.put("disabled" , "true");
	}

	public JSONObject getCustomData() {
		return customData;
	}

	public void setCustomData(JSONObject customData) {
		this.customData = customData;
	}
	
}
