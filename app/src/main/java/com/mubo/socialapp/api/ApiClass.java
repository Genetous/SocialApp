package com.mubo.socialapp.api;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.android.gms.common.api.Api;
import com.mubo.socialapp.helpers.Ayarlar;
import com.mubo.socialapp.helpers.interfaces;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Random;

public class ApiClass implements interfaces.checkToken {
    public static String applicationId="xxxxx-xxx-xxxxx-xxxx";
    public static String organizationId="xxxxxxx";
    Handler handler;
    interfaces.checkToken ct;
    static JSONObject j;
    static View progressParent;
    static Handler hnd;
    static Activity act;
    static int functionPosition=0;
    static long token_max=3600000;
    @Override
    public void getToken(String token) {
        switch (ApiClass.functionPosition) {
            case 1:
                addUniqueCollection(ApiClass.j, ApiClass.progressParent, ApiClass.hnd, ApiClass.act);
                break;
            case 2:
                addCollection(ApiClass.j, ApiClass.progressParent, ApiClass.hnd, ApiClass.act);
                break;
            case 3:
                updateCollection(ApiClass.j, ApiClass.progressParent, ApiClass.hnd, ApiClass.act);
                break;
            case 4:
                deleteCollection(ApiClass.j, ApiClass.progressParent, ApiClass.hnd, ApiClass.act);
                break;
            case 5:
                getCollection(ApiClass.j, ApiClass.progressParent, ApiClass.hnd, ApiClass.act);
                break;
            case 6:
                selectRelation(ApiClass.j, ApiClass.progressParent, ApiClass.hnd, ApiClass.act);
                break;
            case 7:
                search(ApiClass.j, ApiClass.progressParent, ApiClass.hnd, ApiClass.act);
                break;
            case 8:
                addRelation(ApiClass.j, ApiClass.progressParent, ApiClass.hnd, ApiClass.act);
                break;
            case 9:
                getRelations(ApiClass.j, ApiClass.progressParent, ApiClass.hnd, ApiClass.act);
                break;
        }
        Log.i("Token",token);
    }
    //It is used to perform collection records which contains unique fields.
    //unique fields must be specified inside the uniqueFields array.
    public void addUniqueCollection(JSONObject j, View progressParent, Handler hnd, Activity act){

        ProgressBar pb = null;
        ct=this;
        if(progressParent!=null)
            pb=addProgressBar(progressParent);
        handler=hnd;
        ProgressBar finalPb = pb;
        Ayarlar ayarlar=new Ayarlar(act);
        String token=ayarlar.get_pref_string("token");
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sonuc = HttpPostJson.SendHttpPost(UrlList.create_unique_collection, j, token);
                if (progressParent != null)
                    removeProgressBar(progressParent, finalPb, act);
                if (sonuc.isEmpty()) {
                    hnd.obtainMessage(2).sendToTarget();
                } else if (sonuc.trim().toLowerCase().equals("unauthorized")) {
                    long token_time=ayarlar.get_pref_long("token_time");
                    long now=new Date().getTime();
                    if(!token.isEmpty() && (now-token_time)<token_max)
                        hnd.obtainMessage(2).sendToTarget();
                    else {
                        ApiClass.j = j;
                        ApiClass.act = act;
                        ApiClass.hnd = hnd;
                        ApiClass.progressParent = progressParent;
                        ApiClass.functionPosition = 1;
                        hnd.post(new Runnable() {
                            @Override
                            public void run() {
                                TokenOp to = new TokenOp(act, ct);
                                to.verify();
                            }
                        });
                    }
                } else {
                    hnd.obtainMessage(1, sonuc).sendToTarget();
                }

            }
        }).start();

    }


    //It is used to perform collection creation.
    public void addCollection(JSONObject j, View progressParent, Handler hnd, Activity act){
        ProgressBar pb = null;
        ct=this;
        if(progressParent!=null)
            pb=addProgressBar(progressParent);
        handler=hnd;
        ProgressBar finalPb = pb;
        Ayarlar ayarlar=new Ayarlar(act);
        String token = ayarlar.get_pref_string("token");
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sonuc=HttpPostJson.SendHttpPost(UrlList.add_collection,j,token);
                if(progressParent!=null)
                    removeProgressBar(progressParent, finalPb,act);
                if (sonuc.isEmpty()) {
                    hnd.obtainMessage(2).sendToTarget();
                }  else if (sonuc.trim().toLowerCase().equals("unauthorized")) {
                    long token_time=ayarlar.get_pref_long("token_time");
                    long now=new Date().getTime();
                    if(!token.isEmpty() && (now-token_time)<token_max)
                        hnd.obtainMessage(2).sendToTarget();
                    else {
                        ApiClass.j = j;
                        ApiClass.act = act;
                        ApiClass.hnd = hnd;
                        ApiClass.progressParent = progressParent;
                        ApiClass.functionPosition = 2;
                        hnd.post(new Runnable() {
                            @Override
                            public void run() {
                                TokenOp to = new TokenOp(act, ct);
                                to.verify();
                            }
                        });
                    }
                } else {
                    hnd.obtainMessage(1, sonuc).sendToTarget();
                }

            }
        }).start();

    }

    //It is used to perform collection update.
    public void updateCollection(JSONObject j, View progressParent, Handler hnd, Activity act){
        ct=this;
        ProgressBar pb = null;
        if(progressParent!=null)
            pb=addProgressBar(progressParent);
        handler=new Handler();
        ProgressBar finalPb = pb;
        Ayarlar ayarlar=new Ayarlar(act);
        String token = ayarlar.get_pref_string("token");
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sonuc=HttpPostJson.SendHttpPost(UrlList.update_collection,j,token);
                if(progressParent!=null)
                    removeProgressBar(progressParent, finalPb,act);
                if(sonuc.isEmpty()){
                    hnd.obtainMessage(2).sendToTarget();
                }else if (sonuc.trim().toLowerCase().equals("unauthorized")) {
                    long token_time=ayarlar.get_pref_long("token_time");
                    long now=new Date().getTime();
                    if(!token.isEmpty() && (now-token_time)<token_max)
                        hnd.obtainMessage(2).sendToTarget();
                    else {
                        ApiClass.j = j;
                        ApiClass.act = act;
                        ApiClass.hnd = hnd;
                        ApiClass.progressParent = progressParent;
                        ApiClass.functionPosition = 3;
                        hnd.post(new Runnable() {
                            @Override
                            public void run() {
                                TokenOp to = new TokenOp(act, ct);
                                to.verify();
                            }
                        });
                    }
                }else{
                    hnd.obtainMessage(1,sonuc).sendToTarget();
                }

            }
        }).start();

    }

    //It is used to perform collection deletion.
    public void deleteCollection(JSONObject j, View progressParent, Handler hnd, Activity act){
        ct=this;
        ProgressBar pb = null;
        if(progressParent!=null)
            pb=addProgressBar(progressParent);
        handler=new Handler();
        ProgressBar finalPb = pb;
        Ayarlar ayarlar=new Ayarlar(act);
        String token = ayarlar.get_pref_string("token");
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sonuc=HttpPostJson.SendHttpPost(UrlList.delete_collection,j,token);
                if(progressParent!=null)
                    removeProgressBar(progressParent, finalPb,act);
                if(sonuc.isEmpty()){
                    hnd.obtainMessage(2).sendToTarget();
                }else if (sonuc.trim().toLowerCase().equals("unauthorized")) {
                    long token_time=ayarlar.get_pref_long("token_time");
                    long now=new Date().getTime();
                    if(!token.isEmpty() && (now-token_time)<token_max)
                        hnd.obtainMessage(2).sendToTarget();
                    else {
                        ApiClass.j = j;
                        ApiClass.act = act;
                        ApiClass.hnd = hnd;
                        ApiClass.progressParent = progressParent;
                        ApiClass.functionPosition = 4;
                        hnd.post(new Runnable() {
                            @Override
                            public void run() {
                                TokenOp to = new TokenOp(act, ct);
                                to.verify();
                            }
                        });
                    }
                }else{
                    hnd.obtainMessage(1,sonuc).sendToTarget();
                }

            }
        }).start();

    }

    //It is used to perform collection finding and filtering operations.
    public void getCollection(JSONObject j, View progressParent, Handler hnd, Activity act){
        ct=this;
        ProgressBar pb = null;
        if(progressParent!=null)
            pb=addProgressBar(progressParent);
        handler=new Handler();
        ProgressBar finalPb = pb;
        Ayarlar ayarlar=new Ayarlar(act);
        String token = ayarlar.get_pref_string("token");
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sonuc=HttpPostJson.SendHttpPost(UrlList.get_collection,j,token);
                if(progressParent!=null)
                    removeProgressBar(progressParent, finalPb,act);
                if(sonuc.isEmpty()){
                    hnd.obtainMessage(2).sendToTarget();
                }else if (sonuc.trim().toLowerCase().equals("unauthorized")) {
                    long token_time=ayarlar.get_pref_long("token_time");
                    long now=new Date().getTime();
                    if(!token.isEmpty() && (now-token_time)<token_max)
                        hnd.obtainMessage(2).sendToTarget();
                    else {
                        ApiClass.j = j;
                        ApiClass.act = act;
                        ApiClass.hnd = hnd;
                        ApiClass.progressParent = progressParent;
                        ApiClass.functionPosition = 5;
                        hnd.post(new Runnable() {
                            @Override
                            public void run() {
                                TokenOp to = new TokenOp(act, ct);
                                to.verify();
                            }
                        });
                    }
                }else{
                    hnd.obtainMessage(1,sonuc).sendToTarget();
                }

            }
        }).start();

    }

    // it is used to perform relation finding and filtering operations on search engine.
    public void selectRelation(JSONObject j, View progressParent, Handler hnd, Activity act){
        ct=this;
        ProgressBar pb = null;
        if(progressParent!=null)
            pb=addProgressBar(progressParent);
        handler=new Handler();
        ProgressBar finalPb = pb;
        Ayarlar ayarlar=new Ayarlar(act);
        String token = ayarlar.get_pref_string("token");
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sonuc=HttpPostJson.SendHttpPost(UrlList.select_relation,j,token);
                if(progressParent!=null)
                    removeProgressBar(progressParent, finalPb,act);
                if(sonuc.isEmpty()){
                    hnd.obtainMessage(2).sendToTarget();
                }else if (sonuc.trim().toLowerCase().equals("unauthorized")) {
                    long token_time=ayarlar.get_pref_long("token_time");
                    long now=new Date().getTime();
                    if(!token.isEmpty() && (now-token_time)<token_max)
                        hnd.obtainMessage(2).sendToTarget();
                    else {
                        ApiClass.j = j;
                        ApiClass.act = act;
                        ApiClass.hnd = hnd;
                        ApiClass.progressParent = progressParent;
                        ApiClass.functionPosition = 6;
                        hnd.post(new Runnable() {
                            @Override
                            public void run() {
                                TokenOp to = new TokenOp(act, ct);
                                to.verify();
                            }
                        });
                    }
                }else{
                    hnd.obtainMessage(1,sonuc).sendToTarget();
                }

            }
        }).start();

    }

    // it is used to perform search operations on db.
    public void search(JSONObject j, View progressParent, Handler hnd, Activity act){
        ct=this;
        ProgressBar pb = null;
        if(progressParent!=null)
            pb=addProgressBar(progressParent);
        handler=new Handler();
        ProgressBar finalPb = pb;
        Ayarlar ayarlar=new Ayarlar(act);
        String token = ayarlar.get_pref_string("token");
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sonuc=HttpPostJson.SendHttpPost(UrlList.search,j,token);
                if(progressParent!=null)
                    removeProgressBar(progressParent, finalPb,act);
                if(sonuc.isEmpty()){
                    hnd.obtainMessage(2).sendToTarget();
                }else if (sonuc.trim().toLowerCase().equals("unauthorized")) {
                    long token_time=ayarlar.get_pref_long("token_time");
                    long now=new Date().getTime();
                    if(!token.isEmpty() && (now-token_time)<token_max)
                        hnd.obtainMessage(2).sendToTarget();
                    else {
                        ApiClass.j = j;
                        ApiClass.act = act;
                        ApiClass.hnd = hnd;
                        ApiClass.progressParent = progressParent;
                        ApiClass.functionPosition = 7;
                        hnd.post(new Runnable() {
                            @Override
                            public void run() {
                                TokenOp to = new TokenOp(act, ct);
                                to.verify();
                            }
                        });
                    }
                }else{
                    hnd.obtainMessage(1,sonuc).sendToTarget();
                }

            }
        }).start();

    }

    // it is used to perform relation creation.
    public void addRelation(JSONObject j, View progressParent, Handler hnd, Activity act){
        ct=this;
        ProgressBar pb = null;
        if(progressParent!=null)
            pb=addProgressBar(progressParent);
        handler=new Handler();
        ProgressBar finalPb = pb;
        Ayarlar ayarlar=new Ayarlar(act);
        String token = ayarlar.get_pref_string("token");
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sonuc=HttpPostJson.SendHttpPost(UrlList.add_relation,j,token);
                if(progressParent!=null)
                    removeProgressBar(progressParent, finalPb,act);
                if(sonuc.isEmpty()){
                    hnd.obtainMessage(2).sendToTarget();
                }else if (sonuc.trim().toLowerCase().equals("unauthorized")) {
                    long token_time=ayarlar.get_pref_long("token_time");
                    long now=new Date().getTime();
                    if(!token.isEmpty() && (now-token_time)<token_max)
                        hnd.obtainMessage(2).sendToTarget();
                    else {
                        ApiClass.j = j;
                        ApiClass.act = act;
                        ApiClass.hnd = hnd;
                        ApiClass.progressParent = progressParent;
                        ApiClass.functionPosition = 8;
                        hnd.post(new Runnable() {
                            @Override
                            public void run() {
                                TokenOp to = new TokenOp(act, ct);
                                to.verify();
                            }
                        });
                    }
                }else{
                    hnd.obtainMessage(1,sonuc).sendToTarget();
                }

            }
        }).start();

    }

    // it is used to perform relation finding and filtering operations on db.
    public void getRelations(JSONObject j, View progressParent, Handler hnd, Activity act){
        ct=this;
        ProgressBar pb = null;
        if(progressParent!=null)
            pb=addProgressBar(progressParent);
        handler=new Handler();
        ProgressBar finalPb = pb;
        Ayarlar ayarlar=new Ayarlar(act);
        String token = ayarlar.get_pref_string("token");
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sonuc=HttpPostJson.SendHttpPost(UrlList.get_relations,j,token);
                if(progressParent!=null)
                    removeProgressBar(progressParent, finalPb,act);
                if(sonuc.isEmpty()){
                    hnd.obtainMessage(2).sendToTarget();
                }else if (sonuc.trim().toLowerCase().equals("unauthorized")) {
                    long token_time=ayarlar.get_pref_long("token_time");
                    long now=new Date().getTime();
                    if(!token.isEmpty() && (now-token_time)<token_max)
                        hnd.obtainMessage(2).sendToTarget();
                    else {
                        ApiClass.j = j;
                        ApiClass.act = act;
                        ApiClass.hnd = hnd;
                        ApiClass.progressParent = progressParent;
                        ApiClass.functionPosition = 9;
                        hnd.post(new Runnable() {
                            @Override
                            public void run() {
                                TokenOp to = new TokenOp(act, ct);
                                to.verify();
                            }
                        });
                    }
                }else{
                    hnd.obtainMessage(1,sonuc).sendToTarget();
                }

            }
        }).start();

    }
    ProgressBar addProgressBar(View v){
        Random rnd=new Random();
        int progressBarId = rnd.nextInt();
        ProgressBar pb=new ProgressBar(v.getContext(),null, android.R.attr.progressBarStyleSmall);
        pb.setProgressTintList(ColorStateList.valueOf(Color.WHITE));
        ViewGroup vg=(ViewGroup)v;
        if(vg instanceof FrameLayout){
            FrameLayout.LayoutParams f=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            f.gravity = Gravity.CENTER;
            pb.setLayoutParams(f);
            vg.addView(pb);
        }
        return pb;
    }
    void removeProgressBar(View v,ProgressBar pb,Activity act){
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ViewGroup vg=(ViewGroup)v;
                vg.removeView(pb);
            }
        });

    }

    // it is used to perform token verification.
    public void verify(JSONObject data, Handler hnd,String token) {
        String sonuc = HttpPostJson.SendHttpPost(UrlList.token_verify, data, token);
        if (sonuc.isEmpty()) {
            hnd.obtainMessage(2).sendToTarget();
        } else {
            hnd.obtainMessage(1, sonuc).sendToTarget();
        }

    }
    public void create_client(JSONObject data, Handler hnd){
        String sonuc=HttpPostJson.SendHttpPost(UrlList.token_client,data,"");
        if (sonuc.isEmpty()) {
            hnd.obtainMessage(2).sendToTarget();
        } else {
            hnd.obtainMessage(1, sonuc).sendToTarget();
        }
    }
    public void auth(JSONObject data, Handler hnd){
        String sonuc=HttpPostJson.SendHttpPost(UrlList.token_auth,data,"");
        if(!sonuc.isEmpty()){
            if (sonuc.isEmpty()) {
                hnd.obtainMessage(2).sendToTarget();
            } else {
                hnd.obtainMessage(1, sonuc).sendToTarget();
            }
        }
    }


}
