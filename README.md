grawler
=======

A command line client for browsing UCSB's Gaucho-On-Line-Data (GOLD) website


Setting Up
=======

First, you'll need a couple of things.  I tested the code with Java JDK 1.7 and ANT 1.9.2.  Most any version should be fine as both tools are very backwards compatable.

1. Download and install a recent JDK version

2. Download and install ANT

3. Download grawler

		git clone https://github.com/nguyenmp/grawler

4. Load in your credentials

		cd grawler
		Make a copy of src/com/nguyenmp/grawler/Credentials.java.template and rename it Credentials.java
		Edit Credentials.java so that username and password are string literals of those values
		USERNAME = "mynamehere"

5. Test to make sure it's all good

		cd grawler
		ant test

How to Log In
=======

To log into GOLD using Grawler, you need to call the static "login" method.

	String username, password;
	GoldSession session = Grawler.login(username, password);

What is returned is a GoldSession object which holds the cookies for authentication.

You use this GoldSession object for most, if not all, actions available by Grawler.

How to Search for Classes
=======

	BasicFindCourses.Params params = BasicFindCourses.getParams(session);
	String fallQuarter = "20131";
	
	//Iterate through the available subject areas
	for (BasicFindCourses.Params.SubjectArea subjectArea : params.subjectAreas) {
		
		//Retrieve all the courses in that subject area
		Course[] courses = BasicFindCourses.findCourses(session, fallQuarter, subjectArea.value);
		
		//Print every course in that subject area
		for (Course course : courses) {
			System.out.println(course.toString());
		}
	}

In this code, we get all the subject areas by requesting what is available using the 
get params method.  This returns us a list of available quarters and subject areas. 
From there, we iterate through each subject area and get all the courses in that subject 
area.
