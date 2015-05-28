/*
 * Exodus Login Manager - A class for managing login to 8chan's mod.php
 * Copyright (C) 2015 expeditiousRubyist
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.wingy.chan.core.manager;

import android.util.Log;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import org.wingy.chan.chan.ChanUrls;

public class LoginManager {

    private DefaultHttpClient httpclient;
    private BasicResponseHandler responseHandler;
    private boolean amLoggedIn;

    public LoginManager() {
        httpclient = new DefaultHttpClient();
        responseHandler = new BasicResponseHandler();
        amLoggedIn = false;
    }

    private boolean isCookieValid() {
        HttpGet httpget = new HttpGet(ChanUrls.getModUrl());
        try {
            HttpResponse response = httpclient.execute(httpget);
            String responseString = responseHandler.handleResponse(response);
            return responseString.contains("var inMod = true;");
        }
        // Unable to connect to server. Assume false
        catch (IOException e) {
            return false;
        }
    }

    public boolean isLoggedIn() {
        if (!amLoggedIn) { return false; }
        return isCookieValid();
    }

    public boolean attemptLogin(String username, String password) throws IOException {
        // Convert login credentials to POST arguments
        List<NameValuePair> postArgs = new ArrayList<>(3);
        postArgs.add(new BasicNameValuePair("username", username));
        postArgs.add(new BasicNameValuePair("password", password));
        postArgs.add(new BasicNameValuePair("login", "Continue"));

        // Login to site
        HttpPost httppost = new HttpPost(ChanUrls.getModUrl());
        httppost.setEntity(new UrlEncodedFormEntity(postArgs, HTTP.UTF_8));
        HttpResponse response = httpclient.execute(httppost);

        amLoggedIn = isCookieValid();
        return amLoggedIn;
    }

    public void logout() {
        amLoggedIn = false;
        // TODO: Execute logout on mod.php?
        httpclient.getCookieStore().clear();
    }
}
