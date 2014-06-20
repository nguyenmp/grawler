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
        assertNotEquals(result.indexOf("Att Unit"), -1);

        // The String contains more than one instance of "Att Unit"
        // This test case would probably fail on a new student
        assertNotEquals(result.indexOf("Att Unit"), result.lastIndexOf("Att Unit"));
    }

    @Test
    public void testGetCurrent() throws Exception {
        String result = Grades.getCurrent(session);

        // Assert the result is not null
        assertNotNull(result);

        // The String contains "ATt Unit"
        assertNotEquals(result.indexOf("Att Unit"), -1);

        // The String contains only one instance of "Att Unit"
        assertEquals(result.indexOf("Att Unit"), result.lastIndexOf("Att Unit"));
    }

    @Test
    public void testNull() throws Exception {
        String result = Grades.getAll(null);

        // Assert the result is not null
        assertNotNull(result);

        // The String does not contain "ATt Unit"
        assertEquals(result.indexOf("Att Unit"), -1);
    }
}