package org.wingy.chan.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import org.wingy.chan.ChanApplication;
import org.wingy.chan.R;

public class BanActivity extends Activity {

    private EditText banReasonField;
    private EditText banLengthField;
    private EditText publicBanMessage;
    private boolean isPublicBan;

    private String board;
    private int postNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ban_user);

        banReasonField = (EditText) findViewById(R.id.banReasonField);
        banLengthField = (EditText) findViewById(R.id.banLengthField);
        publicBanMessage = (EditText) findViewById(R.id.publicBanMessage);
        isPublicBan = false;

        Bundle extras = getIntent().getExtras();
        board = extras.getString("board");
        postNo = extras.getInt("no");

        TextView banner = (TextView) findViewById(R.id.banUserWithPostNumberMessage);
        String new_message = banner.getText().toString();
        new_message += " "+ postNo;
        banner.setText(new_message);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ban, menu);
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

    public void onPublicBanBoxClicked(View view) {
        isPublicBan = ((CheckBox)view).isChecked();
        publicBanMessage.setEnabled(isPublicBan);
    }

    public void onBanButtonClicked(View view) {
        String message = publicBanMessage.getText().toString();
        String reason = banReasonField.getText().toString();
        String length = banLengthField.getText().toString();

        ChanApplication.getModManager().banUser(
            board, postNo, isPublicBan, message,
            reason, length
        );
        finish();
    }

    public void onCancelButtonClicked(View view) {
        finish();
    }

}
