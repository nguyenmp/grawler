package com.nguyenmp.grawler.tests;

import com.nguyenmp.grawler.Credentials;
import com.nguyenmp.grawler.GoldSession;
import com.nguyenmp.grawler.Grawler;
import com.nguyenmp.grawler.utils.HttpClientFactory;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class GrawlerTest {
    @Test
    public void testLogin() throws HttpClientFactory.SSHException, IOException {
        GoldSession session = Grawler.login(Credentials.username(), Credentials.password());
        assertTrue(session != null);
        assertTrue(session.getCookies() != null);
        assertTrue(session.getCookies().length > 0);
        assertTrue(session.getViewState() != null);
        assertTrue(session.getViewState().length() > 0);
    }

    @Test
    public void testFailure1() throws HttpClientFactory.SSHException, IOException {
        GoldSession session = Grawler.login("asfd" + Credentials.username(), Credentials.password());
        assertTrue(session == null);
    }

    @Test
    public void testFailure2() throws HttpClientFactory.SSHException, IOException {
        GoldSession session = Grawler.login(Credentials.username(), "asdf" + Credentials.password());
        assertTrue(session == null);
    }

    @Test
    public void testFailure3() throws HttpClientFactory.SSHException, IOException {
        GoldSession session = Grawler.login(Credentials.username() + "asdf", Credentials.password());
        assertTrue(session == null);
    }

    @Test
    public void testFailure4() throws HttpClientFactory.SSHException, IOException {
        GoldSession session = Grawler.login(Credentials.username(), Credentials.password() + "asdf");
        assertTrue(session == null);
    }

    @Test
    public void testNull1() throws HttpClientFactory.SSHException, IOException {
        GoldSession session = Grawler.login(null, Credentials.password());
        assertTrue(session == null);
    }

    @Test
    public void testNull2() throws HttpClientFactory.SSHException, IOException {
        GoldSession session = Grawler.login(Credentials.username(), null);
        assertTrue(session == null);
    }

    @Test
    public void testNull3() throws HttpClientFactory.SSHException, IOException {
        GoldSession session = Grawler.login(null, null);
        assertTrue(session == null);
    }

    @Test
    public void testEmptyString1() throws HttpClientFactory.SSHException, IOException {
        GoldSession session = Grawler.login("", Credentials.password());
        assertTrue(session == null);
    }

    @Test
    public void testEmptyString2() throws HttpClientFactory.SSHException, IOException {
        GoldSession session = Grawler.login(Credentials.username(), "");
        assertTrue(session == null);
    }

    @Test
    public void testEmptyString3() throws HttpClientFactory.SSHException, IOException {
        GoldSession session = Grawler.login("", "");
        assertTrue(session == null);
    }
}
