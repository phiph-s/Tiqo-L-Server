package me.m_3.tiqoL.htmlbuilder;

public class HTMLButton extends HTMLObject{

	public HTMLButton(String text) {
		super("button");
		this.insideText = text;
		this.tiqo_object = "button";
	}

}
