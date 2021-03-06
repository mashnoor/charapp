package com.origo.android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.applozic.mobicomkit.api.account.register.RegistrationResponse;
import com.applozic.mobicomkit.api.account.user.User;
import com.applozic.mobicomkit.api.account.user.UserLoginTask;
import com.applozic.mobicomkit.uiwidgets.ApplozicSetting;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sw926.imagefileselector.ImageFileSelector;


import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;


import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

public class LoginActivity extends Activity {



    @BindView(R.id.etUserName) EditText user_name;
    @BindView(R.id.etUserPhone) EditText user_phone;
    ImageFileSelector mImageFileSelector;
    AsyncHttpClient client;
    String file_location = null;
    ProgressDialog dialog;
    @BindView(R.id.profile_image) CircleImageView profile_pic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if(!HelperClass.getPhone(this).equals("NO"))
        {
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }
        ButterKnife.bind(this);

        //Set Hint color of the EditTexts
        user_name.setHintTextColor(Color.WHITE);
        user_phone.setHintTextColor(Color.WHITE);

        client = new AsyncHttpClient();

        mImageFileSelector =  new ImageFileSelector(this);
        mImageFileSelector.setCallback(new ImageFileSelector.Callback() {
            @Override
            public void onSuccess(final String file) {
               file_location = file;
                profile_pic.setImageURI(Uri.parse(file_location));

            }

            @Override
            public void onError() {
                // 选取图片失败
            }
        });



    }

    private void uploadImageFile() {

        try {
            String uploadId =
                    new MultipartUploadRequest(LoginActivity.this, HelperClass.UPLOAD_IMAGE + user_phone.getText().toString())
                            .addFileToUpload(file_location, "userimage")
                            .setNotificationConfig(new UploadNotificationConfig())
                            .setMaxRetries(2)
                            .startUpload();
        } catch (Exception exc) {
            Log.e("AndroidUploadService", exc.getMessage(), exc);
        }
    }


    //Handle When Join Button is Pressed
    public void join(View v)
    {


        //Fetch the Name
        if(user_name.getText().toString().equals(""))
        {
            user_name.setError("User Name can't be blank");
        }
        else if(user_phone.getText().toString().equals(""))
        {
            user_phone.setError("Phone can't be blank");
        }
        else if(file_location == null)
        {
            showToast("You need a profile picture.");
        }
        else
        {

            dialog = new ProgressDialog(this);
            dialog.setMessage("Creating Account. Please wait...");
            uploadImageFile();
            //Send and add the user to server
            sendAndAddUser();







        }

    }

    private void addUser() {
        UserLoginTask.TaskListener listener = new UserLoginTask.TaskListener() {

            @Override
            public void onSuccess(RegistrationResponse registrationResponse, Context context)
            {
                // After successful registration with Applozic server the callback will come here
                ApplozicSetting.getInstance(context).showStartNewButton();//To show contact list.
                //ApplozicSetting.getInstance(context).enableRegisteredUsersContactCall();//To enable the applozic Registered Users Contact Note:for disable that you can comment this line of code
            }

            @Override
            public void onFailure(RegistrationResponse registrationResponse, Exception exception)
            {
                // If any failure in registration the callback  will come here
            }};

        User user = new User();
        user.setUserId(user_phone.getText().toString().trim()); //userId it can be any unique user identifier
        user.setDisplayName(user_name.getText().toString().trim()); //displayName is the name of the user which will be shown in chat messages

        user.setImageLink(HelperClass.GET_IMAGE_FILE + user_phone.getText().toString().trim());//optional,pass your image link
        new UserLoginTask(user, listener, this).execute((Void) null);
    }

    private void sendAndAddUser() {
       // RequestParams userPhone = new RequestParams("user_phone", user_phone.getText().toString().trim());
        RequestParams params = new RequestParams();
        params.put("user_phone", user_phone.getText().toString().trim());
        params.put("user_name", user_name.getText().toString().trim());

        client.post(HelperClass.ADD_USER_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //showToast("Successfully Added");
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                i.putExtra("user_phone", user_phone.getText().toString().trim());
                HelperClass.savePhone(LoginActivity.this, user_phone.getText().toString().trim());
                dialog.dismiss();
                startActivity(i);
                HelperClass.savePhone(LoginActivity.this, user_phone.getText().toString().trim());
                addUser();

            }



            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showToast("Problem Occured");
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mImageFileSelector.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mImageFileSelector.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mImageFileSelector . onRestoreInstanceState (savedInstanceState);
    }

    public void showToast(String s)
    {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    public void takephoto(View v)
    {

        mImageFileSelector.takePhoto(LoginActivity.this);



    }







}
