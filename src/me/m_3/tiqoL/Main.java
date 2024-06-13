package me.m_3.tiqoL;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
//File: Main.java

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.xml.bind.DatatypeConverter;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.SimpleLayout;
import org.java_websocket.server.DefaultSSLWebSocketServerFactory;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import me.m_3.tiqoL.contentserver.ContentServer;

/** 
 * Tiqo-L Server
 * Copyright (c) 2018 Philipp Seelos
 * 
 * The Main class that loads the configuration and starts the server.
 *
 * @see me.m_3.tiqoL.WSServer
 * @author Philipp Seelos
 */

public class Main {
	
	WSServer webSocketServer;
	
	public JSONObject getKeySettings() {
		return keySettings;
	}

	public JSONObject getSettings() {
		return settings;
	}

	public JSONObject getServerSettings() {
		return serverSettings;
	}

	JSONObject keySettings;
	JSONObject settings;
	JSONObject serverSettings;
	
	static org.slf4j.Logger Logger = LoggerFactory.getLogger(WSServer.class);
	
    public static void main(String[] args)
    {  
    	new Main();
    }
    
    public Main () { 	    	    	

    	try {
			SimpleLayout layout = new SimpleLayout();
			FileAppender fileAppender = new FileAppender( layout, "logs/latest.log", false );
			org.apache.log4j.Logger.getRootLogger().addAppender( fileAppender );
			// ALL | DEBUG | INFO | WARN | ERROR | FATAL | OFF:
			org.apache.log4j.Logger.getRootLogger().setLevel( Level.INFO );
		} catch( Exception ex ) {
			System.out.println( ex );
	    }
    	
    	String[] copyFiles = {"settings.json"};
    	
    	BasicConfigurator.configure();
    	
    	for (String s : copyFiles) {
	    	InputStream defaultSite = this.getClass().getResourceAsStream(s);    	
	    	File to = new File(System.getProperty("user.dir") + File.separator + s);
	    	if(!to.exists() || to.isDirectory()) { 
	        	try {
					byte[] buffer = new byte[defaultSite.available()];
					defaultSite.read(buffer);
					OutputStream outStream = new FileOutputStream(to);
					outStream.write(buffer);
					outStream.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
	    	}
    	}
    	new File("core").mkdir();
    	
    	
    	    	
    	settings = new JSONObject(WSServer.readFile(System.getProperty("user.dir") + File.separator + "settings.json"));
    	serverSettings = settings.getJSONObject("server");
    	
    	Logger.info("Starting server on " + serverSettings.getString("host") + ":" + serverSettings.getInt("port"));
    	this.webSocketServer = new WSServer(this, serverSettings.getString("host") , serverSettings.getInt("port"));
    	WSServer server = this.webSocketServer;
    	    	
    	keySettings = serverSettings.getJSONObject("keystore");
    	
    	//Use SSL
    	if (serverSettings.getBoolean("use-ssl")) {
    		JSONObject letsencryptSettings = keySettings.getJSONObject("letsencrypt");
	    	if (letsencryptSettings.getBoolean("use")) {
	    		Logger.info("Using LetsEncrypt ...");
	    		SSLContext context = getContext(letsencryptSettings);
	    		if( context != null ) {
	    			server.setWebSocketFactory( new DefaultSSLWebSocketServerFactory( getContext(letsencryptSettings) ) );
	    		}
	    	}
	    	else {
	    		Logger.info("Using Keystore ...");
	    		SSLContext context = getSSLConextFromKeystore(keySettings);
	    		if( context != null ) {
	    			server.setWebSocketFactory( new DefaultSSLWebSocketServerFactory( getSSLConextFromKeystore(keySettings) ) );
	    		}	    		
	    	}
    	}
    	
    	//Create content server (Nanohhttpd-server)
    	int contentPort = serverSettings.getInt("contentServerPort");
    	Logger.info("Starting content server on port "+contentPort+" ...");
    	
    	ContentServer contentServer = new ContentServer(serverSettings.getString("host") , contentPort, serverSettings.getString("contentServerPublicURL"));
    	
    	//Use SSL
    	if (serverSettings.getBoolean("use-ssl")) {
    		JSONObject letsencryptSettings = keySettings.getJSONObject("letsencrypt");
	    	if (letsencryptSettings.getBoolean("use")) {
	    		SSLContext context = getContext(letsencryptSettings);
	    		if( context != null ) {
	    			SSLContext sslC = getContext(letsencryptSettings);
	    	    	contentServer.makeSecure(sslC.getServerSocketFactory(), null);
	    	    	contentServer.setSSL(true);
	    	    	Logger.info("Content server secured using letsencrypt ssl");
	    		}
	    	}
	    	else {
	    		SSLContext context = getSSLConextFromKeystore(keySettings);
	    		if( context != null ) {
	    			SSLContext sslC = getSSLConextFromKeystore(keySettings);
	    	    	contentServer.makeSecure(sslC.getServerSocketFactory(), null);
	    	    	contentServer.setSSL(true);
	    	    	Logger.info("Content server secured using ssl certificate from keystore");
	    	    }	    		
	    	}
    	}
    	 
    	contentServer.start();
    	this.webSocketServer.setContentServer(contentServer);
    	    	
    	//Catch Shutdown
    	
    	Runtime.getRuntime().addShutdownHook(new Thread()
    	{
    	    @Override
    	    public void run()
    	    {
    	    	try {
					Logger.info("Shutting down...");
					server.stop();
					Logger.info("The server has been shut down savely...");
				} catch (IOException e) {
					e.printStackTrace();
					Logger.error("The server could not shut down! Ports may be still occupied and data may be lost!");
				} catch (InterruptedException e) {
					e.printStackTrace();
					Logger.error("The server could not shut down! Ports may be still occupied and data may be lost!");
				}
    	    }
    	});
    	
    	server.loadCore();

    	this.webSocketServer.setConnectionLostTimeout(15);
    	this.webSocketServer.run();
    }
    
    public SSLContext getSSLContext() {
    	if (serverSettings.getBoolean("use-ssl")) {
	    	JSONObject letsencryptSettings = keySettings.getJSONObject("letsencrypt");
	    	if (letsencryptSettings.getBoolean("use")) {
	    		SSLContext context = getContext(letsencryptSettings);
	    		if( context != null ) {
	    			return context;
	    		}
	    	}
	    	else {
	    		SSLContext context = getSSLConextFromKeystore(keySettings);
	    		if( context != null ) {
	    			return context;
	    	    }	    		
	    	}
    	}
    	return null;
    }
    
    private static SSLContext getSSLConextFromKeystore(JSONObject keySettings) {
        // load up the key store
		String storeType = "JKS";
	    String keystore = keySettings.getString("keystore");
	    String storePassword = keySettings.getString("storepassword");
	    String keyPassword = keySettings.getString("keypassword");

        KeyStore ks;
        SSLContext sslContext;
        try {
            ks = KeyStore.getInstance(storeType);
            ks.load(Files.newInputStream(Paths.get("..", keystore)), storePassword.toCharArray());
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, keyPassword.toCharArray());
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(ks);


            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        } catch (Exception e) {
           throw new IllegalArgumentException();
        }
        return sslContext;
    }
	
