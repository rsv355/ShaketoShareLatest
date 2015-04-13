package com.developershake2share.Twitter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.developershake2share.R;

import net.londatiga.android.twitter.*;
import net.londatiga.android.twitter.oauth.OauthAccessToken;

public class MainActivity extends BaseActivity {
	private Twitter mTwitter;
	
	public static final String CONSUMER_KEY = "Ha9y7fR1LxarQqPGGhht9HAws";
	public static final String CONSUMER_SECRET = "icAXIRfVt0ypQoRe0pnYUzxO6Ga296owuyMS3R988Go8WUxzgX";
	public static final String CALLBACK_URL = "http://www.shake2share.com";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_twitter_main);
		
		mTwitter = new Twitter(this, CONSUMER_KEY, CONSUMER_SECRET, CALLBACK_URL);
		
		if (mTwitter.sessionActive()) {
			startActivity(new Intent(this, UserActivity.class));
			
			finish();
		} else {
			((Button) findViewById(R.id.btn_connect)).setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					signinTwitter();
				}
			});
		}		
	}

	private void signinTwitter() {
		mTwitter.signin(new Twitter.SigninListener() {				
			@Override
			public void onSuccess(OauthAccessToken accessToken, String userId, String screenName) {
				getCredentials();
			}
			
			@Override
			public void onError(String error) {
				showToast(error);
			}
		});
	}
	
	private void getCredentials() {
		final ProgressDialog progressDlg = new ProgressDialog(this);
		
		progressDlg.setMessage("Getting credentials...");
		progressDlg.setCancelable(false);
		
		progressDlg.show();
		
		TwitterRequest request = new TwitterRequest(mTwitter.getConsumer(), mTwitter.getAccessToken());
		
		request.verifyCredentials(new TwitterRequest.VerifyCredentialListener() {
			
			@Override
			public void onSuccess(TwitterUser user) {
				progressDlg.dismiss();
				
				showToast("Hello " + user.name);
				
				saveCredential(user.screenName, user.name, user.profileImageUrl);
				
				startActivity(new Intent(getActivity(), UserActivity.class));
				
				finish();
			}
			
			@Override
			public void onError(String error) {
				progressDlg.dismiss();
				
				showToast(error);
			}
		});
	}
}