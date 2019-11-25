// File: WSServer.java
// The WebSocket Server of the Tiqo-L Server

package me.m_3.tiqoL;

import java.io.File;

import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import me.m_3.tiqoL.contentserver.ContentServer;
import me.m_3.tiqoL.coreloader.Core;
import me.m_3.tiqoL.coreloader.interfaces.ClassPointer;
import me.m_3.tiqoL.event.EventManager;
import me.m_3.tiqoL.paket.PaketAuthentificator;
import me.m_3.tiqoL.paket.PaketBuilder;
import me.m_3.tiqoL.storage.SessionStorage;
import me.m_3.tiqoL.user.User;
import me.m_3.tiqoL.user.UserStatus;

/**
 * This is the Core of the Tiqo-L server.
 * It manages all connections and the core.
 * It is based of the Java-Websocket project by TooTallNate on github.com
 * https://github.com/TooTallNate/Java-WebSocket
 * 
 * Java-Websocket license:
 *  
 * Copyright (c) 2010-2018 Nathan Rajlich
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 * 
 * 
 * For server/client communication a JSON based paket-protocol is used.
 * The JSON libary used is org.json
 * 
 * org.json license:
 * 
 * Copyright (c) 2002 JSON.org
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * The Software shall be used for Good, not Evil.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 * 
 * For temporary and permanent data storage the nitrite database by dizitart is used.
 * https://github.com/dizitart/nitrite-database
 * 
 * Like this software it is under the Apache license 2.0 which can be reviewed in the LICENSE file.
 * 
 * Logging is done by SLF4j.
 * 
 * The license of SLF4j:
 * 
 * Copyright (c) 2004-2017 QOS.ch
 * All rights reserved.
 *
 * Permission is hereby granted, free  of charge, to any person obtaining
 * a  copy  of this  software  and  associated  documentation files  (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 * 
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 * 
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * Tiqo-L Server
 * Copyright (c) 2018 Philipp Seelos
 *
 * @see org.java_websocket.server.WebSocketServer
 * @author Philipp Seelos
 */

public class WSServer extends WebSocketServer {
	
	HashMap<String , User> userMap = new HashMap<String , User>();
	
	public HashMap<String , User> getUserMap(){
		return userMap;
	}
	
	private EventManager eventManager;
	PaketAuthentificator paketAuthentificator;
	
	MainEventHandler mainEventHandler;
	MainHTMLClickHandler mainHTMLClickHandler;

	ContentServer contentServer;
	
	static org.slf4j.Logger Logger = LoggerFactory.getLogger(WSServer.class);
	
	//Storage
	SessionStorage sessionStorage;
	
	Core core;
	
   /**
    * Constructor to create a server instance.
    * The server needs to be started using WSServer::start()
    * The constructor will also load the provided core
    */

	public WSServer(String host, int port) {
		super(new InetSocketAddress(host, port));
		
		Logger.info(",--------.,--.                     ,--.");   
		Logger.info("'--.  .--'`--' ,---.  ,---. ,-----.|  |");    
		Logger.info("   |  |   ,--.| .-. || .-. |'-----'|  |");    
		Logger.info("   |  |   |  |' '-' |' '-' '       |  '--."); 
		Logger.info("   `--'   `--' `-|  | `---'        `-----'"); 
		Logger.info("                 `--'");      
		
		setEventManager(new EventManager(this));
		paketAuthentificator = new PaketAuthentificator(this);
		sessionStorage = new SessionStorage(this);
		
		mainEventHandler = new MainEventHandler(this);
		getEventManager().registerHandler(mainEventHandler);
		
		mainHTMLClickHandler = new MainHTMLClickHandler();
				
	}
	
