package me.m_3.tiqoL.coreloader;

import java.net.InetSocketAddress;
import java.util.Collection;

import org.slf4j.LoggerFactory;

import me.m_3.tiqoL.WSServer;
import me.m_3.tiqoL.event.EventHandler;
import me.m_3.tiqoL.user.User;

public class Core {
	
	String name;
	
	public static org.slf4j.Logger Logger = LoggerFactory.getLogger(Core.class);
	
	public Core(WSServer server , String name) {
		this.name = name;
		this.server = server;
	}
	
	public String getName() {
		return this.name;
	}
	
	WSServer server;
	public WSServer getServer() {
		return this.server;
	}
	
	public void registerEventHandler(EventHandler eventHandler) {
		this.getServer().getEventManager().registerHandler(eventHandler);
	}
	
	public User getUser(String session) {
		for (User user : this.getServer().getUserMap().values()) {
			if (user.getSessionKey().equals(session))
				return user;
		}
		return null;
	}
	
	public User getUser(InetSocketAddress adress) {
		return this.getServer().getUserMap().get(adress);
	}
	
	public Collection<User> getUsers() {
		return this.getServer().getUserMap().values();
	}

}
