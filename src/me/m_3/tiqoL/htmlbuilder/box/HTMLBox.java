package me.m_3.tiqoL.htmlbuilder.box;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import me.m_3.tiqoL.WSServer;
import me.m_3.tiqoL.htmlbuilder.HTMLBody;
import me.m_3.tiqoL.htmlbuilder.HTMLButton;
import me.m_3.tiqoL.htmlbuilder.HTMLDiv;
import me.m_3.tiqoL.htmlbuilder.HTMLObject;
import me.m_3.tiqoL.htmlbuilder.HTMLSpan;
import me.m_3.tiqoL.htmlbuilder.exceptions.UnknownObjectIDException;
import me.m_3.tiqoL.htmlbuilder.input.HTMLCheckbox;
import me.m_3.tiqoL.htmlbuilder.input.HTMLTextInput;
import me.m_3.tiqoL.htmlbuilder.input.TextInputType;
import me.m_3.tiqoL.paket.PaketSender;
import me.m_3.tiqoL.user.User;

public class HTMLBox {
	
	WSServer server;
	User user;
	
	public HTMLBody body = new HTMLBody();
	
	private HashMap<String , HTMLObject> direct_access = new HashMap<String , HTMLObject>();
	
	static org.slf4j.Logger Logger = LoggerFactory.getLogger(WSServer.class);
	
	public HTMLBox(WSServer server , User user) {
		this.server = server;
		this.user = user;
	}
	
	public void fromJSON (JSONObject json) {
		HTMLBody body = new HTMLBody();
		if (!json.getString("type").equals("body")){
			Logger.warn("The top object of imported JSON is not a body object!");
		}
		
		//Attributes
		
		Iterator<String> nameItr = json.getJSONObject("attributes").keys();
		Map<String, String> outMap = new HashMap<String, String>();
		while(nameItr.hasNext()) {
		    String name = nameItr.next();
		    outMap.put(name, json.getJSONObject("attributes").getString(name));
		}
		for (String s : outMap.keySet()) {
			body.setHtmlAttribute(s, outMap.get(s));
		}
		
		//CSS
		
		nameItr = json.getJSONObject("css").keys();
		outMap = new HashMap<String, String>();
		while(nameItr.hasNext()) {
		    String name = nameItr.next();
		    outMap.put(name, json.getJSONObject("css").getString(name));
		}
		for (String s : outMap.keySet()) {
			body.setJavaScriptCSS(s, outMap.get(s));
		}
		
		//ID
		body.setObjectID(json.getString("id"));
		
		//Inside Text
		body.insideText = json.getString("insideText");
		
		//Children
		JSONArray array = json.getJSONArray("children");
		
		array.forEach(item -> {
			body.addChild(this.fromJSONrek((JSONObject)item));
		});
						
		this.setHTMLBody(body);
		
	}

	private HTMLObject fromJSONrek (JSONObject json) {
		
		HTMLObject htmlObj = new HTMLObject(json.getString("type"));
		if (json.getString("tiqoL-type").equals("button")) {
			htmlObj = new HTMLButton(json.getString("insideText"));
		}
		else if (json.getString("tiqoL-type").equals("div")) {
			htmlObj = new HTMLDiv();
		}
		else if (json.getString("tiqoL-type").equals("span")) {
			htmlObj = new HTMLSpan(json.getString("insideText"));
		}
		else if (json.getString("tiqoL-type").equals("input_checkbox")) {
			boolean checked = false;
			
			try {
				if (json.getJSONObject("attributes").has("checked")) {
					checked = true;
				}
			}catch(Exception ex){}
			
			htmlObj = new HTMLCheckbox(checked);
		}
		else if (json.getString("tiqoL-type").equals("input_text")) {
			TextInputType type = TextInputType.TEXT;
			
			try {
				String checked_str = json.getJSONObject("attributes").getString("type");
				type = TextInputType.valueOf(checked_str.toUpperCase());
			}catch(Exception ex){}
			
			htmlObj = new HTMLTextInput(type);
		}
		
		//Attributes
		
		Iterator<String> nameItr = json.getJSONObject("attributes").keys();
		Map<String, String> outMap = new HashMap<String, String>();
		while(nameItr.hasNext()) {
		    String name = nameItr.next();
		    outMap.put(name, json.getJSONObject("attributes").getString(name));
		}
		for (String s : outMap.keySet()) {
			htmlObj.setHtmlAttribute(s, outMap.get(s));
		}
		
		//CSS
		
		nameItr = json.getJSONObject("css").keys();
		outMap = new HashMap<String, String>();
		while(nameItr.hasNext()) {
		    String name = nameItr.next();
		    outMap.put(name, json.getJSONObject("css").getString(name));
		}
		for (String s : outMap.keySet()) {
			htmlObj.setJavaScriptCSS(s, outMap.get(s));
		}
		
		//ID
		htmlObj.setObjectID(json.getString("id"));
		
		//Inside Text
		htmlObj.insideText = "";
		if (json.has("insideText"))
			htmlObj.insideText = json.getString("insideText");
		
		//Children
		JSONArray array = json.getJSONArray("children");
		for (Object item : array) {
			htmlObj.addChild(this.fromJSONrek((JSONObject)item));
		}
		
		return htmlObj;
	}
	
