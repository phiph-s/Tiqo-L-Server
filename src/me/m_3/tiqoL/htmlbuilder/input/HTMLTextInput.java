package me.m_3.tiqoL.htmlbuilder.input;

import me.m_3.tiqoL.event.EventManager;
import me.m_3.tiqoL.htmlbuilder.HTMLObject;
import me.m_3.tiqoL.htmlbuilder.handlers.HTMLCheckboxHandler;

public class HTMLTextInput extends HTMLObject{

	public HTMLTextInput(TextInputType type) {
		super("input");
		this.tiqo_object = "input_text";
		
		this.protected_attributes.put("type", "checkbox");
		this.protected_attributes.put("checked", type.toString());
	}
	
	public void setCheckboxHandler(EventManager eventManager , HTMLCheckboxHandler handler) {
		
	}
	
}
