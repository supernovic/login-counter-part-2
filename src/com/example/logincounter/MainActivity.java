package com.example.logincounter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	public final static String LOGIN_COUNT = "com.example.logincounter.LOGIN_COUNT";
	public final static String USERNAME = "com.example.logincounter.USERNAME";
	
	private EditText username;
	private EditText password;
	private TextView messageBox;
	private MainActivity app = this; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		messageBox = (TextView) findViewById(R.id.message_box);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	// called once the login button is clicked
	public void login(View view) {
		String siteURL = "http://arcane-basin-8645.herokuapp.com/users/login";
		// check if there is Internet connection
	    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
	    	// handle json
	    	new jsonParser().execute(siteURL);
	    }
	}

	// called once the add user button is clicked
	public void addUser(View view) {
		String siteURL = "http://arcane-basin-8645.herokuapp.com/users/add";
		// check if there is Internet connection
	    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
	    	// handle json
	    	new jsonParser().execute(siteURL);
	    }
	}
	
	private class jsonParser extends AsyncTask {

		@Override
		protected Object doInBackground(Object... params) {
			try {
				String siteURL = (String) params[0];
				return sendPostReqToUrl(siteURL);
			} catch (IOException e) {
				return null;
			}
		}
		
		protected void onPostExecute(Object o) {
			try {
				JSONObject json = (JSONObject) o;
				if (json != null) {
					switch (json.getInt("errCode")) {
						case -1:
							messageBox.setText("Invalid username and password combination. Please try again.");
							break;
						case -2:
							messageBox.setText("This user name already exists. Please try again");
							break;
						case -3:
							messageBox.setText("The user name should be non-empty and at most 128 characters long. Please try again.");
							break;
						case -4:
							messageBox.setText("The password should be at most 128 characters long. Please try again.");
							break;
						default:
		    				   Intent intent = new Intent(app, WelcomeMessageActivity.class);
		    				   intent.putExtra(LOGIN_COUNT, json.getInt("count"));
		    				   intent.putExtra(USERNAME, username.getText().toString());
		    				   startActivity(intent);
		    				   break;
					}
				}
			} catch (Exception e) {
				messageBox.setText("Something unexpeted occurred.");
			}
		}

		private JSONObject sendPostReqToUrl(String siteURL) throws IOException {
			InputStream in = null;
			HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                HttpPost postRequest = new HttpPost(siteURL);
                json.put("user", username.getText().toString());
                json.put("password", password.getText().toString());
                StringEntity se = new StringEntity(json.toString());  
                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                postRequest.setEntity(se);
                Log.v("TAG", "bout to execute");
                response = client.execute(postRequest);

                if(response != null){
                    in = response.getEntity().getContent(); //Get the data in the entity
                    String jsonString = convertStreamToString(in);
                    Log.v("RESPONSE", jsonString);
                    return new JSONObject(jsonString);
                } else {
                	return null;
                }

            } catch(Exception e) {
                e.printStackTrace();
                return null;
            } finally {
            	if (in != null) {
            		in.close();
            	}
            }
		}

		private String convertStreamToString(InputStream in) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	        StringBuilder sb = new StringBuilder();
	 
	        String line = null;
	        try {
	            while ((line = reader.readLine()) != null) {
	                sb.append(line + "\n");
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                in.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	        return sb.toString();
		}
		
	}

}
