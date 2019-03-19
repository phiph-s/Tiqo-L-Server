package me.m_3.tiqoL.htmlbuilder.input;

import me.m_3.tiqoL.event.EventManager;
import me.m_3.tiqoL.htmlbuilder.HTMLObject;
import me.m_3.tiqoL.htmlbuilder.handlers.HTMLTextInputHandler;

public class HTMLTextInput extends HTMLObject{

	String currentText = "";
	
	public HTMLTextInput(TextInputType type) {
		super("input");
		this.tiqo_object = "input_text";
		
		this.protected_attributes.put("type", type.toString());
	}
		
	public HTMLTextInput setTextInputHandler(EventManager eventManager , HTMLTextInputHandler handler) {
		eventManager.regsiterTextInputHandler(this.getObjectID(), handler);
		return this;
	}
	
}
