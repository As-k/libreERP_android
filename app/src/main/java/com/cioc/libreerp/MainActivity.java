package com.cioc.libreerp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import cz.msebera.android.httpclient.Header;



import io.crossbar.autobahn.wamp.Client;
import io.crossbar.autobahn.wamp.Session;
import io.crossbar.autobahn.wamp.types.CallResult;
import io.crossbar.autobahn.wamp.types.CloseDetails;
import io.crossbar.autobahn.wamp.types.EventDetails;
import io.crossbar.autobahn.wamp.types.ExitInfo;
import io.crossbar.autobahn.wamp.types.InvocationDetails;
import io.crossbar.autobahn.wamp.types.Publication;
import io.crossbar.autobahn.wamp.types.PublishOptions;
import io.crossbar.autobahn.wamp.types.Registration;
import io.crossbar.autobahn.wamp.types.SessionDetails;
import io.crossbar.autobahn.wamp.types.Subscription;




//import static com.cioc.libreerp.Backend.getHTTPClient;

public class MainActivity extends AppCompatActivity {

    TextView userName, emailId, mobileNo;
    ImageView profilePic;
    Button logoutButton;
    SessionManager sessionManager;
    Backend backend;
    AsyncHttpClient httpclient;
    File file1;
    static int pk;

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

        profilePic = findViewById(R.id.profile_image);


        httpclient.get(Backend.serverUrl + "/api/HR/users/?mode=mySelf&format=json", new JsonHttpResponseHandler() { //
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
                    Log.e("image",""+dp);
//                    for (int item=0; item<image.length; item++) {
//                        System.out.println("item = " + item);
//                        Log.e("image"+item,""+image[item]);
//                    }

//                    String path = LoginActivity.file.getAbsolutePath()+"/image";
//                    file1 = LoginActivity.file.getAbsoluteFile();
                    FileOutputStream outputStream;
                    try {
                        file1 = new File(LoginActivity.file.getAbsolutePath());
                        outputStream = new FileOutputStream(file1+"/"+dp);
                        outputStream.write(dp.getBytes());
                        outputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Bitmap bit = BitmapFactory.decodeFile(file1.getAbsolutePath()+"/"+dp);
                    if (bit != null){
                        profilePic.setImageBitmap(bit);
                    }



                }catch (JSONException e){
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
                sessionManager.clearAll();
                getApplicationContext().deleteFile(LoginActivity.fileName);
                startActivity(new Intent(MainActivity.this, FlashActivity.class));
                finish();
            }
        });






    }

    public void changePassword(View v){
        startActivity(new Intent(this, ChangePasswordActivity.class));
    }


}
