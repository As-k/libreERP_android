package com.cioc.libreerp;

import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MultiChatActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView text;
    Backend backend;
    AsyncHttpClient httpClient;
    ArrayList<UserMeta> userMetas;
    MultiUserListAdapter multiUserListAdapter;
    GridLayoutManager gridLayoutManager;
    MenuItem search1,sendALL;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_multi_contacts, menu);
        search1 = menu.findItem(R.id.multiple_search);
        sendALL = menu.findItem(R.id.send_all);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search1);
        search1.expandActionView();
        searchView.setIconified(false);
        searchView.setIconifiedByDefault(true);

        searchView.setPadding(0,0,20,0);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                recyclerView.setVisibility(View.VISIBLE);
                query = query.toLowerCase();
                userMetas.clear();
                multiUserListAdapter.clearData();
                httpClient.get(Backend.serverUrl + "/api/HR/users/?limit=10&search=" + query, new JsonHttpResponseHandler() {
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

                            multiUserListAdapter.notifyDataSetChanged();

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
                if (newText.equals("")) {
                    text.setVisibility(View.VISIBLE);
                    userMetas.clear();
                    multiUserListAdapter.clearData();
                }
                return false;
            }
        });
        search1.setVisible(true);
        userMetas.clear();
        multiUserListAdapter.clearData();

        sendALL.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                sendContacts();
                return true;
            }
        });


        return true;
    }

    public void search(SearchView searchView) {
    }

    public void sendContacts() {
        Intent intent = new Intent();
        ArrayList<HashMap> contents = new ArrayList();
        if (!is) {
            List<UserMeta> multi = MultiUserListAdapter.my_data;
//            intent.putExtra("arrayList",multi);
            int count=0;
            for (int i=0; i<multi.size(); i++) {
//                Toast.makeText(this, "" + multi.size(), Toast.LENGTH_SHORT).show();
                if (MultiUserListAdapter.isSelected.get(i) == true){
                    HashMap mp = new HashMap();
                    UserMeta um = (UserMeta) multi.get(i);
                    mp.put("pkUser",um.getPkUser());
                    mp.put("firstName",um.getFirstName());
                    mp.put("lastName",um.getLastName());
                    contents.add(mp);
                }
//                HashMap map = (HashMap) multi.get(i);
//                int pk = (int) map.get("with_id");
//                intent.putExtra("with_id", pk);
//                Log.e("HashMap=--===>>>>","with_id "+pk);
            }
            intent.putExtra("multi_contacts",contents);
//            intent.putExtra("multi_contacts_size",contents.size());
            setResult(RESULT_OK, intent);
            finish();
//                data.getExtras().getInt("with_pk");
//                data.getExtras().getIntegerArrayList("arrayList"); data.getExtras().getInt("arrayListSize")
        }
    }

    boolean is = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_chat);

        Bundle b = getIntent().getExtras();
        if (b!=null) {
            is = b.getBoolean("boolean");
        }


        text = (TextView) findViewById(R.id.multi_text);
        recyclerView = (RecyclerView) findViewById(R.id.multi_contact_list);

        backend = new Backend(this);
        httpClient = backend.getHTTPClient();
        userMetas = new ArrayList<UserMeta>();

        multiUserListAdapter = new MultiUserListAdapter(this, userMetas);
        gridLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(multiUserListAdapter);
        if (userMetas.size() != 0) {
            text.setVisibility(View.GONE);
        } else
            text.setVisibility(View.VISIBLE);

//        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(View view, int position) {
//                        UserMeta userMeta = userMetas.get(position);
//                        Intent intent = new Intent();
////                        intent.putExtra("with_id", (userMeta.getPkUser()));
////                        intent.putExtra("name", userMeta.getFirstName() + " " + userMeta.getLastName());
//                        intent.putExtra("msg", "i am hear");
//                        setResult(RESULT_OK, intent);
//                    }
//                })
//        );

    }
}