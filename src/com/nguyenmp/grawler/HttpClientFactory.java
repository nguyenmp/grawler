package com.nguyenmp.grawler;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.BasicClientConnectionManager;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;


public class HttpClientFactory {

	/**
	 * Same as calling getClient(false);
	 * @return an HTTPClient with a connection manager that is configured to accept the 
	 * SSL cert from my.sa.ucsb.edu
	 * @throws SSHException If the SSL connection manager could not be generated
	 */
	public static HttpClient getClient() throws SSHException {
		return getClient(false);
	}

	/**
	 * Creates a new HttpContext object that will authenticate according to the param.
	 * @param autoAcceptAllSSL True if you want to ignore invalid SSL certificates. 
	 * This is the case at the time of writing this code for "my.sa.uscb.edu". 
	 * This will open you to man-in-the-middle attacks and render the purpose of SSL 
	 * and HTTPS completely void. False if you want to validate the SSL certificate 
	 * appropriately.
	 * @return an HTTPClient with a connection manager that is configured to accept the 
	 * SSL cert from my.sa.ucsb.edu
	 * @throws SSHException If the SSL connection manager could not be generated
	 */
	public static HttpClient getClient(boolean autoAcceptAllSSL) throws SSHException {
		HttpClient client;
		try {
			if (autoAcceptAllSSL) {
				SSLContext sslContext = SSLContext.getInstance("SSL");

				//Set up a trust manager that trusts everything
				sslContext.init(null, new TrustManager[] {
					new X509TrustManager() {

						public X509Certificate[] getAcceptedIssuers() {
							//Do nothing
							return null;
						}

						public void checkClientTrusted(X509Certificate[] certs, String authType) {
							//Do nothing
						}

						public void checkServerTrusted(X509Certificate[] certs, String authType) {

						}
					}
				}, new SecureRandom());

				SSLSocketFactory socketFactory = new SSLSocketFactory(sslContext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
				Scheme httpsScheme = new Scheme("https", 443, socketFactory);
				SchemeRegistry schemeRegistry = new SchemeRegistry();
				schemeRegistry.register(httpsScheme);

				//apacheHttpClient version > 4.2 should use BasicClientConnectionManager
				ClientConnectionManager connectionManager = new BasicClientConnectionManager(schemeRegistry);
				client = new DefaultHttpClient(connectionManager);
			} else {
				//TODO: Load the keystore relative and within the jar
				KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
				FileInputStream inStream = new FileInputStream(new File(".\\assets\\GrawlerKeyStore"));
				trustStore.load(inStream, "password".toCharArray());
	
				SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
				Scheme scheme = new Scheme("https", 443, socketFactory);
	
				SchemeRegistry schemeRegistry = new SchemeRegistry();
				schemeRegistry.register(scheme);
	
				ClientConnectionManager connectionManager = new BasicClientConnectionManager(schemeRegistry);
				client = new DefaultHttpClient(connectionManager);
			}
		} catch (KeyStoreException e) {
			e.printStackTrace();
			throw new SSHException(e.getMessage());
		} catch (CertificateException e) {
			e.printStackTrace();
			throw new SSHException(e.getMessage());
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
			throw new SSHException(e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new SSHException(e.getMessage());
		} catch (KeyManagementException e) {
			e.printStackTrace();
			throw new SSHException(e.getMessage());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new SSHException(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			throw new SSHException(e.getMessage());
		}

		return client;
	}

	/**
	 * A class representing an error that occurs during 
	 * creation of an SSL certificate authenticator.
	 */
	public static class SSHException extends Exception {
		
		/**
		 * Creates a new exception representing an error that occurs during 
		 * creation of an SSL certificate authenticator.
		 * @param message the message for this exception
		 */
		SSHException(String message) {
			super(message);
		}
	}
}
