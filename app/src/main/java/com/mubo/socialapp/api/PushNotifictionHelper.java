package com.mubo.socialapp.api;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.mubo.socialapp.R;
import com.mubo.socialapp.helpers.Ayarlar;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class PushNotifictionHelper {
    public final static String AUTH_KEY_FCM = "FCM KEY";
    public final static String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";
    static NotificationClass nc;
    static Activity act;
    public static void sendNotification(String userid, Activity act,NotificationClass nc){
        PushNotifictionHelper.nc=nc;
        PushNotifictionHelper.act=act;
        getUser(act,userid);
    }
    static String sendPushNotification(String deviceToken)
            throws IOException, JSONException {
        String result = "";
        URL url = new URL(API_URL_FCM);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setUseCaches(false);
        conn.setDoInput(true);
        conn.setDoOutput(true);

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "key=" + AUTH_KEY_FCM);
        conn.setRequestProperty("Content-Type", "application/json");

        JSONObject json = new JSONObject();

        json.put("to", deviceToken.trim());
        JSONObject info = new JSONObject();
        info.put("title", nc.getTitle()); // Notification title
        info.put("body", nc.getBody()); // Notification
        info.put("image", nc.getImage()); // Notification
        info.put("icon", nc.getIcon_id()); // Notification
        // body
        json.put("data", info);
        try {
            OutputStreamWriter wr = new OutputStreamWriter(
                    conn.getOutputStream());
            wr.write(json.toString());
            wr.flush();

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }
            result = "1";
        } catch (Exception e) {
            e.printStackTrace();
            result = "0";
        }
        System.out.println("GCM Notification is sent successfully");

        return result;

    }

    static void getUser(Activity act,String userid){
        JSONObject jo=new JSONObject();
        try {
            jo.put("_id",userid);
            ApiClass ac=new ApiClass();
            ac.getCollection(jo,null,hnd,act);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    static void getOwnUser(){
        Ayarlar ayarlar=new Ayarlar(act);
        String userid=ayarlar.get_pref_string("id");
        JSONObject jo=new JSONObject();
        try {
            jo.put("_id",userid);
            ApiClass ac=new ApiClass();
            ac.getCollection(jo,null,hnd_own,act);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    static Handler hnd=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 1:
                    String sonuc=(String)msg.obj;
                    try {
                        JSONObject jo = new JSONObject(sonuc);
                        String token=jo.getJSONObject("content").optString("user_devicetoken");
                        nc.setToken(token);
                        getOwnUser();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

            }
        }
    };
    static Handler hnd_own=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 1:
                    String sonuc=(String)msg.obj;
                    try {
                        JSONObject jo = new JSONObject(sonuc);
                        String username=jo.getJSONObject("content").optString("user_username");
                        String person=jo.getJSONObject("content").optString("user_image");
                        nc.setImage(person);
                        nc.setBody(nc.getBody().replace("$",username));
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    sendPushNotification(nc.getToken());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

            }
        }
    };
}
