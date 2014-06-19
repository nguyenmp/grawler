package com.nguyenmp.grawler.tests;

import com.nguyenmp.grawler.Credentials;
import com.nguyenmp.grawler.GoldSession;
import com.nguyenmp.grawler.Grawler;
import com.nguyenmp.grawler.StudentSchedule;
import com.nguyenmp.grawler.utils.HttpClientFactory;
import org.junit.Test;

import java.io.IOException;

public class StudentScheduleTest {
    @Test
    public void exceptionTest() throws IOException, HttpClientFactory.SSHException {
        GoldSession session = Grawler.login(Credentials.username(), Credentials.password());
        StudentSchedule.getSchedule(session);
    }
}
