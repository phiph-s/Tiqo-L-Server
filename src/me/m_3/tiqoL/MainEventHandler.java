// File: MainEventHandler.java
// The EventHandler of the server itself

package me.m_3.tiqoL;

import org.slf4j.LoggerFactory;

import me.m_3.tiqoL.event.EventHandler;
import me.m_3.tiqoL.user.User;
import me.m_3.tiqoL.user.UserStatus;

/** 
 * Tiqo-L Server
 * Copyright (c) 2018 Philipp Seelos
 *
 * @see me.m_3.tiqoL.event.EventHandler
 * @author Philipp Seelos
 */

public class MainEventHandler implements EventHandler{
	
	WSServer server;
	
	static org.slf4j.Logger Logger = LoggerFactory.getLogger(WSServer.class);	
	
	public MainEventHandler(WSServer server) {
		this.server = server;
	}
	
	@Override
	public void onHandshakeComplete(User user , String secret) {
		user.setUserStatus(UserStatus.OPEN);

		Logger.debug("User authentication-handshake complete: " + user.getAddress() + " " + user.getUserStatus());
	}
	
	@Override
	public void onConnectionEnd(User user , int code , String reason , boolean remote) {
		System.out.println("User disconnected, user: " + user);
		server.userMap.remove(user.getAddress());
		user.setUserStatus(UserStatus.CLOSED);
		Logger.debug("Connection ended");
	}
	
}
