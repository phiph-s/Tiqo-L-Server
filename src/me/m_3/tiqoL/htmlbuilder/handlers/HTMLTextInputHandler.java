package me.m_3.tiqoL.htmlbuilder.handlers;

import me.m_3.tiqoL.user.User;

public interface HTMLTextInputHandler {

	public default void onInput(User user , String htmlObject , String text) {
		
	}
	
	public default void onSubmit(User user , String htmlObject , String text) {
		
	}
	
	
}
