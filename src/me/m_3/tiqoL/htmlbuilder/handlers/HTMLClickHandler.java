package me.m_3.tiqoL.htmlbuilder.handlers;

import me.m_3.tiqoL.user.User;

public interface HTMLClickHandler {
	
	public default void onClick(User user , String htmlObject , double x , double y , double pageX , double pageY) {
		
	}
	
}
