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

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.BasicResponseHandler;

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

    public ModManager() {
        loginManager = ChanApplication.getLoginManager();
        httpclient = loginManager.getHttpClient();
        responseHandler = new BasicResponseHandler();
    }

    public void deletePost(String board, int no) {
        new DeleteTask().execute(board, Integer.toString(no));
    }

    private void toastReport(String report) {
        Toast.makeText(
            ChanApplication.getInstance().getApplicationContext(),
            report, Toast.LENGTH_SHORT
        ).show();
    }
}
