package com.developershake2share.Facebook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AppEventsLogger;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.developershake2share.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;


public class FacebookHomeActivity extends ActionBarActivity {
    private static final int FACEBOOK_LOGIN_REQUEST_CODE =1 ;
    com.facebook.widget.LoginButton authButton ;
    TextView txt;
    private static final String PERMISSION = "publish_actions";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_main);

        authButton  = (com.facebook.widget.LoginButton)findViewById(R.id.authButton);
        authButton .setReadPermissions(Arrays.asList("public_profile"));

        authButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Session session = Session.getActiveSession();
                if (session != null) {
                    if (!session.isOpened() && !session.isClosed()) {
                        session.openForRead(new Session.OpenRequest(FacebookHomeActivity.this).setPermissions(Arrays.asList("public_profile")).setCallback(statusCallback));
                    } else {
                        Session.openActiveSession(FacebookHomeActivity.this, true, statusCallback);
                    }
                }
            }
        });

         txt = (TextView)findViewById(R.id.txt);

    }

    private Session.StatusCallback statusCallback =  new SessionStatusCallback();



    private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            // Respond to session state changes, ex: updating the view
        }
    }


    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            txt.setVisibility(View.VISIBLE);

            Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

                @Override
                public void onCompleted(GraphUser user, Response response) {
                    if (user != null) {
                        // Display the parsed user info
                        txt.setText(buildUserInfoDisplay(user));
                    }
                }


            });
        } else if (state.isClosed()) {
            txt.setVisibility(View.INVISIBLE);
        }
    }

    private String buildUserInfoDisplay(GraphUser user) {
        StringBuilder userInfo = new StringBuilder("");

        // Example: typed access (name)
        // - no special permissions required
        userInfo.append(String.format("Name: %s\n\n",
                user.getName()));

        // Example: typed access (birthday)
        // - requires user_birthday permission
        userInfo.append(String.format("Birthday: %s\n\n",
                user.getBirthday()));

        // Example: partially typed access, to location field,
        // name key (location)
        // - requires user_location permission
        userInfo.append(String.format("Location: %s\n\n",
                user.getLocation().getProperty("name")));

        // Example: access via property name (locale)
        // - no special permissions required
        userInfo.append(String.format("Locale: %s\n\n",
                user.getProperty("locale")));

        // Example: access via key for array (languages)
        // - requires user_likes permission
        JSONArray languages = (JSONArray)user.getProperty("languages");
        if (languages.length() > 0) {
            ArrayList<String> languageNames = new ArrayList<String> ();
            for (int i=0; i < languages.length(); i++) {
                JSONObject language = languages.optJSONObject(i);
                // Add the language name to a list. Use JSON
                // methods to get access to the name field.
                languageNames.add(language.optString("name"));
            }
            userInfo.append(String.format("Languages: %s\n\n",
                    languageNames.toString()));
        }

        return userInfo.toString();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FacebookHomeActivity.FACEBOOK_LOGIN_REQUEST_CODE) {

            if (resultCode == Activity.RESULT_OK) {
               // String accessToken = data.getStringExtra(FacebookLoginActivity.EXTRA_FACEBOOK_ACCESS_TOKEN);
              Toast.makeText(this, "Access Token: " , Toast.LENGTH_LONG).show();
            }
            else {
              //  String errorMessage = data.getStringExtra(FacebookHomeActivity.EXTRA_ERROR_MESSAGE);
                Toast.makeText(this, "Error: ", Toast.LENGTH_LONG).show();
            }

        }
    }
}
