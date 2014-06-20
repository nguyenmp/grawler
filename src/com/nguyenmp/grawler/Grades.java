package com.nguyenmp.grawler;

import com.nguyenmp.grawler.utils.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Grades {
    public String quarter;
    public List<Grade> grades;
    public Summary quarter_summary, cumulative_summary;

    public static List<Grades> getAll(GoldSession session) throws HttpClientFactory.SSHException, IOException, ParsingException {
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

        return parse(html);
    }

    public static List<Grades> getCurrent(GoldSession session) throws HttpClientFactory.SSHException, IOException, ParsingException {
        // Initialize execution environment
        HttpClient client = HttpClientFactory.getClient();
        HttpContext context = HttpContextFactory.getContext(session);

        // Get initialization html which generates the viewstate parameter
        HttpGet init = new HttpGet("https://my.sa.ucsb.edu/gold/StudentGrades.aspx");
        HttpResponse initResponse = client.execute(init, context);
        String initHtml = Utils.toString(initResponse);
        return parse(initHtml);
    }

    private static List<Grades> parse(String html) throws ParsingException {
        String strippedHTML = html.replaceAll("_[0-9]+\" class=\"datatable\"", "\" class=\"datatable\"");
        Element table = XMLParser.getDocumentFromString(strippedHTML).getElementById("pageContent_quarterGrid");
        if (table == null) return new ArrayList<Grades>();

        NodeList children = table.getChildNodes();

        List<Grades> grades = new ArrayList<Grades>();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            Document nodeAsDoc = XMLParser.getDocumentFromString(XMLParser.nodeToString(node));
            Element quarterSpan = XMLParser.getChildFromAttribute((Element) node, "class", "quarter");
            String quarter = quarterSpan.getTextContent();
            List<Grade> listOfGrades = parseGrades(nodeAsDoc.getElementById("pageContent_quarterGrid_gradesByQuarter"));

            Grades quarterGrades = new Grades();
            quarterGrades.grades = listOfGrades;
            quarterGrades.quarter = quarter;
            grades.add(quarterGrades);
        }

        return grades;
    }

    private static List<Grade> parseGrades(Element grades) {
        return null;
    }

    public static class Grade {
        public String course, grade, enrl_cd, att_unit, comp_unit, gpa_unit, points, additional_info;
    }

    public static class Summary {
        public float gpa, att_unit, comp_unit, gpa_unit, points, additional_info;
    }
}