	private static SSLContext getContext(JSONObject letsencryptSettings) {
		SSLContext context;
		String password = letsencryptSettings.getString("password");
		String pathname = letsencryptSettings.getString("path");
		try {
			context = SSLContext.getInstance( "TLS" );

			byte[] certBytes = parseDERFromPEM( getBytes( new File( pathname + File.separator + "cert.pem" ) ), "-----BEGIN CERTIFICATE-----", "-----END CERTIFICATE-----" );
			byte[] keyBytes = parseDERFromPEM( getBytes( new File( pathname + File.separator + "privkey.pem" ) ), "-----BEGIN PRIVATE KEY-----", "-----END PRIVATE KEY-----" );

			X509Certificate cert = generateCertificateFromDER( certBytes );
			RSAPrivateKey key = generatePrivateKeyFromDER( keyBytes );

			KeyStore keystore = KeyStore.getInstance( "JKS" );
			keystore.load( null );
			keystore.setCertificateEntry( "cert-alias", cert );
			keystore.setKeyEntry( "key-alias", key, password.toCharArray(), new Certificate[]{ cert } );

			KeyManagerFactory kmf = KeyManagerFactory.getInstance( "SunX509" );
			kmf.init( keystore, password.toCharArray() );

			KeyManager[] km = kmf.getKeyManagers();

			context.init( km, null, null );
		} catch ( Exception e ) {
			context = null;
		}
		return context;
	}

	private static byte[] parseDERFromPEM( byte[] pem, String beginDelimiter, String endDelimiter ) {
		String data = new String( pem );
		String[] tokens = data.split( beginDelimiter );
		tokens = tokens[1].split( endDelimiter );
		return DatatypeConverter.parseBase64Binary( tokens[0] );
	}

	private static RSAPrivateKey generatePrivateKeyFromDER( byte[] keyBytes ) throws InvalidKeySpecException, NoSuchAlgorithmException {
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec( keyBytes );

		KeyFactory factory = KeyFactory.getInstance( "RSA" );

		return ( RSAPrivateKey ) factory.generatePrivate( spec );
	}

	private static X509Certificate generateCertificateFromDER( byte[] certBytes ) throws CertificateException {
		CertificateFactory factory = CertificateFactory.getInstance( "X.509" );

		return ( X509Certificate ) factory.generateCertificate( new ByteArrayInputStream( certBytes ) );
	}

	private static byte[] getBytes( File file ) {
		byte[] bytesArray = new byte[( int ) file.length()];

		FileInputStream fis = null;
		try {
			fis = new FileInputStream( file );
			fis.read( bytesArray ); //read file into bytes[]
			fis.close();
		} catch ( IOException e ) {
			e.printStackTrace();
		}
		return bytesArray;
	}
    
}
