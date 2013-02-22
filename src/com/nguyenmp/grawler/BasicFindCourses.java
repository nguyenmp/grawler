package com.nguyenmp.grawler;

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

public class BasicFindCourses {
	private static final String mQuarterOptionsDelimiterStart = "<select name=\"ctl00$pageContent$quarterDropDown\"";
	private static final String mQuarterOptionsDelimiterEnd = "</select>";
	private static final String mSubjectAreasDelimiterStart = "ctl00$pageContent$subjectAreaDropDown";
	private static final String mSubjectAreasDelimiterEnd = "</select>";
	
	
	public static Course[] findCourses(GoldSession session, String quarterValue, String subjectAreaValue) throws IOException, HttpClientFactory.SSHException {
		return findCourses(session, quarterValue, subjectAreaValue, "");
	}

	public static Course[] findCourses(GoldSession session, String quarterValue, String subjectAreaValue, String courseNumber) throws IOException, HttpClientFactory.SSHException {
		HttpClient client = HttpClientFactory.getClient();
		HttpContext context = HttpContextFactory.getContext(session);
		
		HttpGet get = new HttpGet("https://my.sa.ucsb.edu/gold/BasicFindCourses.aspx");
		HttpResponse getResponse = client.execute(get, context);
		String getResponseString = Utils.toString(getResponse);
		get.abort();
		
		//Get viewstate argument from the source
		int viewStateStart = getResponseString.indexOf("<input type=\"hidden\" name=\"__VIEWSTATE\" id=\"__VIEWSTATE\" value=\"") + "<input type=\"hidden\" name=\"__VIEWSTATE\" id=\"__VIEWSTATE\" value=\"".length();
		int viewStateEnd = getResponseString.indexOf("\"", viewStateStart);
		String viewState = getResponseString.substring(viewStateStart, viewStateEnd);
		
		HttpPost post = new HttpPost("https://my.sa.ucsb.edu/gold/BasicFindCourses.aspx");
		List<NameValuePair> formData = new ArrayList<NameValuePair>();
		formData.add(new BasicNameValuePair("__EVENTTARGET", ""));
		formData.add(new BasicNameValuePair("__EVENTARGUMENT", ""));
		formData.add(new BasicNameValuePair("__LASTFOCUS", ""));
		formData.add(new BasicNameValuePair("__VIEWSTATE", viewState));
		formData.add(new BasicNameValuePair("ctl00$pageContent$quarterDropDown", quarterValue));
		formData.add(new BasicNameValuePair("ctl00$pageContent$subjectAreaDropDown", subjectAreaValue));
		formData.add(new BasicNameValuePair("ctl00$pageContent$courseNumberTextBox", courseNumber));
		formData.add(new BasicNameValuePair("ctl00$pageContent$HiddenTextBox", ""));
		formData.add(new BasicNameValuePair("ctl00$pageContent$searchButton.x", "0"));
		formData.add(new BasicNameValuePair("ctl00$pageContent$searchButton.y", "0"));
		HttpEntity formEntity = new UrlEncodedFormEntity(formData);
		post.setEntity(formEntity);
		client.execute(post, context);
		post.abort();
		
		HttpGet resultGet = new HttpGet("https://my.sa.ucsb.edu/gold/ResultsFindCourses.aspx");
		HttpResponse resultResponse = client.execute(resultGet, context);
		String resultResponseString = Utils.toString(resultResponse);
		return getCourseList(resultResponseString);
	}
	
	private static Course[] getCourseList(String html) {
		List<Course> courses = new ArrayList<Course>();
		
		int start = html.indexOf("<table id=\"pageContent_CourseList\"");
		int end = html.indexOf("\"ctl00$pageContent$modifySearchBottomButton\" id=\"pageContent_modifySearchBottomButton\" class=\"fr\"", start);
		
		String courseListSubstring = html.substring(start, end);
		int courseSubstringStart, courseSubstringEnd = 0;
		
		while ((courseSubstringStart = courseListSubstring.indexOf("<table Class=\"datatable\"", courseSubstringEnd)) != -1) {
			courseSubstringEnd = courseListSubstring.indexOf("</table>\r\n" +
					"\t\t<br/>\r\n" +
					"\t</td>\r\n" +
					"\t</tr>", courseSubstringStart);
			String courseSubstring = courseListSubstring.substring(courseSubstringStart, courseSubstringEnd);
			
			Course course = parseCourse(courseSubstring);
			courses.add(course);
		}
		
		return courses.toArray(new Course[courses.size()]);
	}

