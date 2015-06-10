/*
 * Exodus Mod Manager - A class for managing actions on 8chan's mod.php
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

import android.os.AsyncTask;
import android.widget.Toast;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.message.BasicNameValuePair;

import org.apache.http.protocol.HTTP;
import org.wingy.chan.ChanApplication;
import org.wingy.chan.chan.ChanUrls;

public class ModManager {
    private LoginManager loginManager;
    private DefaultHttpClient httpclient;
    private BasicResponseHandler responseHandler;

    private class DeleteTask extends AsyncTask<String, Void, Integer> {
        protected Integer doInBackground(String... params) {
            if (!loginManager.isLoggedIn()) { return 1; }
            try {
                // GET mod.php?/boardname/delete/postno
                String url = ChanUrls.getModDeleteUrl(params[0], Integer.valueOf(params[1]));
                HttpGet httpget = new HttpGet(url);
                HttpResponse response = httpclient.execute(httpget);

                // Get redirect link
                String responseText = responseHandler.handleResponse(response);
                int redirectIndex = responseText.indexOf("Click to proceed to");
                if (redirectIndex < 0) { return 2; }
                String redirectString = responseText.substring(redirectIndex - 10, redirectIndex - 2);
                String newurl = url + "/" + redirectString;

                // Execute delete
                httpget = new HttpGet(newurl);
                response = httpclient.execute(httpget);
                if (response.getStatusLine().getStatusCode() == 400) { return 3; }

                return 0;
            }
            catch (IOException e) { return 4; }
        }

        protected void onPostExecute(Integer result) {
            switch (result) {
                case 0: // success
                    toastReport("Post deleted");
                    break;
                case 1: // not logged in
                    toastReport("You are not logged in");
                    break;
                case 2: // no redirect link
                    toastReport("Could not find delete redirect link");
                    break;
                case 3: // not mod
                    toastReport("Could not delete post (are you a mod?)");
                    break;
                case 4: // IOException
                    toastReport("Cannot connect to server");
                    break;
            }
        }
    }

    private class SpoilerTask extends AsyncTask<String, Void, Integer> {
        protected Integer doInBackground(String... params) {
            if (!loginManager.isLoggedIn()) { return 1; }
            try {
                // GET mod.php?/boardname/spoiler_all/postno
                String url = ChanUrls.getModSpoilerUrl(params[0], Integer.valueOf(params[1]));
                HttpGet httpget = new HttpGet(url);
                HttpResponse response = httpclient.execute(httpget);

                // Get redirect link
                String responseText = responseHandler.handleResponse(response);
                int redirectIndex = responseText.indexOf("Click to proceed to");
                if (redirectIndex < 0) { return 2; }
                String redirectString = responseText.substring(redirectIndex - 10, redirectIndex - 2);
                String newurl = url + "/" + redirectString;

                // Execute spoiler all
                httpget = new HttpGet(newurl);
                response = httpclient.execute(httpget);
                if (response.getStatusLine().getStatusCode() == 400) { return 3; }

                return 0;
            }
            catch (IOException e) { return 4; }
        }

        protected void onPostExecute(Integer result) {
            switch (result) {
                case 0: // success
                    toastReport("All images spoilered");
                    break;
                case 1: // not logged in
                    toastReport("You are not logged in");
                    break;
                case 2: // no redirect link
                    toastReport("Could not find spoiler redirect link");
                    break;
                case 3: // not mod
                    toastReport("Could not spoiler images (are you a mod?)");
                    break;
                case 4: // IOException
                    toastReport("Cannot connect to server");
                    break;
            }
        }
    }

    private class BanTask extends AsyncTask<String, Void, Integer> {
        protected Integer doInBackground(String... params) {
            if (!loginManager.isLoggedIn()) { return 1; }
            try {
                String board = params[0];
                int postNo = Integer.valueOf(params[1]);
                String isPublic = params[2];
                String banMessage = params[3];
                String banReason = params[4];
                String banLength = params[5];
                String shouldDelete = params[6];
                String banRange = params[7];

                // Get mod.php?/boardname/ban/postno
                String url = ChanUrls.getModBanUrl(board, postNo);
                HttpGet httpget = new HttpGet(url);
                HttpResponse response = httpclient.execute(httpget);

                // Can't access page (You do not have permission to do that.)
                if (response.getStatusLine().getStatusCode() == 400) { return 3; }

                // Otherwise, find the token for the ban
                String responseText = responseHandler.handleResponse(response);
                int redirectIndex = responseText.indexOf("name=\"token\" value=");
                if (redirectIndex < 0) { return 2; }
                String token = responseText.substring(redirectIndex + 20, redirectIndex + 28);

                // Generate post args for final ban request
                List<NameValuePair> postArgs = new ArrayList<>(8);
                postArgs.add(new BasicNameValuePair("token", token));
                postArgs.add(new BasicNameValuePair("delete", shouldDelete));
                postArgs.add(new BasicNameValuePair("range", banRange));
                postArgs.add(new BasicNameValuePair("reason", banReason));
                postArgs.add(new BasicNameValuePair("public_message", isPublic));
                postArgs.add(new BasicNameValuePair("message", banMessage));
                postArgs.add(new BasicNameValuePair("length", banLength));
                postArgs.add(new BasicNameValuePair("board", board));
                postArgs.add(new BasicNameValuePair("new_ban", "New Ban"));

                // Make ban
                HttpPost httppost = new HttpPost(url);
                httppost.setEntity(new UrlEncodedFormEntity(postArgs, HTTP.UTF_8));
                response = httpclient.execute(httppost);

                if (response.getStatusLine().getStatusCode() == 400) return 3;
                else return 0;
            } catch (IOException e) { return 4; }
        }

        protected void onPostExecute(Integer result) {
            switch (result) {
                case 0: // success
                    toastReport("Ban successful");
                    break;
                case 1: // not logged in
                    toastReport("You are not logged in");
                    break;
                case 2: // can't find ban token
                    toastReport("Can't find ban token");
                    break;
                case 3: // not mod
                    toastReport("Could not ban user (are you a mod?)");
                    break;
                case 4: // IOException
                    toastReport("Cannot connect to server");
                    break;
            }
        }
    }

    public ModManager() {
        loginManager = ChanApplication.getLoginManager();
        httpclient = loginManager.getHttpClient();
        responseHandler = new BasicResponseHandler();
    }

    public void deletePost(String board, int no) {
        new DeleteTask().execute(board, Integer.toString(no));
    }

    public void spoilerImages(String board, int no) {
        new SpoilerTask().execute(board, Integer.toString(no));
    }

    public void banUser(String board, int no, boolean isPublic, String banMessage,
                        String banReason, String banLength) {
        String shouldDelete = "0"; // To implement later as args
        String banRange = ""; // To implement later as args
        String isPublicStr = (isPublic ? "1" : "0");
        if (!isPublic) { banMessage = ""; }
        new BanTask().execute(board, Integer.toString(no), isPublicStr, banMessage,
                              banReason, banLength, shouldDelete, banRange);
    }

    private void toastReport(String report) {
        Toast.makeText(
            ChanApplication.getInstance().getApplicationContext(),
            report, Toast.LENGTH_SHORT
        ).show();
    }
}
