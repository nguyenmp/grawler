package com.nguyenmp.grawler;

public class Course {
	public final String name;
	
	/** the __EVENTTARGET param for getting course info */
	public final String courseInfo;
	public final String units;
	public final String grading;
	public final String session;
	public final Lecture[] lectures;

	public Course(String name, String courseInfo, String units, String grading, String session, Lecture[] lectures) {
		this.name = name;
		this.courseInfo = courseInfo;
		this.units = units;
		this.grading = grading;
		this.session = session;
		this.lectures = lectures;
	}

	@Override
	public String toString() {
		return String.format("%s	%s	%s", name, units, grading);
	}
}

class Lecture {
	public final String enrlCd;
	public final String days;
	public final String times;
	public final String instructors;
	public final String locations;
	public final String max;
	public final String space;
	public final Section[] sections;

	Lecture(String enrlCd, String days, String times, String instructors, String locations, String max, String space, Section[] sections) {
		this.enrlCd = enrlCd;
		this.days = days;
		this.times = times;
		this.instructors = instructors;
		this.locations = locations;
		this.max = max;
		this.space = space;
		this.sections = sections;
	}

	@Override
	public String toString() {
		return String.format("%s	%s	%s	%s	%s	%s	%s", enrlCd, days, times, instructors, locations, max, space);
	}
}

class Section {
	public final String enrlCd;
	public final String days;
	public final String times;
	public final String instructors;
	public final String locations;
	public final String max;
	public final String space;

	public Section(String enrlCd, String days, String times, String instructors, String locations, String max, String space) {
		this.enrlCd = enrlCd;
		this.days = days;
		this.times = times;
		this.instructors = instructors;
		this.locations = locations;
		this.max = max;
		this.space = space;
	}
	
	@Override
	public String toString() {
		return String.format("%s	%s	%s	%s	%s	%s	%s", enrlCd, days, times, instructors, locations, max, space);
	}
}