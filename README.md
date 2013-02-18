grawler
=======

A command line client for browsing UCSB's Gaucho-On-Line-Data (GOLD) website

How to use
=======

To log into GOLD using Grawler, you need to call the static "login" method.

    String username, password;
  	GoldSession session = Grawler.login(username, password);

What is returned is a GoldSession object which holds the cookies for authentication.

You use this GoldSession object for most, if not all, actions available by Grawler.

For example, to print all courses for the Winter 2013 period:

		BasicFindCourses.Params params = BasicFindCourses.getParams(session);
		
		for (BasicFindCourses.Params.SubjectArea subjectArea : params.subjectAreas) {
			System.out.println(subjectArea.value);
			Course[] courses = BasicFindCourses.findCourses(session, "20131", subjectArea.value);
			for (Course course : courses) {
				System.out.println(course.toString());
			}
		}

In this code, we get all the subject areas by requesting what is available using the 
get params method.  This returns us a list of available quarters and subject areas. 
From there, we iterate through each subject area and get all the courses in that subject 
area.
