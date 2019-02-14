// File: User.java

package me.m_3.tiqoL.user;

import java.net.InetSocketAddress;
import java.security.SecureRandom;

import org.java_websocket.WebSocket;

import me.m_3.tiqoL.WSServer;
import me.m_3.tiqoL.htmlbuilder.box.HTMLBox;
import me.m_3.tiqoL.paket.PaketSender;

public class User {
	
	static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	static SecureRandom rnd = new SecureRandom();

	String secretKey;
	String sessionKey;
	InetSocketAddress adress;
	WebSocket socket;
	
	UserStatus userStatus = UserStatus.HANDSHAKE;
	
	HTMLBox htmlBox;
	WSServer server;
	
	public User (WSServer server , WebSocket socket) {
		
		this.socket = socket;
		this.adress = socket.getRemoteSocketAddress();
		this.server = server;
		this.htmlBox = new HTMLBox(server , this);
		generateSecretKey(2056);
		
	}
	
	public void setHTMLBox (HTMLBox box) {
		this.htmlBox = box;
		PaketSender.sendRebuildHTMLPaket(server, this, this.htmlBox.toJSON());
	}
	
	public void alert (String s) {
		PaketSender.sendAlertPaket(server, this, s);
	}
	
	public void addHeaderTag(String tag) {
		PaketSender.sendHeaderTagPaket(server, this, tag);
	}
	
	public void clearHeaderTags() {
		PaketSender.clearHeaderTagsPaket(server, this);
	}
	
	public void setTitle(String title) {
		PaketSender.sendTitlePaket(server, this, title);
	}

	private void generateSecretKey(int len) {
		StringBuilder sb = new StringBuilder( len );
		for( int i = 0; i < len; i++ ) 
			sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
		secretKey = sb.toString();
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
	 * @return the adress
	 */
	public InetSocketAddress getAdress() {
		return adress;
	}

	/**
	 * @param adress the adress to set
	 */
	public void setAdress(InetSocketAddress adress) {
		this.adress = adress;
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

	/**
	 * @param htmlBox the htmlBox to set
	 */
	public void setHtmlBox(HTMLBox htmlBox) {
		this.htmlBox = htmlBox;
	}
		
}
