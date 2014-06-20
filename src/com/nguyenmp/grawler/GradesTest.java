package com.nguyenmp.grawler;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

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
        List<Grades> result = Grades.getAll(session);

        // Assert the result is not null
        assertNotNull(result);

        // The String contains "ATt Unit"
        assertTrue(result.size() > 1);
    }

    @Test
    public void testGetCurrent() throws Exception {
        List<Grades> result = Grades.getCurrent(session);

        // Assert the result is not null
        assertNotNull(result);

        // The String contains "ATt Unit"
        assertEquals(result.size(), 1);
    }

    @Test
    public void testNull() throws Exception {
        List<Grades> result = Grades.getAll(null);

        // Assert the result is not null
        assertNotNull(result);

        // The String does not contain "ATt Unit"
        assertEquals(result.size(), 0);
    }
}