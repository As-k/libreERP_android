package com.cioc.libreerp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.client.CookieStore;
import cz.msebera.android.httpclient.cookie.Cookie;

public class LoginActivity extends AppCompatActivity {
    AutoCompleteTextView username, password, otpEdit;
    Button loginButton, getOTP;
    LinearLayout llUsername, llPassword, llotpEdit;
    TextView forgot, goBack;

    Backend backend = new Backend(this);

    Intent mServiceIntent;
    Context context;

    private CookieStore httpCookieStore;
    private AsyncHttpClient client;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor spEditor;
    SessionManager sessionManager;
    boolean res;
    String csrfId, sessionId;

    String STATUS = "status";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();
//
//        mServiceIntent = new Intent(context, BackgroundService.class);
//
//        startService(mServiceIntent);

        sessionManager = new SessionManager(this);

        httpCookieStore = new PersistentCookieStore(this);
        httpCookieStore.clear();
        client = new AsyncHttpClient();
        client.setCookieStore(httpCookieStore);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        otpEdit = findViewById(R.id.otpEdit);


        forgot= findViewById(R.id.forgot_password);
        goBack= findViewById(R.id.go_back);
        goBack.setVisibility(View.GONE);

        llUsername = findViewById(R.id.llUsername);
        llPassword = findViewById(R.id.llPassword);
        llotpEdit = findViewById(R.id.llOtp);
        llotpEdit.setVisibility(View.GONE);

        loginButton = findViewById(R.id.sign_in_button);
        getOTP = findViewById(R.id.get_otp);
        getOTP.setVisibility(View.GONE);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

    }

    public void forgotPassword(View v){
        llPassword.setVisibility(View.GONE);
        loginButton.setVisibility(View.GONE);
        llotpEdit.setVisibility(View.GONE);
        forgot.setVisibility(View.GONE);
        getOTP.setVisibility(View.VISIBLE);
        goBack.setVisibility(View.VISIBLE);

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llPassword.setVisibility(View.VISIBLE);
                llUsername.setVisibility(View.VISIBLE);
                loginButton.setVisibility(View.VISIBLE);
                llotpEdit.setVisibility(View.GONE);
                forgot.setVisibility(View.VISIBLE);
                getOTP.setVisibility(View.GONE);
                goBack.setVisibility(View.GONE);

            }
        });

        getOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llPassword.setVisibility(View.GONE);
                llUsername.setVisibility(View.GONE);
                loginButton.setVisibility(View.GONE);
                forgot.setVisibility(View.GONE);
                llotpEdit.setVisibility(View.VISIBLE);
                getOTP.setVisibility(View.VISIBLE);
                goBack.setVisibility(View.VISIBLE);
            }
        });


    }



    public void login(){
        Toast.makeText(this, backend.serverUrl, Toast.LENGTH_LONG).show();
        String userName = username.getText().toString().trim();
        String pass = password.getText().toString().trim();
        if (userName.isEmpty()){
            username.setError("Empty");
            username.requestFocus();
        } else {
            if (pass.isEmpty()){
                password.setError("Empty");
                password.requestFocus();
            } else {

                res = sessionManager.getStatus();
                csrfId = sessionManager.getCsrfId();
                sessionId = sessionManager.getSessionId();

                if (csrfId.equals("") && sessionId.equals("")) {


                    RequestParams params = new RequestParams();
                    params.put("username", userName);
                    params.put("password", pass);

                    client.post(backend.serverUrl + "/login/?mode=api", params, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject c) {
                            Toast.makeText(LoginActivity.this, "success", Toast.LENGTH_SHORT).show();
                            Log.e("LoginActivity", "  onSuccess");


                            super.onSuccess(statusCode, headers, c);

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject c) {
                            super.onFailure(statusCode, headers, e, c);

                            if (statusCode == 401) {
                                Toast.makeText(LoginActivity.this, "un success", Toast.LENGTH_SHORT).show();
                                Log.e("LoginActivity", "  onFailure");
                            }

                        }

                        @Override
                        public void onFinish() {
                            List<Cookie> lst = httpCookieStore.getCookies();
                            if (lst.isEmpty()) {
                                Toast.makeText(LoginActivity.this, String.format("Error , Empty cookie store"), Toast.LENGTH_SHORT).show();
                            } else {

                                if (lst.size() < 2) {
                                    String msg = String.format("Error while logining, fetal error!");
                                    Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                Cookie csrfCookie = lst.get(0);
                                Cookie sessionCookie = lst.get(1);

                                String csrf_token = csrfCookie.getValue();
                                String session_id = sessionCookie.getValue();

                                sessionManager.setCsrfId(csrf_token);
                                sessionManager.setSessionId(session_id);

                                getUser();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();

                            }
                            Log.e("LoginActivity", "  finished");
                        }
                    });
                } else {
                    getUser();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }
            }
        }
    }

    protected void getUser(){
        client.get(backend.serverUrl + "/api/HR/users/?mode=mySelf&format=json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.e("getUser", "  success");
                try {
                    JSONObject usrObj = response.getJSONObject(0);
                    String username = usrObj.getString("username");
                    String firstName = usrObj.getString("first_name");
                    Integer pk = usrObj.getInt("pk");
                    String lastName = usrObj.getString("last_name");
                    JSONObject profileObj = usrObj.getJSONObject("profile");
                    String DPLink = profileObj.getString("displayPicture");

//                    user = new User(username ,pk);
//                    user.setFirstName(firstName);
//                    user.setLastName(lastName);

//                    client.get(DPLink, new FileAsyncHttpResponseHandler(context) {
//                        @Override
//                        public void onSuccess(int statusCode, Header[] headers, File file) {
//                            // Do something with the file `response`
//                            writeConfigFile(context);
//                            Bitmap pp = BitmapFactory.decodeFile(file.getAbsolutePath());
//                            user.setProfilePicture(pp);
//                            user.saveUserToFile(context);
//                            Intent intent = new Intent(context, HomeActivity.class);
//                            startActivity(intent);
//                        }
//                        @Override
//                        public void onFailure(int statusCode, Header[] headers,Throwable e, File file) {
//                            // called when response HTTP status is "4XX" (eg. 401, 403, 404)
//                            System.out.println("failure");
//                            System.out.println(statusCode);
//                        }
//                    });

                    System.out.println(username);
                }catch (JSONException e){
                    throw  new RuntimeException(e);
                }
            }
            @Override
            public void onFinish() {
                System.out.println("finished 001");
                Log.e("getUser", "  finish");

            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject errorResponse) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                System.out.println("finished failed 001");
                Log.e("getUser", "  onFailure");
            }
        });
    }

    @Override
    protected void onDestroy() {
        Intent intent = new Intent("com.cioc.libreerp.backendservice");
        intent.putExtra("yourvalue", "torestore");
        sendBroadcast(intent);
        Log.i("MAINACT", "onDestroy!");
        super.onDestroy();

    }
}
