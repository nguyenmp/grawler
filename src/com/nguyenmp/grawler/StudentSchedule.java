package com.nguyenmp.grawler;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Scanner;

public class StudentSchedule {
	
	public static void getSchedule(GoldSession session) throws HttpClientFactory.SSHException, IOException {
		HttpClient client = HttpClientFactory.getClient(false);
		HttpContext context = HttpContextFactory.getContext(session);
		
		HttpGet get = new HttpGet("https://my.sa.ucsb.edu/gold/StudentSchedule.aspx");
		HttpResponse response = client.execute(get, context);
		
		
	}
	
	
	
}