	void loadCore() {
		//Load Core
		
		File dir = new File("core");
		File [] files = dir.listFiles(new FilenameFilter() {
		    @Override
		    public boolean accept(File dir, String name) {
		        return name.endsWith(".jar");
		    }
		});
		
		if (files.length == 0) {
			Logger.error("No core provided! Place a core inside the /core folder!");
			try {
				this.stop();
				Logger.info("Server shut down.");
				return;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			Logger.info("Loading core '"+files[0] + "' ...");
			
			JarFile jarFile = new JarFile(files[0]);
			Enumeration<JarEntry> e = jarFile.entries();
			
			@SuppressWarnings("deprecation")
			final URLClassLoader cl = new URLClassLoader (new URL[] {files[0].toURL()});
			
			while (e.hasMoreElements()) {
			    JarEntry je = e.nextElement();
			    if(je.isDirectory() || !je.getName().endsWith(".class")){
			        continue;
			    }
			    String className = je.getName().substring(0,je.getName().length()-6);
			    className = className.replace('/', '.');
			    cl.loadClass(className);
			}
			
			final Class<?> pointerClass = cl.loadClass("Pointer");
			final ClassPointer classPointer = (ClassPointer) pointerClass.getConstructor().newInstance();
			this.core = classPointer.getCore(this);
			
			jarFile.close();
			
			cl.close();
			
			Logger.info("The core has been loaded as '" + core.getName() + "'");
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (core == null) {
			Logger.error("Error loading the core, check the log for details.");
			try {
				this.stop();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
   /**
    * Method that gets called when a Connection is being opened.
    * It does nothing but printing since the first paket is being sent by the client.
    */

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		Logger.info("new connection on " + conn.getRemoteSocketAddress());
		conn.setAttachment(conn.getRemoteSocketAddress().toString());
	}

   /**
    * Method that gets called when a Connection is being closed.
    * It redirects to the EventManagerv
    */
	
	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		String socketAdress = conn.getAttachment();
		if (this.sessionStorage.no_close_event.contains(socketAdress)) {
			this.sessionStorage.no_close_event.remove(socketAdress);
			Logger.info("A session that has been taken control of by another connection has been timed out. No event has been called.");
			return;
		}
		this.getEventManager().callConnectionEndEvent(this.userMap.get(socketAdress) , code , reason , remote);
	}

   /**
    * Method that gets called when the client sends a message to the server.
    * The paket is given to the PaketAuthentificator for authentification.
    */
	
	public ArrayList<User> isSmoothResuming = new ArrayList<User>();
	
	@Override
	public void onMessage(WebSocket conn, String message) {
		
		JSONObject paket = new JSONObject(message);
		
		if (paket.getString("id").equals("c00") && !this.userMap.containsKey(conn.getRemoteSocketAddress().toString())) {
			
			JSONObject data = (JSONObject) paket.get("data");
			
			User user = new User(this , conn);
			
			if (data.getBoolean("resume_session")) {
				boolean double_connection = false;
				for (User u : this.userMap.values()) {
					if (u.getSessionKey().equals(data.getString("session")) && u.getUserStatus() == UserStatus.OPEN && u.getSecretKey().equals(data.getString("secret"))) {
						sessionStorage.createSession(user);	
						double_connection = true;
					}
				}
				user = sessionStorage.restoreSession(user, data.getString("secret"), data.getString("session") , double_connection);
				if (double_connection)
					this.isSmoothResuming.add(user);
			}
			else {
				sessionStorage.createSession(user);
			}
			
			//Paket data		
			JSONObject send = new JSONObject();
			send.put("secret", user.getSecretKey());
			send.put("session", user.getSessionKey());
			
			conn.send(PaketBuilder.createPaket("s00", send));
			
			userMap.put(conn.getRemoteSocketAddress().toString() , user);
			
		}
		else {
			this.paketAuthentificator.handlePakage(this.userMap.get(conn.getRemoteSocketAddress().toString()), paket);			
		}
	}

   /**
    * Method that gets called when the connections get interupted by an error.
    */

	@Override
	public void onError(WebSocket conn, Exception ex) {
		Logger.error("Forwarded to onError in WSServer:");
		ex.printStackTrace();

		if (conn == null) return;
		
		String socketAdress = conn.getAttachment();
		
		if (this.sessionStorage.no_close_event.contains(socketAdress)) {
			this.sessionStorage.no_close_event.remove(socketAdress);
			return;
		}
		if (this.getUserMap() != null && conn != null) {
			if (this.getUserMap().containsKey(socketAdress)) {
				this.getEventManager().callConnectionEndEvent(this.userMap.get(socketAdress) , 0 , null , true);
			}
		}
		if (conn != null)
			Logger.error("an error occured on connection " + socketAdress + ":" + ex);
	}
	
   /**
    * Method that gets called on server start.
    */
	
	@Override
	public void onStart() {                
		Logger.info("Started WebSocket-Server " + Description.name + " " + Description.version);
	}
	
	public static String readFile(String filePath){
	    StringBuilder contentBuilder = new StringBuilder();
	    try (Stream<String> stream = Files.lines( Paths.get(filePath), StandardCharsets.UTF_8))
	    {
	        stream.forEach(s -> contentBuilder.append(s).append("\n"));
	    }
	    catch (IOException e)
	    {
	        e.printStackTrace();
	    }
	    return contentBuilder.toString();
	}
	
   /**
    * Returns the EventManager
    */

	public EventManager getEventManager() {
		return eventManager;
	}

	private void setEventManager(EventManager eventManager) {
		this.eventManager = eventManager;
	}
	
	
	public ContentServer getContentServer() {
		return contentServer;
	}

	public void setContentServer(ContentServer contentServer) {
		this.contentServer = contentServer;
	}
	
}
