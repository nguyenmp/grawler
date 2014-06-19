package com.nguyenmp.grawler;

import com.nguyenmp.grawler.utils.HttpClientFactory;
import com.nguyenmp.grawler.utils.HttpContextFactory;
import com.nguyenmp.grawler.utils.Utils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

public class StudentSchedule {
	
	public static void getSchedule(GoldSession session) throws HttpClientFactory.SSHException, IOException {
		HttpClient client = HttpClientFactory.getClient(false);
		HttpContext context = HttpContextFactory.getContext(session);
		
		HttpGet get = new HttpGet("https://my.sa.ucsb.edu/gold/StudentSchedule.aspx");
		HttpResponse response = client.execute(get, context);
		
		String text = Utils.toString(response);
	}
	
	
	
}
