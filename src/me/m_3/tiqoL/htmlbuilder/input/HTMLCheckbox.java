package me.m_3.tiqoL.htmlbuilder.input;

import me.m_3.tiqoL.event.EventManager;
import me.m_3.tiqoL.htmlbuilder.HTMLObject;
import me.m_3.tiqoL.htmlbuilder.handlers.HTMLCheckboxHandler;

public class HTMLCheckbox extends HTMLObject{

	public HTMLCheckbox(boolean checked) {
		super("input");
		
		//The checkbox doesn't have insideText
		//this.insideText = text;
		
		this.tiqo_object = "input_checkbox";
		
		this.setChecked(checked);
	}
	
	public HTMLCheckbox setChecked(boolean checked) {
		this.protected_attributes.put("type", "checkbox");
		if (checked)
			this.protected_attributes.put("checked", "");
		else {
			this.protected_attributes.remove("checked");
		}
		return this;
	}
	
	public HTMLCheckbox setCheckboxHandler(EventManager eventManager , HTMLCheckboxHandler handler) {
		eventManager.regsiterCheckboxHandler(this.getObjectID(), handler);
		return this;
	}

}
