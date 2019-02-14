package me.m_3.tiqoL.htmlbuilder.input;

import me.m_3.tiqoL.event.EventManager;
import me.m_3.tiqoL.htmlbuilder.HTMLObject;
import me.m_3.tiqoL.htmlbuilder.handlers.HTMLCheckboxHandler;

public class HTMLCheckbox extends HTMLObject{

	public HTMLCheckbox(String text , boolean checked) {
		super("input");
		this.insideText = text;
		this.tiqo_object = "input_checkbox";
		
		this.protected_attributes.put("type", "checkbox");
		this.protected_attributes.put("checked", checked + "");
	}
	
	public void setCheckboxHandler(EventManager eventManager , HTMLCheckboxHandler handler) {
		
	}

}
