package me.m_3.tiqoL.htmlbuilder.input;

import me.m_3.tiqoL.event.EventManager;
import me.m_3.tiqoL.htmlbuilder.HTMLObject;
import me.m_3.tiqoL.htmlbuilder.handlers.HTMLTextInputHandler;

public class HTMLMultilineTextInput extends HTMLObject{

	String currentText = "";
	
	public HTMLMultilineTextInput() {
		super("textarea");
		this.tiqo_object = "input_multiline_text";
	}
		
	public HTMLMultilineTextInput setTextInputHandler(EventManager eventManager , HTMLTextInputHandler handler) {
		eventManager.regsiterTextInputHandler(this.getObjectID(), handler);
		return this;
	}
	
}
