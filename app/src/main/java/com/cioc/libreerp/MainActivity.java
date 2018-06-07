package com.cioc.libreerp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import cz.msebera.android.httpclient.Header;


public class MainActivity extends AppCompatActivity {

    TextView userName, emailId, mobileNo;
    ImageView profilePic;
    Button logoutButton, updateButton, routeLists;
    SessionManager sessionManager;
    Backend backend;
    AsyncHttpClient httpclient;
    File file1;
    static int pk;
    public static Switch sw;
    boolean serviveRuning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sessionManager = new SessionManager(this);

        getSupportActionBar().hide();

        backend = new Backend(this);
        httpclient = backend.getHTTPClient();

        userName = findViewById(R.id.username);
        emailId = findViewById(R.id.emailId);
        mobileNo = findViewById(R.id.mobileNo);
//        routeLists = findViewById(R.id.route_lists);
//        sw = findViewById(R.id.sw);

        profilePic = findViewById(R.id.profile_image);


//        serviveRuning = sessionManager.getStatus();
//
//        sw.setChecked(serviveRuning);
//        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    sessionManager.setStatus(isChecked);
//                    startService(new Intent(MainActivity.this, BackgroundService.class));
                    startService(new Intent(MainActivity.this, BackgroundService.class));
//                } else {
//                    sessionManager.setStatus(isChecked);
//                    stopService(new Intent(MainActivity.this, BackgroundService.class));
//                    stopService(new Intent(MainActivity.this, LocationService.class));
//                }
//            }
//        });


        httpclient.get(Backend.serverUrl + "/api/HR/users/?mode=mySelf&format=json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.e("MainActivity","onSuccess");
                try {
                    JSONObject usrObj = response.getJSONObject(0);
                    pk = usrObj.getInt("pk");
                    String username = usrObj.getString("username");
                    String firstName = usrObj.getString("first_name");
                    String lastName = usrObj.getString("last_name");
                    String email = usrObj.getString("email");
                    JSONObject profileObj = usrObj.getJSONObject("profile");

                    String dpLink = profileObj.getString("displayPicture");
                    String mobile = profileObj.getString("mobile");

                    userName.setText(firstName+" "+lastName);
                    emailId.setText(email);
                    if (!mobile.equals("null"))
                        mobileNo.setText(mobile);

                    String[] image = dpLink.split("/"); //Backend.serverUrl+"/media/HR/images/DP/"
                    String dp = image[7];
                    Log.e("image "+dpLink,""+dp);

                    httpclient.get(dpLink, new FileAsyncHttpResponseHandler(MainActivity.this) {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, File file) {
                            // Do something with the file `response`

                            FileOutputStream outputStream;
                            try {
                                file1 = new File(Environment.getExternalStorageDirectory()+"/CIOC"+ "/" + dp);
                                if (file1.exists())
                                    file1.delete();
                                outputStream = new FileOutputStream(file1);
                                outputStream.write(dp.getBytes());
                                outputStream.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Log.e("image",""+file1.getAbsolutePath());
                            Bitmap pp = BitmapFactory.decodeFile(file.getAbsolutePath());
                            profilePic.setImageBitmap(pp);
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers,Throwable e, File file) {
                            // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                            Log.e("failure-image",""+file.getAbsolutePath());
                            System.out.println("failure");
                            System.out.println(statusCode);
                        }
                    });


                } catch (JSONException e){
                    e.printStackTrace();
                }

                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("MainActivity","onFailure");
            }

            @Override
            public void onFinish() {
                super.onFinish();
                Log.e("MainActivity","onFinish");
            }
        });

        logoutButton = findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Logout ?")
                        .setMessage("Are you sure you want to logout ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sessionManager.clearAll();
                                File dir = new File(Environment.getExternalStorageDirectory()+"/CIOC");
                                Log.e("MainActivity",""+Environment.getExternalStorageDirectory()+"/CIOC");
                                if (dir.exists())
                                if (dir.isDirectory()) {
                                    String[] children = dir.list();
                                    for (int i = 0; i < children.length; i++)
                                    {
                                        new File(dir, children[i]).delete();
                                    }
                                    dir.delete();
                                }
                                stopService(new Intent(MainActivity.this, BackgroundService.class));
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                finish();
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

    }


    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


    public void home(View v){
        startActivity(new Intent(this, HomeActivity.class));
    }

    @Override
    public void onDestroy() {
//            Log.w("onDestroy", " Destroyed Notification Service");
        super.onDestroy();
        Intent intent = new Intent("com.cioc.libreerp.backendservice");
        intent.putExtra("yourvalue", "torestore");
        sendBroadcast(intent);
    }

}
