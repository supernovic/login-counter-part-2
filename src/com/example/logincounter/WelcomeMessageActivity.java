package com.example.logincounter;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class WelcomeMessageActivity extends Activity {
	
	private TextView welcomeMessageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// grab the intent
        Intent intent = getIntent();
        String username = intent.getStringExtra(MainActivity.USERNAME);
        Integer count = intent.getIntExtra(MainActivity.LOGIN_COUNT, 0);
        String welcomeMessage = String.format("Welcome %s.\n  You have logged in %d times", username, count);
		setContentView(R.layout.activity_welcome_message);
        welcomeMessageView = (TextView) findViewById(R.id.welcomeMessage);
        welcomeMessageView.setText(welcomeMessage);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.welcome_message, menu);
		return true;
	}
	
    public void logout(View view) {
    	Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
