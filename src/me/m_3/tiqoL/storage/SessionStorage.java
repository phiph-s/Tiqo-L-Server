package me.m_3.tiqoL.storage;

import java.util.ArrayList;
import java.util.UUID;

import org.dizitart.no2.Cursor;
import org.dizitart.no2.Document;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteCollection;
import org.dizitart.no2.filters.Filters;
import org.slf4j.LoggerFactory;

import me.m_3.tiqoL.WSServer;
import me.m_3.tiqoL.user.User;
import me.m_3.tiqoL.user.UserStatus;

public class SessionStorage{
	
	WSServer server;
	
	Nitrite sessiondata;
	NitriteCollection security;
	
	static org.slf4j.Logger Logger = LoggerFactory.getLogger(WSServer.class);
	
	public ArrayList<String> no_close_event = new ArrayList<String>();
	
	public SessionStorage(WSServer server) {
		this.server = server;
		
		Logger.info("Building temporary session database ...");
		
		sessiondata = Nitrite.builder().filePath("sessions.db").openOrCreate();
		
		security = sessiondata.getCollection("security");
		
	}
	
	public User restoreSession(User user , String old_secret , String session, boolean doubleSession) {
		if (!doubleSession) {
			Cursor cursor = security.find(Filters.eq("session", session));
			for (Document e : cursor) {
				if (e.get("secret").equals(old_secret)) {
					user.setSessionKey(session);
					security.remove(e);
					Document doc = new Document().put("secret" , user.getSecretKey()).put("session", session);
					security.insert(doc);
					Logger.info(user.getAddress() + " resumed session: " + session);
					return user;
				}
			}
			//Giving old session id as unsafe old session for the core to track things like usernames etc among sessions
			user.setUnsafeOldSessionKey(session);
			createSession(user);
			return user;
		}
		else{
			
			User restore = null;
			for (User u : server.getUserMap().values()) {
				if (u.getSessionKey().equals(session) && u.getUserStatus() == UserStatus.OPEN && u.getSecretKey().equals(old_secret)) {
					restore = u;
					break;
				}
			}
			
			server.getUserMap().remove(restore.getAddress().toString());
			
			no_close_event.add(restore.getAddress().toString());
			
			restore.getSocket().close(1000);
			restore.setSocket(user.getSocket());
			restore.setaddress(user.getAddress());
			restore.setClientVersion(user.getClientVersion());
			
			restore.resendHeaderTags();
			
			Logger.info(user.getAddress() + " has taken control of running session " + session);
			
			//Restore current display state
			try {
				restore.setHTMLBox(restore.getHtmlBox());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return restore;
		}
	}
	
	public void createSession(User user) {
		UUID session_id = UUID.randomUUID();
		Document doc = new Document().put("secret" , user.getSecretKey()).put("session", session_id.toString());
		security.insert(doc);
		user.setSessionKey(session_id.toString());
		Logger.info(user.getAddress() + " temporary session key: " + session_id);
	}
	
}
