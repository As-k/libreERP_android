package com.cioc.libreerp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    Button logoutButton;
    SessionManager sessionManager;
    Backend backend;
    AsyncHttpClient httpclient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sessionManager = new SessionManager(this);

        getSupportActionBar().hide();

        backend = new Backend(this);

        httpclient = backend.getHTTPClient();

        httpclient.get(Backend.serverUrl + "/api/HR/users/?mode=mySelf&format=json", new JsonHttpResponseHandler() { //
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



        Session session = new Session();
        // Add all onJoin listeners
        session.addOnJoinListener(this::demonstrateSubscribe);

        // finally, provide everything to a Client and connect
        Client client = new Client(session, "ws://192.168.1.113:8080/ws", "default");
        CompletableFuture<ExitInfo> exitInfoCompletableFuture = client.connect();


    }

    public void demonstrateSubscribe(Session session, SessionDetails details) {
        // Subscribe to topic to receive its events.
        CompletableFuture<Subscription> subFuture = session.subscribe("service.chat.admin",
                this::onEvent);
        subFuture.whenComplete((subscription, throwable) -> {
            if (throwable == null) {
                // We have successfully subscribed.
                System.out.println("Subscribed to topic " + subscription.topic);
            } else {
                // Something went bad.
                throwable.printStackTrace();
            }
        });
    }

    private void onEvent(List<Object> args, Map<String, Object> kwargs, EventDetails details) {
        System.out.println(String.format("Got event: %s", args.get(0)));
    }
}
