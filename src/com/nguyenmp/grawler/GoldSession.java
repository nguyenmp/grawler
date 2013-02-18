package com.nguyenmp.grawler;

import org.apache.http.cookie.Cookie;

import java.util.Arrays;

/**
 * A wrapper object to store the cookies returned 
 * from authenticating with the GOLD server.
 */
public class GoldSession {
	private String mViewState = null;
	private final Cookie[] mCookies;

	/**
	 * Creates a new wrapper object that will hold the cookies given.
	 * @param cookies the cookies from the GOLD server.  This value 
	 *                should remain encoded.
	 */
	GoldSession(Cookie[] cookies) {
		mCookies = cookies;
	}

	/**
	 * Gets the cookies from this wrapper.  The value will be the 
	 * literal value of the cookies, including the name and any other 
	 * parameters when see from the header.  The name and value will 
	 * also be encoded.
	 * @return an array of Strings that will represent the cookies 
	 * scanned from the header file.
	 */
	public Cookie[] getCookies() {
		return mCookies;
	}
	
	public String getViewState() {
		return mViewState;
	}
	
	public void setViewState(String viewState) {
		mViewState = viewState;
	}
	
	public String toString() {
		String value = "null";
		if (mCookies != null) value = Arrays.toString(mCookies);
		
		return value;
	}
}