	private static Course parseCourse(String courseSubstring) {
		//Parse course title
		int start = courseSubstring.indexOf("<div class=\"fl\">&nbsp;<span class=\"tableheader\" style=\"font-size:12px;\">") + "<div class=\"fl\">&nbsp;<span class=\"tableheader\" style=\"font-size:12px;\">".length();
		int end = courseSubstring.indexOf("</span>", start);
		String name = courseSubstring.substring(start, end);
		
		//Parse course info parameter link
		start = courseSubstring.indexOf("<div class=\"fl\" style=\"font-size:11px;\"><a id=\"", end) + "<div class=\"fl\" style=\"font-size:11px;\"><a id=\"".length();
		end = courseSubstring.indexOf("href=\"javascript:__doPostBack(", start);
		String courseInfo = courseSubstring.substring(start, end);
		
		//Parse units count
		String units = null;
		if (courseSubstring.contains("\">Units:")) {
			start = courseSubstring.indexOf("\">Units:", end) + "\">Units:".length();
			end = courseSubstring.indexOf("</td>", start);
			units = courseSubstring.substring(start, end);
		}

		//Parse grading option
		String gradingOption = null;
		if (courseSubstring.contains("\">Grading:")) {
			start = courseSubstring.indexOf("\">Grading:") + "\">Grading:".length();
			end = courseSubstring.indexOf("</td>", start);
			gradingOption = courseSubstring.substring(start, end);
		}
		
		//Parse section
		String session = null;
		if (courseSubstring.contains("Session ")) {
			start = courseSubstring.indexOf("Session ") + "Session ".length();
			end = courseSubstring.indexOf("</span>", start);
			session = courseSubstring.substring(start, end);
		}
		
		Lecture[] lectures = parseLectures(courseSubstring);
		
		return new Course(name, courseInfo, units, gradingOption, session, lectures);
	}
	
	private static Lecture[] parseLectures(String courseSubstring) {
		List<Lecture> lectures = new ArrayList<Lecture>();
		
		//Parse lectures
		int start, end = 0;
		
		while (end != -1) {
			start = courseSubstring.indexOf("<table width=\"571\" cellpadding=\"0\" cellspacing=\"0\" align=\"left\" border=\"0\">", end);
			end = courseSubstring.indexOf("<table width=\"571\" cellpadding=\"0\" cellspacing=\"0\" align=\"left\" border=\"0\">" , start + 1);
			
			if (start == -1) break;
			
			Lecture lecture = parseLecture(courseSubstring.substring(start, end == -1 ? courseSubstring.length() : end));
			lectures.add(lecture);
		}
		
		return lectures.toArray(new Lecture[lectures.size()]);
	}

	private static Lecture parseLecture(String lectureSubstring) {
		
		int start = lectureSubstring.indexOf("class=\"clcellprimary\">") + "class=\"clcellprimary\">".length();
		int end = lectureSubstring.indexOf("</td>", start);
		String enrlCd = lectureSubstring.substring(start, end);

		start = lectureSubstring.indexOf("class=\"clcellprimary\">", end) + "class=\"clcellprimary\">".length();
		end = lectureSubstring.indexOf("</td>", start);
		String days = lectureSubstring.substring(start, end);

		start = lectureSubstring.indexOf("class=\"clcellprimary\">", end) + "class=\"clcellprimary\">".length();
		end = lectureSubstring.indexOf("</td>", start);
		String times = lectureSubstring.substring(start, end);

		start = lectureSubstring.indexOf("class=\"clcellprimary\">", end) + "class=\"clcellprimary\">".length();
		end = lectureSubstring.indexOf("</td>", start);
		String instructors = lectureSubstring.substring(start, end);

		start = lectureSubstring.indexOf("class=\"clcellprimary\">", end) + "class=\"clcellprimary\">".length();
		end = lectureSubstring.indexOf("</td>", start);
		String locations = lectureSubstring.substring(start, end);

		start = lectureSubstring.indexOf("class=\"clcellprimary\">", end) + "class=\"clcellprimary\">".length();
		end = lectureSubstring.indexOf("</td>", start);
		String max = lectureSubstring.substring(start, end);

		start = lectureSubstring.indexOf("class=\"clcellprimary\">", end) + "class=\"clcellprimary\">".length();
		end = lectureSubstring.indexOf("</td>", start);
		String space = lectureSubstring.substring(start, end);
		
		Section[] sections = parseSections(lectureSubstring);
		
		return new Lecture(enrlCd, days, times, instructors, locations, max, space, sections);
	}

