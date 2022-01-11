package me.m_3.tiqoL.storage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.dizitart.no2.Cursor;
import org.dizitart.no2.Document;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteCollection;
import org.dizitart.no2.WriteResult;
import org.dizitart.no2.filters.Filters;
import org.dizitart.no2.tool.Recovery;
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

	private ScheduledExecutorService scheduler;
	
	public SessionStorage(WSServer server) {
		this.server = server;
		
		Logger.info("Building session database ...");
		try {
			sessiondata = Nitrite.builder().filePath("sessions.db").openOrCreate();
		}
		catch(Exception ex) {
			Recovery.recover("sessions.db");
			sessiondata = Nitrite.builder().filePath("sessions.db").openOrCreate();
		}
		
		security = sessiondata.getCollection("security");
		
		//Starting an save scheduler
		//Start Thread Timer
		this.scheduler = Executors.newSingleThreadScheduledExecutor();

		Runnable task = new Runnable() {
			public void run() {
				clearOldSessions();
        		scheduler.schedule(this, 60, TimeUnit.MINUTES);
      		}
		};
		
		scheduler.schedule(task, 10, TimeUnit.SECONDS);
		
	}
	
	//Clear guest names
	public void clearOldSessions() {
		int amount = 0;
				
		Long removeAfter = Calendar.getInstance().getTimeInMillis() - TimeUnit.MILLISECONDS.convert(672, TimeUnit.HOURS);
		WriteResult data = security.remove(Filters.lt("_modified", removeAfter));
		amount = data.getAffectedCount();
		
		System.out.println("[SessionStorage] Removed "+amount+" timed-out sessions from database to free up space...");
	}
	
	public void setCustomData(String session_id, String key, String data) {
		if (security.find(Filters.eq("session", session_id)).size() == 0) return;
		if (key.equalsIgnoreCase("secret") || key.equalsIgnoreCase("session") || key.equalsIgnoreCase("used")) return;
		security.update(Filters.eq("session", session_id), new Document().put(key, data));
	}
	
	public String getCustomData(String session_id, String key) {
		Cursor cursor = security.find(Filters.eq("session", session_id));
		if (cursor.size() == 0) return null;
		Document session = security.find(Filters.eq("session", session_id)).firstOrDefault();
		if (key.equalsIgnoreCase("secret") || key.equalsIgnoreCase("session") || key.equalsIgnoreCase("used")) return null;
		
		if (!session.containsKey(key)) return null;
		return (String) session.get(key);
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
					this.updateSession(session);
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
			
			if (restore == null) return null;
			
			server.getUserMap().remove(restore.getAddress().toString());
			
			no_close_event.add(restore.getAddress().toString());
			
			restore.getSocket().close(1000);
			restore.setSocket(user.getSocket());
			restore.setaddress(user.getAddress());
			restore.setClientVersion(user.getClientVersion());
			
			restore.resendHeaderTags();
			
			Logger.info(user.getAddress() + " has taken control of running session " + session);
			
			this.updateSession(session);
			
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
	
	public void updateSession(String session_id) {
		int used = 0;
		Document session = security.find(Filters.eq("session", session_id)).firstOrDefault();
		if (session.containsKey("used")) {
			used = (int) session.get("used");
		}
		security.update(Filters.eq("session", session_id), new Document().put("used", used + 1));
	}
	
	public void createSession(User user) {
		UUID session_id = UUID.randomUUID();
		Document doc = new Document().put("secret" , user.getSecretKey()).put("session", session_id.toString()).put("used", 0);
		security.insert(doc);
		user.setSessionKey(session_id.toString());
		Logger.info(user.getAddress() + " temporary session key: " + session_id);
	}
	
}
