package com.tumblr.apps.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.tumblr.apps.R;
import com.tumblr.apps.TumblrSnapApp;
import com.tumblr.apps.models.User;

public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    public void onLogoutButton(View view) {
        TumblrSnapApp.getClient().clearAccessToken();
        User.setCurrentUser(null);

        finish();
    }

}
