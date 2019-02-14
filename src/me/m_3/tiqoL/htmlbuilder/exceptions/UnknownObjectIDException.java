package me.m_3.tiqoL.htmlbuilder.exceptions;

public class UnknownObjectIDException extends Exception{
	
	private static final long serialVersionUID = 8180639371535324359L;
	public UnknownObjectIDException() { super(); }
	public UnknownObjectIDException(String message) { super(message); }
	public UnknownObjectIDException(String message, Throwable cause) { super(message, cause); }
	public UnknownObjectIDException(Throwable cause) { super(cause); }

}
