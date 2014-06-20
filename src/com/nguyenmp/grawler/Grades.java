package com.nguyenmp.grawler;

import com.nguyenmp.grawler.utils.HttpClientFactory;
import com.nguyenmp.grawler.utils.HttpContextFactory;
import com.nguyenmp.grawler.utils.Utils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Grades {

    public static String get(GoldSession session) throws HttpClientFactory.SSHException, IOException {
        // Initialize execution environment
        HttpClient client = HttpClientFactory.getClient();
        HttpContext context = HttpContextFactory.getContext(session);

        // Get initialization html which generates the viewstate parameter
        HttpGet init = new HttpGet("https://my.sa.ucsb.edu/gold/StudentGrades.aspx");
        HttpResponse initResponse = client.execute(init, context);
        String initHtml = Utils.toString(initResponse);
        String viewState = Utils.getViewState(initHtml);

        // Create a new login request with params
        HttpPost post = new HttpPost("https://my.sa.ucsb.edu/gold/StudentGrades.aspx");
        List<NameValuePair> formData = new ArrayList<NameValuePair>();
        formData.add(new BasicNameValuePair("__EVENTTARGET", "ctl00$pageContent$quarterDropDown"));
        formData.add(new BasicNameValuePair("__EVENTARGUMENT", ""));
        formData.add(new BasicNameValuePair("__LASTFOCUS", ""));
        formData.add(new BasicNameValuePair("__VIEWSTATE", viewState));
        formData.add(new BasicNameValuePair("ctl00$pageContent$quarterDropDown", "ALL"));
        HttpEntity formEntity = new UrlEncodedFormEntity(formData);
        post.setEntity(formEntity);

        // Get response
        HttpResponse response = client.execute(post, context);
        String html = Utils.toString(response);
        return html;
    }
}