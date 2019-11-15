package me.m_3.tiqoL.storage;

import java.util.UUID;

import org.dizitart.no2.Cursor;
import org.dizitart.no2.Document;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteCollection;
import org.dizitart.no2.filters.Filters;
import org.slf4j.LoggerFactory;

import me.m_3.tiqoL.WSServer;
import me.m_3.tiqoL.user.User;

public class SessionStorage{
	
	WSServer server;
	
	Nitrite sessiondata;
	NitriteCollection security;
	
	static org.slf4j.Logger Logger = LoggerFactory.getLogger(WSServer.class);
	
	public SessionStorage(WSServer server) {
		this.server = server;
		
		Logger.info("Building temporary session database ...");
		
		sessiondata = Nitrite.builder().openOrCreate();
		
		security = sessiondata.getCollection("security");
		
	}
	
	public void restoreSession(User user , String old_secret , String session) {
		Cursor cursor = security.find(Filters.eq("session", session));
		for (Document e : cursor) {
			if (e.get("secret").equals(old_secret)) {
				user.setSessionKey(session);
				security.remove(e);
				Document doc = new Document().put("secret" , user.getSecretKey()).put("session", session);
				security.insert(doc);
				Logger.info(user.getAddress() + " resumed session: " + session);
				return;
			}
		}
		createSession(user);
	}
	
	public void createSession(User user) {
		UUID session_id = UUID.randomUUID();
		Document doc = new Document().put("secret" , user.getSecretKey()).put("session", session_id.toString());
		security.insert(doc);
		user.setSessionKey(session_id.toString());
		Logger.info(user.getAddress() + " temporary session key: " + session_id);
	}
	
}