	private static Section[] parseSections(String lectureSubstring) {
		List<Section> sections = new ArrayList<Section>();
		int start, end = 0;
		
		while ((start = lectureSubstring.indexOf("<table width=\"571\" cellpadding=\"0\" cellspacing=\"0\" align=\"left\" border=\"0\" bgcolor=\"#", end)) != -1) {
			end = lectureSubstring.indexOf("/a></td>\r\n", start);
			
			Section section = parseSection(lectureSubstring.substring(start, end));
			sections.add(section);
		}
		
		return sections.toArray(new Section[sections.size()]);
	}

	private static Section parseSection(String sectionSubstring) {

		int start, end = sectionSubstring.indexOf("images/spacer.gif") + "images/spacer.gif".length();
		
		start = sectionSubstring.indexOf("class=\"clcellsecondary", end);
		start = sectionSubstring.indexOf(">", start) + 1;
		end = sectionSubstring.indexOf("</td>", start);
		String enrlCd = sectionSubstring.substring(start, end);

		start = sectionSubstring.indexOf("class=\"clcellsecondary", end);
		start = sectionSubstring.indexOf(">", start) + 1;
		end = sectionSubstring.indexOf("</td>", start);
		String days = sectionSubstring.substring(start, end);

		start = sectionSubstring.indexOf("class=\"clcellsecondary", end);
		start = sectionSubstring.indexOf(">", start) + 1;
		end = sectionSubstring.indexOf("</td>", start);
		String times = sectionSubstring.substring(start, end);

		start = sectionSubstring.indexOf("class=\"clcellsecondary", end);
		start = sectionSubstring.indexOf(">", start) + 1;
		end = sectionSubstring.indexOf("</td>", start);
		String instructors = sectionSubstring.substring(start, end);

		start = sectionSubstring.indexOf("class=\"clcellsecondary", end);
		start = sectionSubstring.indexOf(">", start) + 1;
		end = sectionSubstring.indexOf("</td>", start);
		String locations = sectionSubstring.substring(start, end);

		start = sectionSubstring.indexOf("class=\"clcellsecondary", end);
		start = sectionSubstring.indexOf(">", start) + 1;
		end = sectionSubstring.indexOf("</td>", start);
		String max = sectionSubstring.substring(start, end);

		start = sectionSubstring.indexOf("class=\"clcellsecondary", end);
		start = sectionSubstring.indexOf(">", start) + 1;
		end = sectionSubstring.indexOf("</td>", start);
		String space = sectionSubstring.substring(start, end);
		
		return new Section(enrlCd, days, times, instructors, locations, max, space);
	}

	public static Params getParams(GoldSession goldSession) throws HttpClientFactory.SSHException, IOException {
		HttpClient httpClient = HttpClientFactory.getClient();
		HttpContext httpContext = HttpContextFactory.getContext(goldSession);
		
		HttpGet httpGet = new HttpGet("https://my.sa.ucsb.edu/gold/BasicFindCourses.aspx");
		HttpResponse getResponse = httpClient.execute(httpGet, httpContext);
		String getResponseString = Utils.toString(getResponse);
		
		return parseParams(getResponseString);
	}

	private static Params parseParams(String getResponseString) {
		//Parse quarters
		Params.Quarter[] quarters = parseQuarters(getResponseString);
		Params.SubjectArea[] subjectAreas = parseSubjectArea(getResponseString);
		return new Params(quarters, subjectAreas);
	}
	
