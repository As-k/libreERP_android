package com.cioc.libreerp;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class NewChatActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView text;
    Backend backend;
    AsyncHttpClient httpClient;
    ArrayList<UserMeta> userMetas;
    UserListAdapter userListAdapter;
    GridLayoutManager gridLayoutManager;
    MenuItem search1;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_contacts, menu);
        search1 = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search1);
        search1.expandActionView();
        searchView.setIconified(false);
        searchView.setIconifiedByDefault(true);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                recyclerView.setVisibility(View.VISIBLE);
                query = query.toLowerCase();
                userMetas.clear();
                userListAdapter.clearData();
                httpClient.get(Backend.serverUrl + "/api/HR/users/?limit=10&search="+query, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        try {

                            JSONArray jsonArray = response.getJSONArray("results");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                try {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    UserMeta userMeta = new UserMeta(object);

                                    userMetas.add(userMeta);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Log.e("JSONObject", "Json parsing error: " + e.getMessage());
                                }
                            }

                            userListAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("JSONArray", "Json parsing error: " + e.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                    }
                });

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                text.setVisibility(View.GONE);
                if(newText.equals("")){
                    text.setVisibility(View.VISIBLE);
                    userMetas.clear();
                    userListAdapter.clearData();
                }
                return false;
            }
        });
        search1.setVisible(true);
        userMetas.clear();
        userListAdapter.clearData();
        return true;
    }

    public void search(SearchView searchView){
    }

    boolean is=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_chat);

        Bundle b = getIntent().getExtras();
        if (b!=null) {
            is = b.getBoolean("boolean");
        }
        text = (TextView) findViewById(R.id.text);
        recyclerView = (RecyclerView) findViewById(R.id.contactList);

        backend = new Backend(this);
        httpClient = backend.getHTTPClient();
        userMetas = new ArrayList<UserMeta>();

        userListAdapter = new UserListAdapter(this,userMetas);
        gridLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(userListAdapter);
        if(userMetas.size() != 0){
            text.setVisibility(View.GONE);
        }
        else
            text.setVisibility(View.VISIBLE);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        UserMeta userMeta = userMetas.get(position);
                        Intent intent = new Intent();
                        if (!is) {
//                            intent.putExtra("with_id", (userMetas.get(position).getPkUser()));
                            intent.putExtra("name", userMetas.get(position).getFirstName() + " " + userMetas.get(position).getLastName());
                            setResult(RESULT_OK, intent);
//                            Toast.makeText(NewChatActivity.this, "Hi" + position, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                })
        );

    }
}