	private boolean check(HTMLObject object , ArrayList<String> visited) {
		if (visited.contains(object.getObjectID())) return false;
		visited.add(object.getObjectID());
		for (HTMLObject ho : object.getChildren()) {
			if (!check(ho, visited)) return false;
		}
		return true;
	}
	
	public void buildDirectAccess(HTMLObject from) {
		this.getDirectAccess().put(from.getObjectID(), from);
		for (HTMLObject obj : from.getChildren()) {
			buildDirectAccess(obj);
		}
	}
	
	public void setHTMLBody(HTMLBody body) {
		this.body = body;
		this.getDirectAccess().clear();
		buildDirectAccess(this.body);
		if (!check(body , new ArrayList<String>())) {
			Logger.warn("WARNING: HTMLBody contains not-unique ObjectID's! This can cause serious problems when handling events!");
		}
		if (user != null && server != null) {
			PaketSender.sendRebuildHTMLPaket(server, user, this.toJSON());
		}
		
	}
	
	public HTMLBody getBody() {
		if (this.body != null) {
			return this.body;
		}
		return null;
	}
	
	public HTMLObject searchParent(HTMLObject from , String objectID) {
		for (HTMLObject o : from.getChildren()) {
			if (o.getObjectID().equals(objectID)) return from;
		}
		for (HTMLObject o : from.getChildren()) {
			HTMLObject found = searchParent(o , objectID);
			if (found != null)
				return found; 
		}
		return null;
	}
	
	public HTMLObject getObject(String objectID) throws UnknownObjectIDException {
		if (this.getDirectAccess().containsKey(objectID)) return this.getDirectAccess().get(objectID);
		else {
			throw new UnknownObjectIDException();
		}
	}
	
	public HTMLObject searchObject(HTMLObject from , String objectID) {
		for (HTMLObject o : from.getChildren()) {
			if (o.getObjectID().equals(objectID)) {
				return o;
			}
		}
		for (HTMLObject o : from.getChildren()) {
			HTMLObject found = searchParent(o , objectID);
			if (found != null)
				return found; 
		}
		return null;
	}
	
	public void updateObject(String objectID , HTMLObject object , boolean keepOldChildren) throws UnknownObjectIDException {
		HTMLBody body = this.body;
		HTMLObject parent = searchParent(body , objectID);
		HTMLObject old = this.getDirectAccess().get(objectID);
		if (parent == null) {
			throw new UnknownObjectIDException("This HTMLBox doesn't contain a HTMLObject with the ObjectID '"+objectID+"'");
		}
		int old_index = parent.getChildIndex(old);
		parent.removeChild(old);
		
		if (keepOldChildren) {
			
			@SuppressWarnings("unchecked")
			ArrayList<HTMLObject> oldchilds = (ArrayList<HTMLObject>) old.getChildren().clone();
			
			for (@SuppressWarnings("unused") HTMLObject o : old.getChildren()) {
				object.putChild(0, oldchilds.remove(oldchilds.size()-1));
			}
			
		}
		
		parent.putChild(old_index, object);
		
		this.getDirectAccess().clear();
		buildDirectAccess(this.body);
		
		
		if (!check(body , new ArrayList<String>())) {
			Logger.warn("WARNING: HTMLBody contains not-unique ObjectID's! This can cause serious problems when handling events!");
		}
		
		JSONObject paketJSON = new JSONObject();
		paketJSON.put("objectID", objectID);
		paketJSON.put("newObject", object.toJSON(true));
		paketJSON.append("keepOldChildren", keepOldChildren);
		
		PaketSender.sendUpdateHTMLPaket(server, user, paketJSON);
	}
	
	public JSONObject toJSON() {
		return this.body.toJSON(true);
	}

	public HashMap<String , HTMLObject> getDirectAccess() {
		return direct_access;
	}

	public void setDirectAccess(HashMap<String , HTMLObject> direct_access) {
		this.direct_access = direct_access;
	}
	
}
