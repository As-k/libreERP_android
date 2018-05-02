package com.cioc.libreerp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.ResponseHandlerInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

//import static com.cioc.libreerp.Backend.getHTTPClient;

public class MainActivity extends AppCompatActivity {

    Button logoutButton;
    SessionManager sessionManager;
    Backend backend;
    AsyncHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sessionManager = new SessionManager(this);

        getSupportActionBar().hide();

        backend = new Backend(this);
//        client = new AsyncHttpClient();

        client = backend.getHTTPClient(this);

        client.get(backend.serverUrl + "/api/HR/users/?mode=mySelf&format=json", new JsonHttpResponseHandler() { //
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.e("MainActivity","onSuccess");
                try {
                    JSONObject usrObj = response.getJSONObject(0);
                    String username = usrObj.getString("username");
                    String firstName = usrObj.getString("first_name");
                    Integer pk = usrObj.getInt("pk");
                    String lastName = usrObj.getString("last_name");



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
                startActivity(new Intent(MainActivity.this, FlashActivity.class));
                finish();
            }
        });


    }
}
