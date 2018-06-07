package com.cioc.libreerp;

import android.content.Context;
import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by cioc on 10/6/17.
 */

public class UserMeta {

    private int pk;
    private String Username;
    private String FirstName;
    private String LastName;
    private String Mobile;
    private int Designation;
    private int Social;
    private String ProfilePictureLink;
    private Bitmap ProfilePicture;
    JSONObject object;


    public UserMeta(JSONObject object) throws JSONException {
        this.object = object;

        this.pk = object.getInt("pk");
        this.FirstName = object.getString("first_name");
        this.LastName = object.getString("last_name");

        JSONObject designation = object.getJSONObject("designation");

        JSONObject profile = object.getJSONObject("profile");
        String img = profile.getString("displayPicture");
        if (img.equals("null")){
            this.ProfilePictureLink = Backend.serverUrl+"/static/images/userIcon.png";
        } else
            this.ProfilePictureLink = img;
        this.Mobile = profile.getString("mobile");
    }

    private Bitmap getProfilePicture(){
        return ProfilePicture;
    };

    public String getFirstName() {
        return FirstName;
    }


    public int getDesignation() {
        return Designation;
    }

    public int getPkUser() {
        return pk;
    }

    public int getSocial() {
        return Social;
    }


    public String getLastName() {
        return LastName;
    }

    public String getUsername() {
        return Username;
    }

    public String getDisplayPictureLink(){
        return ProfilePictureLink;
    }

    public void setProfilePictureLink(String link) {
        ProfilePictureLink = link;
    }

    public void setDesignation(int designation) {
        Designation = designation;
    }


    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public void setPkUsers(int pkUsers) {
        pk = pkUsers;
    }

    public void setSocial(int social) {
        Social = social;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public UserMeta(int pk){
        this.pk = pk;
    }

    public JSONObject getObject() {
        return object;
    }

    public void setObject(JSONObject object) {
        this.object = object;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public void saveDPOnSD(Context context, Bitmap pp, String dpFileName){

        File DPFolder = new File(context.getFilesDir() , "DPs");
        if (!DPFolder.exists()){
            DPFolder.mkdir();
        }

        File dpFile = new File(DPFolder, dpFileName);

        try{
            FileOutputStream fOut = new FileOutputStream(dpFile);
            pp.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}