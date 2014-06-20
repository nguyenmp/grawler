package com.nguyenmp.grawler.utils;

import org.apache.http.HttpResponse;

import java.io.IOException;
import java.io.InputStreamReader;

public class Utils {

	/**
	 * Reads the content of the HttpResponse's entity as a string
	 * @param httpResponse the response to read the entity from.
	 * @return the content of the HttpResponse's entity as a String
	 * @throws java.io.IOException If an I/O error occurs
	 */
	public static String toString(HttpResponse httpResponse) throws IOException {
		//Create reader and writer objects
		StringBuilder builder = new StringBuilder();
		InputStreamReader reader = new InputStreamReader(httpResponse.getEntity().getContent());
		char[] buffer = new char[1024];
		int charsRead;

		//Read all of the content and put it into a string
		while ((charsRead = reader.read(buffer, 0, buffer.length)) != -1) {
			builder.append(buffer, 0, charsRead);
		}

		//Return the read characters as a string
		return builder.toString();
	}

    public static String getViewState(String html) {
        int viewStateStart = html.indexOf("<input type=\"hidden\" name=\"__VIEWSTATE\" id=\"__VIEWSTATE\" value=\"") + "<input type=\"hidden\" name=\"__VIEWSTATE\" id=\"__VIEWSTATE\" value=\"".length();
        int viewStateEnd = html.indexOf("\"" 	, viewStateStart);
        return html.substring(viewStateStart, viewStateEnd);
    }

    public static String getEventValidation(String html) {
        int eventValidationStart = html.indexOf("<input type=\"hidden\" name=\"__EVENTVALIDATION\" id=\"__EVENTVALIDATION\" value=\"") + "<input type=\"hidden\" name=\"__EVENTVALIDATION\" id=\"__EVENTVALIDATION\" value=\"".length();
        int eventValidationEnd = html.indexOf("\"", eventValidationStart);
        return html.substring(eventValidationStart, eventValidationEnd);
    }
}
