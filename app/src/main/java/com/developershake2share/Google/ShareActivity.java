/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.developershake2share.Google;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.plus.PlusShare;
import com.developershake2share.R;

import java.io.ByteArrayOutputStream;

/**
 * Example of sharing with Google+ through the ACTION_SEND intent.
 */
public class ShareActivity extends Activity implements View.OnClickListener,
        DialogInterface.OnCancelListener {
    protected static final String TAG = "ShareActivity";

    private static final String STATE_SHARING = "state_sharing";

    private static final int DIALOG_GET_GOOGLE_PLAY_SERVICES = 1;

    private static final int PICK_MEDIA_REQUEST_CODE=3;
    private static final int SHARE_MEDIA_REQUEST_CODE = 9;

    private static final int REQUEST_CODE_INTERACTIVE_POST = 1;
    private static final int REQUEST_CODE_GET_GOOGLE_PLAY_SERVICES = 2;


    private static  int REQUEST;

    private static final int CAMERA_REQUEST = 500;
    private static final int GALLERY_REQUEST = 300;
    final CharSequence[] items = { "Take Photo", "Choose from Gallery" };

    /** The button should say "View item" in English. */
    private static final String LABEL_VIEW_ITEM = "VIEW_ITEM";

    private EditText mEditSendText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_activity);

        Button sendButton = (Button) findViewById(R.id.send_interactive_button);
        Button sendPhoto = (Button) findViewById(R.id.sendPhoto);


        sendButton.setOnClickListener(this);
        sendPhoto.setOnClickListener(this);

        mEditSendText = (EditText) findViewById(R.id.share_prefill_edit);
        int available = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (available != ConnectionResult.SUCCESS) {
            showDialog(DIALOG_GET_GOOGLE_PLAY_SERVICES);
        }


    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id != DIALOG_GET_GOOGLE_PLAY_SERVICES) {
            return super.onCreateDialog(id);
        }

        int available = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (available == ConnectionResult.SUCCESS) {
            return null;
        }
        if (GooglePlayServicesUtil.isUserRecoverableError(available)) {
            return GooglePlayServicesUtil.getErrorDialog(
                    available, this, REQUEST_CODE_GET_GOOGLE_PLAY_SERVICES, this);
        }
        return new AlertDialog.Builder(this)
                .setMessage(R.string.plus_generic_error)
                .setCancelable(true)
                .setOnCancelListener(this)
                .create();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

          case R.id.send_interactive_button:
                startActivityForResult(getInteractivePostIntent(), REQUEST_CODE_INTERACTIVE_POST);
                return;

          case R.id.sendPhoto:
              processShareMedia();


                break;


        }
    }


    private void processShareMedia() {


        AlertDialog.Builder builder = new AlertDialog.Builder(ShareActivity.this);
        builder.setTitle("Upload Image");

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    REQUEST = 1;
                    startActivityForResult(takePicture, PICK_MEDIA_REQUEST_CODE);
                    Log.e("Camera ","exit");

                } else if (items[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    REQUEST =2;
                    startActivityForResult(pickPhoto , PICK_MEDIA_REQUEST_CODE);
                }
            }
        });
        builder.show();






       /* Intent photoPicker = new Intent(Intent.ACTION_PICK);
        photoPicker.setType("video*//*, image*//*");
        startActivityForResult(photoPicker, PICK_MEDIA_REQUEST_CODE);*/

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_CODE_GET_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Log.e(TAG, "Unable to sign the user in.");
                    finish();
                }
                break;

            case REQUEST_CODE_INTERACTIVE_POST:
                if (resultCode != RESULT_OK) {
                    Log.e(TAG, "Failed to create interactive post");
                }
                break;

            case PICK_MEDIA_REQUEST_CODE:


                if(REQUEST == 1){



                    Bitmap bmp = (Bitmap) data.getExtras().get("data");
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();

                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();


                    // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                    Uri tempUri = getImageUri(ShareActivity.this, bmp);
                //    ProfileImageName = String.valueOf(tempUri);
                    // CALL THIS METHOD TO GET THE ACTUAL PATH
                 //   ProfileImagePath = new File(getRealPathFromURI(tempUri));


                    Uri selectedImage = tempUri;
                    ContentResolver cr = this.getContentResolver();
                    String mime = cr.getType(selectedImage);

                    PlusShare.Builder share = new PlusShare.Builder(this);
                    share.setText("Shake to Share");
                    share.addStream(selectedImage);
                    share.setType(mime);
                    startActivityForResult(share.getIntent(),
                            SHARE_MEDIA_REQUEST_CODE);

                }else if(REQUEST == 2){
                    Uri selectedImage = data.getData();
                    ContentResolver cr = this.getContentResolver();
                    String mime = cr.getType(selectedImage);

                    PlusShare.Builder share = new PlusShare.Builder(this);
                    share.setText("Shake to Share");
                    share.addStream(selectedImage);
                    share.setType(mime);
                    startActivityForResult(share.getIntent(),
                            SHARE_MEDIA_REQUEST_CODE);

                }else{
                    Toast.makeText(ShareActivity.this,"Problem Occur",Toast.LENGTH_LONG).show();
                }





              /*  if (resultCode == RESULT_OK) {

                    Uri selectedImage = data.getData();
                    ContentResolver cr = this.getContentResolver();
                    String mime = cr.getType(selectedImage);

                    PlusShare.Builder share = new PlusShare.Builder(this);
                    share.setText("Shake to Share");
                    share.addStream(selectedImage);
                    share.setType(mime);
                    startActivityForResult(share.getIntent(),
                            SHARE_MEDIA_REQUEST_CODE);


                }
                else{
                    Toast.makeText(ShareActivity.this,"Problem Occur",Toast.LENGTH_LONG).show();
                }*/

                break;
        }


    }



    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Titlekris", null);
        return Uri.parse(path);
    }



    private Intent getInteractivePostIntent() {
        // Create an interactive post with the "VIEW_ITEM" label. This will
        // create an enhanced share dialog when the post is shared on Google+.
        // When the user clicks on the deep link, ParseDeepLinkActivity will
        // immediately parse the deep link, and route to the appropriate resource.
         String photoUri="";

        String action = "/?view=true";
        Uri callToActionUrl = Uri.parse(getString(R.string.plus_example_deep_link_url) + action);
        String callToActionDeepLinkId = getString(R.string.plus_example_deep_link_id) + action;

        // Create an interactive post builder.
        PlusShare.Builder builder = new PlusShare.Builder(this);

        // Set call-to-action metadata.
        builder.addCallToAction(LABEL_VIEW_ITEM, callToActionUrl, callToActionDeepLinkId);

        // Set the target url (for desktop use).
        builder.setContentUrl(Uri.parse(getString(R.string.plus_example_deep_link_url)));

        // Set the target deep-link ID (for mobile use).
        builder.setContentDeepLinkId(getString(R.string.plus_example_deep_link_id),
                null, null, null);

        // Set the pre-filled message.
        builder.setText(mEditSendText.getText().toString());
     //   builder.setType("image/jpeg");
     //   builder.setStream(Uri.parse(photoUri));

        return builder.getIntent();
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {
        Log.e(TAG, "Unable to sign the user in.");
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
               /* Intent intent = new Intent(this, PlusSampleActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();*/
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
