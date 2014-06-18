package com.nguyenmp.grawler;

import com.nguyenmp.grawler.utils.HttpClientFactory;
import com.nguyenmp.grawler.utils.HttpContextFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class Grawler {

	/**
	 * Authenticates the user to the GOLD system.
	 * @param username the UCSBNetID of the user to authenticate
	 * @param password the password corresponding to the UCSBNetID
	 * @return the GoldSession corresponding to a successful log in event or <code>null</code> 
	 * if the authentication fails.
	 * @throws com.nguyenmp.grawler.utils.HttpClientFactory.SSHException If the SSL connection manager could not be generated
	 * @throws IOException if an io exception or some HTTP error occurd
	 */
	public static GoldSession login(String username, String password) throws HttpClientFactory.SSHException, IOException {
		//Get initialized client and context
		HttpClient client = HttpClientFactory.getClient();
		HttpContext context = HttpContextFactory.getContext();
		
		//Get the login page's source
		HttpGet get = new HttpGet("https://my.sa.ucsb.edu/gold/Login.aspx");
		HttpResponse getResponse = client.execute(get, context);
		InputStreamReader inputStreamReader = new InputStreamReader(getResponse.getEntity().getContent());
		StringBuilder builder = new StringBuilder();
		char[] buffer = new char[1024];
		while (inputStreamReader.read(buffer, 0, buffer.length) != -1) {
			builder.append(buffer);
		}
		String loginSource = builder.toString();
		
		
		//Get viewstate argument from the source
		int viewStateStart = loginSource.indexOf("<input type=\"hidden\" name=\"__VIEWSTATE\" id=\"__VIEWSTATE\" value=\"") + "<input type=\"hidden\" name=\"__VIEWSTATE\" id=\"__VIEWSTATE\" value=\"".length();
		int viewStateEnd = loginSource.indexOf("\"" 	, viewStateStart);
		String viewState = loginSource.substring(viewStateStart, viewStateEnd);
		
		
		//Get event validation argument from the source
		int eventValidationStart = loginSource.indexOf("<input type=\"hidden\" name=\"__EVENTVALIDATION\" id=\"__EVENTVALIDATION\" value=\"") + "<input type=\"hidden\" name=\"__EVENTVALIDATION\" id=\"__EVENTVALIDATION\" value=\"".length();
		int eventValidationEnd = loginSource.indexOf("\"", eventValidationStart);
		String eventValidation = loginSource.substring(eventValidationStart, eventValidationEnd);
		
		
		//Create a new login request with params
		HttpPost post = new HttpPost("https://my.sa.ucsb.edu/gold/Login.aspx");
		List<NameValuePair> formData = new ArrayList<NameValuePair>();
		formData.add(new BasicNameValuePair("ctl00$pageContent$userNameText", username));
		formData.add(new BasicNameValuePair("ctl00$pageContent$passwordText", password));
		formData.add(new BasicNameValuePair("ctl00$pageContent$CredentialCheckBox", "on"));
		formData.add(new BasicNameValuePair("__VIEWSTATE", viewState));
		formData.add(new BasicNameValuePair("__EVENTVALIDATION", eventValidation));
		formData.add(new BasicNameValuePair("ctl00$pageContent$loginButton.x", "0"));
		formData.add(new BasicNameValuePair("ctl00$pageContent$loginButton.y", "0"));
		HttpEntity formEntity = new UrlEncodedFormEntity(formData);
		post.setEntity(formEntity);
		
		
		//Do the login and read the response to a string
		HttpResponse response = client.execute(post, context);
		StringBuilder responseBuilder = new StringBuilder();
		InputStreamReader responseReader = new InputStreamReader(response.getEntity().getContent());
		char[] readerBuffer = new char[1024];
		int readLength;
		while ((readLength = responseReader.read(readerBuffer, 0, readerBuffer.length)) != -1) {
			responseBuilder.append(readerBuffer, 0, readLength);
		}
		String responseString = responseBuilder.toString();
		
		
		//If we got expected response, then return the client
		if (responseString.contains("<h2>Object moved to <a href=\"/gold/AlertMessage.aspx\">here</a>.</h2>")) {
			CookieStore cookieStore = (CookieStore) context.getAttribute(ClientContext.COOKIE_STORE);
			List<Cookie> cookies = cookieStore.getCookies();
			GoldSession session = new GoldSession(cookies.toArray(new Cookie[cookies.size()]));
			session.setViewState(viewState);
			return session;
		}

		//If login failed, then return null
		else {
			return null;
		}
	}
	
}
