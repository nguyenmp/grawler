package com.nguyenmp.grawler;

import com.nguyenmp.grawler.utils.Utils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

public class FinalExam {
	
	public static String getExamDate(GoldSession session, String enrollCode) throws HttpClientFactory.SSHException, IOException {
		HttpClient client = HttpClientFactory.getClient();
		HttpContext context = HttpContextFactory.getContext(session);
		
		HttpGet get = new HttpGet("https://my.sa.ucsb.edu/gold/FinalExam.aspx?EnrollCode=" + enrollCode);
		HttpResponse response = client.execute(get, context);
		
		String responseString = Utils.toString(response);
		
		int start = responseString.indexOf("Final Examination: ") + "Final Examination: ".length();
		int end = responseString.indexOf("</span>", start);
		
		return responseString.substring(start, end);
	}
	
}
