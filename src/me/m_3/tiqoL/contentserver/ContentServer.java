package me.m_3.tiqoL.contentserver;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.LoggerFactory;

import fi.iki.elonen.NanoHTTPD;
import me.m_3.tiqoL.WSServer;

public class ContentServer extends NanoHTTPD {

	String host;
	int port;
	boolean secured = false;
	String publicurl;
	
	HashMap<String , ServableFile> publicFiles = new HashMap<String , ServableFile>();
	
	static org.slf4j.Logger Logger = LoggerFactory.getLogger(WSServer.class);
	
	public ContentServer(String hostname, int port, String publicURL) {
		super(hostname, port);
		this.host = hostname;
		this.port = port;
		this.publicurl = publicURL;
	}
	
	public void setSSL(boolean ssl) {
		this.secured = ssl;
	}
	
	public void start() {
		try {
			start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
		} catch (IOException e) {
			e.printStackTrace();
			Logger.error("ContentServer is offline. Won't be able to serve content");
			return;
		}
		
        Logger.info("ContentServer is serving at " + host + ":" + port);
	}
	
	public String serveFile(ServableFile file) {
		String uuid = UUID.randomUUID().toString();
		publicFiles.put(uuid, file);
		return uuid;
	}
	
	public String serveFile(ServableFile file , String customID) {
		String uuid = customID;
		publicFiles.put(uuid, file);
		return uuid;
	}
	
	public void removeFile(String id) {
		if (publicFiles.containsKey(id)) publicFiles.remove(id);
	}
	
	public String getURL(String id) {
		if (this.secured)
			return publicurl + "/?file="+id;
		return publicurl + "/?file="+id;
	}
	
	public String getPrivateURL(String id) {
		if (this.secured)
			return "https://"+this.host+":"+this.port+"/?file="+id+"&auth={tiqoL-authKey}";
		return "http://"+this.host+":"+this.port+"/?file="+id+"&auth={tiqoL-authKey}";
	}
	
    @Override
    public Response serve(IHTTPSession session) {
        String msg = "";
        Map<String, String> parms = session.getParms();
        if (parms.get("file") == null) {
            msg += "Tiqo-L content server<br><p style=\"color:red;\">No fileID provided</p>";
        }
        else {
            if (!this.publicFiles.containsKey(parms.get("file"))) {
            	msg += "Tiqo-L content server<br><p style=\"color:red;\">Unknown fileID</p>";
            }
            else {
            	ServableFile file = this.publicFiles.get(parms.get("file"));
            	if (!file.isPrivate()) {
            		return file.getResponse();
            	}
            	else if (parms.get("auth") == null) {
                    msg += "Tiqo-L content server<br><p style=\"color:red;\">No authKey provided for restricted file</p>";
                }
            	else if (file.isAllowed(parms.get("auth"))) {
            		return file.getResponse();
            	}
            	else {
                    msg += "Tiqo-L content server<br><p style=\"color:red;\">authKey has no permission for this file</p>";
            	}
            }
        }
        return newFixedLengthResponse(msg);
    }

}
