package com.shake2share.Twitter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.shake2share.R;


import net.londatiga.android.twitter.Twitter;
import net.londatiga.android.twitter.TwitterRequest;
import net.londatiga.android.twitter.util.Debug;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class UserActivity extends BaseActivity {
	private Twitter mTwitter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_twitter_user);
		
		mTwitter = new Twitter(this, MainActivity.CONSUMER_KEY, MainActivity.CONSUMER_SECRET, MainActivity.CALLBACK_URL);
		
		((Button) findViewById(R.id.btn_logout)).setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				mTwitter.clearSession();
				
				clearCredential();
				
				startActivity(new Intent(getActivity(), MainActivity.class));
				
				finish();
			}
		});


        ((TextView) findViewById(R.id.tv_name)).setText(getUserName());
		((TextView) findViewById(R.id.tv_username)).setText(getScreenName());
		
		final EditText tweetEt = (EditText) findViewById(R.id.et_message);
		
		((Button) findViewById(R.id.btn_post)).setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				String status = tweetEt.getText().toString();
				
				if (status.equals("")) {
					showToast("Please write your status");
					return;
				}
				
				updateStatus(status);
			}
		});


        ((Button) findViewById(R.id.btn_imgpost)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

               updateImageStatus();
             //   updateStatus("kukur");
            }
        });



		
		ImageView userIv = (ImageView) findViewById(R.id.iv_user);
		
		DisplayImageOptions displayOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_user)
				.showImageForEmptyUri(R.drawable.ic_user)
				.showImageOnFail(R.drawable.ic_user)
				.cacheInMemory(true)
				.cacheOnDisc(false)
				.considerExifParams(true)
				.build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)                		                       
		        .writeDebugLogs()
		        .defaultDisplayImageOptions(displayOptions)		        
		        .build();
	
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.init(config);
		
		AnimateFirstDisplayListener animate  = new AnimateFirstDisplayListener();
		
		imageLoader.displayImage(getProfilePicture(), userIv, animate);
	}

    private void updateImageStatus() {
        final ProgressDialog progressDlg = new ProgressDialog(this);

        progressDlg.setMessage("Sending...");
        progressDlg.setCancelable(false);

        progressDlg.show();

        TwitterRequest request 		= new TwitterRequest(mTwitter.getConsumer(), mTwitter.getAccessToken());

        String updateStatusUrl		= "https://upload.twitter.com/1.1/media/upload.json";

        List<NameValuePair> params 	= new ArrayList<NameValuePair>(1);

        Resources r = this.getResources();
        Bitmap bm = BitmapFactory.decodeResource(r, R.drawable.ic_launcher);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();
        String  encodedImage = Base64.encodeToString(b, Base64.DEFAULT);


        params.add(new BasicNameValuePair("media",encodedImage));

        request.createRequest("POST", updateStatusUrl, params, new TwitterRequest.RequestListener() {

            @Override
            public void onSuccess(String response) {
                progressDlg.dismiss();

                try {
                    JSONObject jobj = new JSONObject(response);
                    updateimage2("new image",jobj.getString("media_id"));
                }catch (Exception e){
                   Log.e("exc", e.toString());
                }




                Debug.i(response);
            }

            @Override
            public void onError(String error) {
                showToast(error);

                progressDlg.dismiss();
            }
        });
    }

    private void updateimage2(String status,String mediaid) {
        final ProgressDialog progressDlg = new ProgressDialog(this);

        progressDlg.setMessage("Sending...");
        progressDlg.setCancelable(false);

        progressDlg.show();

        TwitterRequest request 		= new TwitterRequest(mTwitter.getConsumer(), mTwitter.getAccessToken());

        String updateStatusUrl		= "https://api.twitter.com/1.1/statuses/update.json";

        List<NameValuePair> params 	= new ArrayList<NameValuePair>(1);

        params.add(new BasicNameValuePair("status", status));
        params.add(new BasicNameValuePair("media_ids", mediaid));

        request.createRequest("POST", updateStatusUrl, params, new TwitterRequest.RequestListener() {

            @Override
            public void onSuccess(String response) {
                progressDlg.dismiss();

                showToast("Image Updated Sucessfully");

                Debug.i(response);
            }

            @Override
            public void onError(String error) {
                showToast(error);

                progressDlg.dismiss();
            }
        });
    }

	
	private void updateStatus(String status) {
		final ProgressDialog progressDlg = new ProgressDialog(this);
		
		progressDlg.setMessage("Sending...");
		progressDlg.setCancelable(false);
		
		progressDlg.show();
		
		TwitterRequest request 		= new TwitterRequest(mTwitter.getConsumer(), mTwitter.getAccessToken());
		
		String updateStatusUrl		= "https://api.twitter.com/1.1/statuses/update.json";
		
		List<NameValuePair> params 	= new ArrayList<NameValuePair>(1);
		
		params.add(new BasicNameValuePair("status", status));

		
		request.createRequest("POST", updateStatusUrl, params, new TwitterRequest.RequestListener() {
			
			@Override
			public void onSuccess(String response) {
				progressDlg.dismiss();

                showToast("Text Updated Sucessfully");
				
				Debug.i(response);
			}
			
			@Override
			public void onError(String error) {
				showToast(error);
				
				progressDlg.dismiss();
			}
		});
	}
	
	public static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}
}