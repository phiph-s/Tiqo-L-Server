package me.m_3.tiqoL.contentserver;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.ArrayList;

import org.slf4j.LoggerFactory;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response;
import me.m_3.tiqoL.user.User;

public class ServableFile {
	
	boolean privateFile = false;
	
	ArrayList<User> access = new ArrayList<User>();
	
	static org.slf4j.Logger Logger = LoggerFactory.getLogger(ServableFile.class);
	
	String mimeType;
	File file;
	
	public ServableFile(File file) {
		this.file = file;
	}
	
	public ServableFile(File file , String mimeType) {
		this.file = file;
		this.mimeType = mimeType; 
	}
	
	public void setPrivate(boolean privateFile) {
		this.privateFile = privateFile;
	}
	
	public boolean isPrivate() {
		return this.privateFile;
	}
	
	public void addUser (User user) {
		this.access.add(user);
	}
	
	public void removeUser(User user) {
		if (access.contains(user)) access.remove(user);
	}
	
	public ArrayList<User> getUsers(){
		return this.access;
	}
	
	public boolean isAllowed(String authKey) {
		for (User user : this.access) {
			if (user.getSecretKey().equals(authKey)) return true;
		}
		return false;
	}
	
	public Response getResponse() {
		return fileResponse(file , mimeType);
	}
	
    Response fileResponse(File file , String fileType) {
         FileInputStream fis = null;
         String mimeType = null;
         try {
            fis = new FileInputStream(file);
            if (fileType == null) {
	            InputStream is = new BufferedInputStream(fis);
	            mimeType = URLConnection.guessContentTypeFromStream(is);
            }
            else {
            	mimeType = fileType;
            }
         } catch (IOException e) {
             Logger.warn("The requested file can't be served ("+e.getMessage()+")");
         }
		try {
			return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, mimeType, new FileInputStream(file), file.length());
		} catch (FileNotFoundException e) {
			return null;
		}

    }

}
