package org.wingy.chan.ui.activity;

import java.io.IOException;
import android.app.Activity;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.wingy.chan.R;
import org.wingy.chan.ChanApplication;

public class LoginActivity extends Activity {

    private EditText usernameField;
    private EditText passwordField;

    private class LoginTask extends AsyncTask<String, Void, Boolean> {
        protected Boolean doInBackground(String... loginInfo) {
            String username = loginInfo[0];
            String password = loginInfo[1];
            try {
                Boolean result = ChanApplication.getLoginManager().attemptLogin(username, password);
                return result;
            }
            catch (IOException e) {
                noConnection();
                return false;
            }
        }

        protected void onPostExecute(Boolean result) {
            if (result) { loginSuccessful(); }
            else { loginUnsuccessful(); }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mod_login);
        usernameField = (EditText)findViewById(R.id.mod_login_username);
        passwordField = (EditText)findViewById(R.id.mod_login_password);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onCancelButtonClicked(View v) {
        finish();
    }

    public void onSubmitButtonClicked(View v) {
        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();
        new LoginTask().execute(username, password);
    }

    public void loginSuccessful() {
        Toast.makeText(
            ChanApplication.getInstance().getApplicationContext(),
            "Login successful!",
            Toast.LENGTH_SHORT
        ).show();
        finish();
    }

    public void loginUnsuccessful() {
        Toast.makeText(
            ChanApplication.getInstance().getApplicationContext(),
            "Login failed!",
            Toast.LENGTH_SHORT
        ).show();
    }

    public void noConnection() {
        Toast.makeText(
            ChanApplication.getInstance().getApplicationContext(),
            "Could not connect to server",
            Toast.LENGTH_SHORT
        ).show();
    }
}
