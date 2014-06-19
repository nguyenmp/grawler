package com.nguyenmp.grawler;

import com.nguyenmp.grawler.utils.HttpClientFactory;
import com.nguyenmp.grawler.utils.HttpContextFactory;
import com.nguyenmp.grawler.utils.Utils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.util.Arrays;

/**
 * A wrapper object to store the cookies returned 
 * from authenticating with the GOLD server.
 */
public class GoldSession {
	private String mViewState = null;
	private final Cookie[] mCookies;
    private HeartbeatThread mHeartbeat = null;

	/**
	 * Creates a new wrapper object that will hold the cookies given.
	 * @param cookies the cookies from the GOLD server.  This value 
	 *                should remain encoded.
	 */
	GoldSession(Cookie[] cookies) {
		mCookies = cookies;
	}

	/**
	 * Gets the cookies from this wrapper.  The value will be the 
	 * literal value of the cookies, including the name and any other 
	 * parameters when see from the header.  The name and value will 
	 * also be encoded.
	 * @return an array of Strings that will represent the cookies 
	 * scanned from the header file.
	 */
	public Cookie[] getCookies() {
		return mCookies;
	}
	
	public String getViewState() {
		return mViewState;
	}
	
	public void setViewState(String viewState) {
		mViewState = viewState;
	}
	
	public String toString() {
		String value = "null";
		if (mCookies != null) value = Arrays.toString(mCookies);
		
		return value;
	}

    public String getHomeMessage() throws IOException, HttpClientFactory.SSHException {
        HttpClient client = HttpClientFactory.getClient();
        HttpContext context = HttpContextFactory.getContext(this);

        HttpGet get = new HttpGet("https://my.sa.ucsb.edu/gold/Home.aspx");
        HttpResponse response = client.execute(get, context);
        String html = Utils.toString(response);
        return html+ "";
    }

    public void startHeartbeat() {
        if (mHeartbeat == null) {
            mHeartbeat = new HeartbeatThread(this);
            mHeartbeat.start();
        }
    }

    public void stopHeartbeat() {
        if (mHeartbeat != null) {
            mHeartbeat.cancel();
            mHeartbeat = null;
        }
    }

    private final class HeartbeatThread extends Thread {
        // 5 minutes times 60 seconds a minute times 1000 milliseconds per second
        /** 5 minute delay in milliseconds */
        private static final int DELAY = 5 * 60 * 1000;
        private final GoldSession session;
        private boolean canceled = false;

        public HeartbeatThread(GoldSession session) {
            this.session = session;
        }

        public void cancel() {
            canceled = true;
        }

        @Override
        public void run() {
            while (!canceled) {
                // Do heartbeat
                try {
                    session.getHomeMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (HttpClientFactory.SSHException e) {
                    e.printStackTrace();
                }

                // Then sleep
                try {
                    sleep(DELAY);
                } catch (InterruptedException e) {e.printStackTrace();}
            }
        }
    }
}
