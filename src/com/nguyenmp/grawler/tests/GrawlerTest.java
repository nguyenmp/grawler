package com.nguyenmp.grawler.tests;

import java.io.IOException;
import com.nguyenmp.grawler.*;
import com.nguyenmp.grawler.utils.HttpClientFactory;
import org.junit.Test;

public class GrawlerTest {
    @Test
    public void testLogin() throws HttpClientFactory.SSHException, IOException {
        GoldSession session = Grawler.login(Credentials.username(), Credentials.password());
        assert(session != null);
        assert(session.getCookies() != null);
        assert(session.getCookies().length > 0);
        assert(session.getViewState() != null);
        assert(session.getViewState().length() > 0);
    }
}