	private static Params.SubjectArea[] parseSubjectArea(String getResponseString) {
		List<Params.SubjectArea> subjectAreas = new ArrayList<Params.SubjectArea>();
		
		int subjectAreasStart = getResponseString.indexOf(mSubjectAreasDelimiterStart);
		int subjectAreasEnd = getResponseString.indexOf(mSubjectAreasDelimiterEnd, subjectAreasStart);
		String subjectAreasSubString = getResponseString.substring(subjectAreasStart, subjectAreasEnd);
		
		int subjectAreaStart, subjectAreaEnd = 0;
		
		while ((subjectAreaStart = subjectAreasSubString.indexOf("<option", subjectAreaEnd)) != -1) {
			subjectAreaEnd = subjectAreasSubString.indexOf("/option>", subjectAreaStart);
			String subjectAreaSubString = subjectAreasSubString.substring(subjectAreaStart, subjectAreaEnd);
			
			//Ignore if invalid
			if (subjectAreaSubString.contains(">-Select-<")) continue;
			
			//Parse selected
			boolean selected = subjectAreaSubString.contains("selected=\"selected\"");
			
			//Parse value
			int valueStart = subjectAreaSubString.indexOf("value=\"") + "value=\"".length();
			int valueEnd = subjectAreaSubString.indexOf("\"", valueStart);
			String value = subjectAreaSubString.substring(valueStart, valueEnd);
			
			//Parse name
			int nameStart = subjectAreaSubString.indexOf(">") + 1;
			int nameEnd = subjectAreaSubString.indexOf("<", nameStart);
			String name = subjectAreaSubString.substring(nameStart, nameEnd);
			
			subjectAreas.add(new Params.SubjectArea(name, value, selected));
		}
		
		return subjectAreas.toArray(new Params.SubjectArea[subjectAreas.size()]);
	}
	
	private static Params.Quarter[] parseQuarters(String getResponseString) {
		List<Params.Quarter> quarters = new ArrayList<Params.Quarter>();
		
		//Narrow down our search area
		int quartersStart = getResponseString.indexOf(mQuarterOptionsDelimiterStart) + mQuarterOptionsDelimiterStart.length();
		int quartersEnd = getResponseString.indexOf(mQuarterOptionsDelimiterEnd, quartersStart);
		String quartersSubString = getResponseString.substring(quartersStart, quartersEnd);
		
		int quarterStart, quarterEnd = 0;
		
		while ((quarterStart = quartersSubString.indexOf("<option", quarterEnd)) != -1) {
			quarterEnd = quartersSubString.indexOf("</option>", quarterStart);
			
			String quarterSubString = quartersSubString.substring(quarterStart, quarterEnd);
			
			//Fetch the value
			int valueStart = quarterSubString.indexOf("value=\"") + "value=\"".length();
			int valueEnd = quarterSubString.indexOf("\"", valueStart);
			String value = quarterSubString.substring(valueStart, valueEnd);
			
			//Fetch the name
			String name = quarterSubString.substring(quarterSubString.indexOf(">") + 1);
			
			boolean selected = quarterSubString.contains("selected=\"selected\"");
			
			//And add it to the list
			quarters.add(new Params.Quarter(name, value, selected));
		}
		
		
		//Return the list as an array
		return quarters.toArray(new Params.Quarter[quarters.size()]);
	}

	public static class Params {
		public final Quarter[] quarters;
		public final SubjectArea[] subjectAreas;
		
		Params(Quarter[] quarters, SubjectArea[] subjectAreas) {
			this.quarters = quarters;
			this.subjectAreas = subjectAreas;
		}

		public static class Quarter {
			public final String value;
			public final String name;
			public final boolean selected;

			public Quarter(String name, String value, boolean selected) {
				this.value = value;
				this.name = name;
				this.selected = selected;
			}
			
			@Override
			public String toString() {
				return String.format("[name = %s], [value = %s], [selected = %s]", name, value, selected);
			}
		}

		public static class SubjectArea {
			public final String value;
			public final String name;
			public final boolean selected;
			
			public SubjectArea(String name, String value, boolean selected) {
				this.value = value;
				this.selected = selected;
				this.name = name;
			}
			
			@Override
			public String toString() {
				return String.format("[name = %s], [value = %s], [selected = %s]", name, value, selected);
			}
		}
	}
	
	
}
