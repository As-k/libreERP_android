package com.cioc.libreerp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by admin on 23/05/18.
 */

public class MultiUserListAdapter extends RecyclerView.Adapter<MultiUserListAdapter.ViewHolder> {

    private Context context;
    public static List<UserMeta> my_data;
    public static List<Boolean> isSelected = new ArrayList<>();
    AsyncHttpClient asyncHttpClient;
    Backend backend;
    public static ArrayList multiusers;


    public MultiUserListAdapter(Context context, List<UserMeta> my_data) {
        this.context = context;
        this.my_data = my_data;
    }

    @Override
    public MultiUserListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        backend = new Backend(context);
        multiusers = new ArrayList();
        asyncHttpClient = backend.getHTTPClient();
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_card, parent, false);
        return new MultiUserListAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MultiUserListAdapter.ViewHolder holder, final int position) {

        final UserMeta um = my_data.get(position);
        isSelected.add(position, false);
        isSelected.set(position, false);
//        Users users = new Users(context);
//        users.get(my_data.get(position).getPkUser() , new UserMetaHandler(){
//            @Override
//            public void onSuccess(UserMeta user){

        holder.userName.setText(um.getFirstName() + " " + um.getLastName());

        String imgUrl = um.getDisplayPictureLink();
        asyncHttpClient.get(imgUrl, new FileAsyncHttpResponseHandler(context) {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                asyncHttpClient.get(Backend.serverUrl + "/static/images/userIcon.png", new FileAsyncHttpResponseHandler(context) {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {

                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, File file) {
                        Bitmap pp = BitmapFactory.decodeFile(file.getAbsolutePath());
                        holder.userImage.setImageBitmap(pp);
                    }
                });
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                Bitmap pp = BitmapFactory.decodeFile(file.getAbsolutePath());
                holder.userImage.setImageBitmap(pp);
            }
        });
//            }
//            @Override
//            public void handleDP(Bitmap dp){
//
//                holder.userImage.setImageBitmap(dp);
//            }
//
//        });

//        if (count == 0) {
//            holder.user.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent();
//                    intent.setAction("com.cioc.libreERP.chatApp");
//                    intent.putExtra("with_id", (my_data.get(position).getPkUser()));
//                    intent.putExtra("name", my_data.get(position).getFirstName() + " " + my_data.get(position).getLastName());
//                    context.sendBroadcast(intent);
//                }
//            });
//        } else {

        HashMap hashMap = new HashMap();
        final boolean[] is = {false};
        holder.user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.llSelected.setVisibility(View.GONE);
                holder.selected.setVisibility(View.VISIBLE);
                if (isSelected.get(position)) {
                    isSelected.set(position, false);
                    holder.selected.setChecked(isSelected.get(position));
                    holder.selected.setVisibility(View.GONE);
                    holder.llSelected.setVisibility(View.VISIBLE);
//                    ((HashMap) multiusers.get(position)).clear();
                    multiusers.remove(position);
                } else {
                    isSelected.set(position, true);
                    holder.selected.setChecked(isSelected.get(position));
                    hashMap.put("with_id", (my_data.get(position).getPkUser()));
                    multiusers.add(hashMap);
                }
            }
        });


        holder.selected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!holder.selected.isChecked()) {
                    holder.llSelected.setVisibility(View.VISIBLE);
                    holder.selected.setVisibility(View.GONE);
                    isSelected.set(position, false);
//                    ((HashMap) multiusers.get(position)).clear();
                        multiusers.remove(position);
                } else {
                    isSelected.set(position, true);
                    holder.selected.setChecked(isSelected.get(position));
                    hashMap.put("with_id", (my_data.get(position).getPkUser()));
//                    hashMap.put("name",my_data.get(position).getFirstName() + " " + my_data.get(position).getLastName());
                    multiusers.add(hashMap);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return my_data.size();
    }

    public  class ViewHolder extends  RecyclerView.ViewHolder{
        TextView userName;
        ImageView userImage;
        LinearLayout user, llSelected;
        CheckBox selected;
        boolean llClick;

        public ViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_name);
            userImage = itemView.findViewById(R.id.user_image);
            user =  itemView.findViewById(R.id.user);
            llSelected =  itemView.findViewById(R.id.ll_checkbox);
            selected =  itemView.findViewById(R.id.user_select);
            llClick = user.isLongClickable();
        }
    }
    public void clearData() {
        my_data.clear();
        notifyDataSetChanged();
    }
}