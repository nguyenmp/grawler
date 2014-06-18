package com.nguyenmp.grawler.utils;

import com.nguyenmp.grawler.GoldSession;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.UnsupportedEncodingException;

/**
 * Generator for a configured HttpContext that has an initialized 
 * CookieStore.
 */
public class HttpContextFactory {

	/**
	 * Creates a new BasicHttpContext with an initialized CookieStore.  To recreate a 
	 * previous GoldSession context, then pass the GoldSession object as a parameter.
	 * @return A new HttpContext object with an initialized CookieStore.
	 */
	public static HttpContext getContext() {
		//Create a new basic http context
		HttpContext context = new BasicHttpContext();

		//Create and assign an empty CookieStore to the context
		CookieStore cookieStore = new BasicCookieStore();
		context.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

		//Return the initialized context
		return context;
	}

	/**
	 * Creates a new HttpContext with the cookies from the GoldSession in it's cookie store
	 * @param goldSession the GoldSession object that we will take the cookies from. If 
	 * this parameter is null, then this function will act like getContext().
	 * @return a new HttpContext object with it's CookieStore filled with the cookies from 
	 * the goldSession.
	 */
	public static HttpContext getContext(GoldSession goldSession) {
		//Return an empty context if a session was not provided
		if (goldSession == null) return getContext();

		//Create a cookie store the session cookies given from the argument
		CookieStore cookieStore = new BasicCookieStore();
		for (Cookie cookie : goldSession.getCookies()) {
			cookieStore.addCookie(cookie);
		}

		//Create a new context with the cookie store as the context's cookies
		HttpContext context = new BasicHttpContext();
		context.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

		//Return the new context
		return context;
	}

}
