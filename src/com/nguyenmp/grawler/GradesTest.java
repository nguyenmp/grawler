package com.nguyenmp.grawler;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GradesTest {
    private GoldSession session;

    @Before
    public void setUp() throws Exception {
        session = Grawler.login(Credentials.username(), Credentials.password());
    }

    @After
    public void tearDown() throws Exception {
        session.stopHeartbeat();
        session = null;
    }

    @Test
    public void testGetAll() throws Exception {
        String result = Grades.getAll(session);

        // Assert the result is not null
        assertNotNull(result);

        // The String contains "ATt Unit"
        assertNotEquals(result.indexOf("ATt Unit"), -1);

        // The String contains more than one instance of "ATt Unit"
        // This test case would probably fail on a new student
        assertNotEquals(result.indexOf("ATt Unit"), result.lastIndexOf("ATt Unit"));
    }

    @Test
    public void testGetMostRecent() throws Exception {
        String result = Grades.getMostRecent(session);

        // Assert the result is not null
        assertNotNull(result);

        // The String contains "ATt Unit"
        assertNotEquals(result.indexOf("ATt Unit"), -1);

        // The String contains only one instance of "ATt Unit"
        assertEquals(result.indexOf("ATt Unit"), result.lastIndexOf("ATt Unit"));
    }
}