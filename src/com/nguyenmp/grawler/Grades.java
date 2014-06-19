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
    public static enum Type {
        ALL("ALL"), MOST_RECENT("NOT_ALL");

        private final String content;
        Type(String content) {
            this.content = content;
        }

        @Override
        public String toString() {
            return content;
        }
    }

    public static String get(GoldSession session, Type type) throws HttpClientFactory.SSHException, IOException {
        // Initialize execution environment
        HttpClient client = HttpClientFactory.getClient();
        HttpContext context = HttpContextFactory.getContext(session);

        // Create a new login request with params
        HttpPost post = new HttpPost("https://my.sa.ucsb.edu/gold/StudentGrades.aspx");
        List<NameValuePair> formData = new ArrayList<NameValuePair>();
        formData.add(new BasicNameValuePair("__EVENTTARGET", "ctl00$ctl12"));
        formData.add(new BasicNameValuePair("__EVENTARGUMENT", ""));
        formData.add(new BasicNameValuePair("__LASTFOCUS", ""));
        formData.add(new BasicNameValuePair("__VIEWSTATE", session.getViewState()));
        formData.add(new BasicNameValuePair("ctl00$pageContent$quarterDropDown", type.toString()));
        HttpEntity formEntity = new UrlEncodedFormEntity(formData);
        post.setEntity(formEntity);
        HttpResponse response = client.execute(post, context);
        Utils.toString(response);

        // Return response
        HttpGet get = new HttpGet("https://my.sa.ucsb.edu/gold/StudentGrades.aspx");
        response = client.execute(get, context);
        String html = Utils.toString(response);

        return html;
    }
    public static String getAll(GoldSession session) throws HttpClientFactory.SSHException, IOException {
        return Grades.get(session, Type.ALL);
    }

    public static String getMostRecent(GoldSession session) throws IOException, HttpClientFactory.SSHException {
        return Grades.get(session, Type.MOST_RECENT);
    }
}