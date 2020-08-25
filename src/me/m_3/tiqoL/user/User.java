// File: User.java

package me.m_3.tiqoL.user;

import java.net.InetSocketAddress;
import java.security.SecureRandom;
import java.util.ArrayList;

import org.java_websocket.WebSocket;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import me.m_3.tiqoL.WSServer;
import me.m_3.tiqoL.htmlbuilder.box.HTMLBox;
import me.m_3.tiqoL.paket.PaketSender;

public class User {
	
	static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	static SecureRandom rnd = new SecureRandom();

	String secretKey;
	String sessionKey;
	InetSocketAddress address;
	WebSocket socket;
	
	String clientVersion;
	
	String unsafeOldSessionKey = null;
	
	public JSONObject getParameters() {
		return parameters;
	}

	public void setParameters(JSONObject parameters) {
		this.parameters = parameters;
	}
	static org.slf4j.Logger Logger = LoggerFactory.getLogger(User.class);

	JSONObject parameters;
	
	UserStatus userStatus = UserStatus.HANDSHAKE;
	
	HTMLBox htmlBox;
	WSServer server;
	
	ArrayList<String> headerTags = new ArrayList<String>();
		
	public User (WSServer server , WebSocket socket) {
		this.socket = socket;
		this.address = socket.getRemoteSocketAddress();
		this.server = server;
		this.htmlBox = new HTMLBox(server , this);
		generateSecretKey(256);
		
	}
	
	public void setHTMLBox (HTMLBox box) throws Exception {
		this.htmlBox = box;
		if (this.userStatus == UserStatus.OPEN)
			PaketSender.sendRebuildHTMLPaket(server, this, this.htmlBox.toJSON());
		else {
			Logger.error("Setting HTMLBox on an closed user. Bug in core. Following trace:");


			for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
			    Logger.error(ste.toString());
			}


		}
	}
	
	public boolean hasBufferedData() {
		return socket.hasBufferedData();
	}
	
	public void playAudio(String path) {
		if (this.userStatus == UserStatus.OPEN)
			PaketSender.sendAudioPaket(server, this, path , 1.0);
	}
	
	public void playAudio(String path , double volume) {
		if (this.userStatus == UserStatus.OPEN)
			PaketSender.sendAudioPaket(server, this, path , volume);
	}
	
	public void executeJavaScript(String code) {
		if (this.userStatus == UserStatus.OPEN)
			PaketSender.sendExecuteJavaScriptPaket(server, this, code);
	}
	
	public void vibrate(Integer[] rythm) {
		if (this.userStatus == UserStatus.OPEN)
		PaketSender.sendVibratePaket(server, this, rythm);
	}
	
	public void alert (String s) throws WebsocketNotConnectedException{
		if (this.userStatus == UserStatus.OPEN)
		PaketSender.sendAlertPaket(server, this, s);
	}
	
	public void addHeaderTag(String tag) throws WebsocketNotConnectedException{
		this.headerTags.add(tag);
		if (this.userStatus == UserStatus.OPEN)
		PaketSender.sendHeaderTagPaket(server, this, tag);
	}
	
	public void resendHeaderTags() throws WebsocketNotConnectedException{
		String doSend = "";
		for (String s : this.headerTags) {
			doSend += s;
		}
		if (this.userStatus == UserStatus.OPEN)
		PaketSender.sendHeaderTagPaket(server, this, doSend);
	}
	
	public void clearHeaderTags() throws WebsocketNotConnectedException{
		if (this.userStatus == UserStatus.OPEN)
		PaketSender.clearHeaderTagsPaket(server, this);
	}
	
	public void setTitle(String title) throws WebsocketNotConnectedException{
		if (this.userStatus == UserStatus.OPEN)
		PaketSender.sendTitlePaket(server, this, title);
	}

	private void generateSecretKey(int len) {
		StringBuilder sb = new StringBuilder( len );
		for( int i = 0; i < len; i++ ) 
			sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
		secretKey = sb.toString();
	}
	
	public void setUnsafeOldSessionKey(String unsafeOldSessionKey) {
		this.unsafeOldSessionKey = unsafeOldSessionKey;
	}
	
	public String getUnsafeOldSessionKey() {
		return unsafeOldSessionKey;
	}
	
	/**
	 * @return the secretKey
	 */
	public String getSecretKey() {
		return secretKey;
	}

	/**
	 * @param secretKey the secretKey to set
	 */
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	/**
	 * @return the sessionKey
	 */
	public String getSessionKey() {
		return sessionKey;
	}

	/**
	 * @param sessionKey the sessionKey to set
	 */
	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}

	/**
	 * @return the address
	 */
	public InetSocketAddress getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setaddress(InetSocketAddress address) {
		this.address = address;
	}

	/**
	 * @return the socket
	 */
	public WebSocket getSocket() {
		return socket;
	}

	/**
	 * @param socket the socket to set
	 */
	public void setSocket(WebSocket socket) {
		this.socket = socket;
	}

	/**
	 * @return the userStatus
	 */
	public UserStatus getUserStatus() {
		return userStatus;
	}

	/**
	 * @param userStatus the userStatus to set
	 */
	public void setUserStatus(UserStatus userStatus) {
		this.userStatus = userStatus;
	}

	/**
	 * @return the htmlBox
	 */
	public HTMLBox getHtmlBox() {
		return htmlBox;
	}
	
	public String getClientVersion() {
		return clientVersion;
	}

	public void setClientVersion(String clientVersion) {
		this.clientVersion = clientVersion;
	}
		
}
