package com.mubo.socialapp.api;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.google.android.gms.common.api.Api;
import com.mubo.socialapp.helpers.Ayarlar;
import com.mubo.socialapp.helpers.interfaces;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class TokenOp{
    Context cx;
    Ayarlar ayarlar;
    String token;
    ApiClass api;
    interfaces.checkToken ct;
    public TokenOp(Context cx, interfaces.checkToken ct){
        this.cx=cx;
        ayarlar=new Ayarlar(cx);
        this.token=ayarlar.get_pref_string("token");
        api=new ApiClass();
        this.ct=ct;
    }
    Handler verifyHnd=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 1:
                    try {
                        JSONObject j=new JSONObject((String)msg.obj);
                        if(j!=null){
                            try {
                                if(j.has("applicationId")) {
                                    ct.getToken(token);
                                }else{
                                    String cd=ayarlar.get_pref_string("client_data").trim();
                                    if(cd.isEmpty())
                                        create_client();
                                    else
                                        auth(new JSONObject(cd));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    break;
            }
        }
    };
    Handler hndClient=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 1:
                    JSONObject j= null;
                    try {
                        j = new JSONObject((String)msg.obj);
                        if(j!=null && j.has("client_id")){
                            ayarlar.set_pref_string("client_data",j.toString());
                            auth(j);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    break;
            }
        }
    };
    Handler authHnd=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 1:
                    JSONObject j= null;
                    try {
                        j=new JSONObject((String)msg.obj);
                        if(j!=null && j.has("token")){
                            try {
                                token=j.getString("token");
                                ayarlar.set_pref_string("token",j.getString("token"));
                                ayarlar.set_pref_long("token_time",new Date().getTime());
                                ct.getToken(token);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else{}
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    break;
            }
        }
    };
    public void verify() {
        if(this.token.trim().isEmpty())
            create_client();
        else
            new Thread(new Runnable() {
                @Override
                public void run() {
                    api.verify(null, verifyHnd,token);
                }
            }).start();
    }

    void create_client(){
        JSONObject j=new JSONObject();
        try {
            String id=ayarlar.get_pref_string("id");
            if(!id.isEmpty())
                j.put("client_id",id);
            j.put("application_id",ApiClass.applicationId);
            j.put("organization_id",ApiClass.organizationId);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    api.create_client(j,hndClient);
                }
            }).start();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    void create_guest_client(){
        JSONObject j=new JSONObject();
        try {
            j.put("application_id",ApiClass.applicationId);
            j.put("organization_id",ApiClass.organizationId);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    api.create_client(j,hndClient);
                }
            }).start();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    void auth(JSONObject j) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                api.auth(j, authHnd);
            }
        }).start();
    }
}
