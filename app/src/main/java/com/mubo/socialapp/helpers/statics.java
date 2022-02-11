package com.mubo.socialapp.helpers;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mubo.socialapp.R;
import com.mubo.socialapp.api.ApiClass;
import com.mubo.socialapp.main.ui.home.PostData;
import com.mubo.socialapp.main.ui.home.postAdaptor;
import com.mubo.socialapp.main.ui.settings.FollowData;
import com.mubo.socialapp.main.ui.settings.followAdaptor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class statics {
    public static ArrayList<PostData> home_data=new ArrayList<>();
    public static ArrayList<PostData> posts_data=new ArrayList<>();
    public static ArrayList<PostData> postLikes_data=new ArrayList<>();

    public static postAdaptor home_adaptor=null;
    public static postAdaptor posts_adaptor=null;
    public static postAdaptor postLikes_adaptor=null;

    public static Object[] PostDataList={home_data,posts_data,postLikes_data};
    public static postAdaptor[] PostAdaptorList={home_adaptor,posts_adaptor,postLikes_adaptor};


    public static final int updatedData=1;
    public static List<Object> updatedObjects=new ArrayList<>();
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    public static float pixelsToSp(float px,Activity act)
    {
        float scaledDensity = act.getResources().getDisplayMetrics().scaledDensity;
        float rt=px/scaledDensity;
        return rt;
    }
    public static float dpFromPx(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }
    static final long MILLION = 1000000L;
    static final long THOUSAND = 1000L;
    static final long BILLION = 1000000000L;
    static final long TRILLION = 1000000000000L;

    public static String setSize(String size) {
        long x=Long.parseLong(size);
        return x < THOUSAND ?  String.valueOf(x) :
                x < MILLION ?  x / THOUSAND + "B" :
                        x < BILLION ? x / MILLION + "M" :
                                x / BILLION + "Mi";
    }
    public static void addLike(String id, String uid, Handler hnd, Activity act) throws JSONException {
        JSONArray relations=new JSONArray();
        JSONArray contents=new JSONArray();
        JSONObject rel=new JSONObject();
        rel.put("relationName","postRelations");
        rel.put("id",id);
        relations.put(rel);
        JSONObject contentdata=new JSONObject();
        contentdata.put("collectionName","postLike");
        JSONObject content =new JSONObject();
        content.put("postLike_data",true);
        JSONArray innerData=new JSONArray();
        JSONObject idata=new JSONObject();
        idata.put("relationName","postRelations");
        idata.put("id",uid);
        JSONArray fields=new JSONArray();
        fields.put("user_username");
        fields.put("user_image");
        idata.put("fields",fields);
        innerData.put(idata);
        contentdata.put("content",content);
        contentdata.put("innerData",innerData);
        contents.put(contentdata);
        JSONObject send_data=new JSONObject();
        send_data.put("relations",relations);
        send_data.put("contents",contents);
        ApiClass ac=new ApiClass();
        ac.addRelation(send_data ,null,hnd,act);

    }
    public static void removeLike(String id, Handler hnd, Activity act) throws JSONException {
        JSONObject j=new JSONObject();
        j.put("id",id);
        ApiClass ac=new ApiClass();
        ac.deleteCollection(j,null,hnd,act);
    }
    public static void removeFollow(String id, Handler hnd, Activity act) throws JSONException {
        JSONObject j=new JSONObject();
        j.put("id",id);
        ApiClass ac=new ApiClass();
        ac.deleteCollection(j,null,hnd,act);
    }
    public static void increase_like(String id,String field,int val,Handler hnd,Activity act) throws JSONException {
        JSONArray fields = new JSONArray();
        JSONObject f=new JSONObject();
        f.put("field",field);
        f.put("increase",val);
        f.put("type","counter");
        fields.put(f);
        JSONObject so=new JSONObject();
        so.put("id",id);
        so.put("fields",fields);
        ApiClass ac=new ApiClass();
        ac.updateCollection(so ,null,hnd,act);

    }
    public static void show_ok(Activity act){
        Dialog d=new Dialog(act);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setCancelable(false);
        d.setContentView(R.layout.okay_lay);
        d.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    d.dismiss();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public static void updateOthers(PostData pd){
        for(int i=0;i<statics.PostDataList.length;++i){
            ArrayList<PostData> pds=(ArrayList<PostData>)statics.PostDataList[i];
            for(int c=0;c<pds.size();++c){
                if(pds.get(c).getPost_id().equals(pd.getPost_id())){
                    pds.set(c,pd);
                    postAdaptor pa= statics.PostAdaptorList[i];
                    pa.notifyItemChanged(c);
                }
            }
        }
    }
    static void getUser(String userid,Activity act){
        statics.act=act;
        JSONObject jo=new JSONObject();
        try {
            jo.put("_id",userid);
            JSONArray ja =new JSONArray();
            ja.put("user_pass");
            jo.put("remove_fields",ja);
            ApiClass ac=new ApiClass();
            ac.getCollection(jo,null,token_hnd,act);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    static Handler token_hnd=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 1:
                    try {
                        String sonuc = (String) msg.obj;
                        JSONObject jo = new JSONObject(sonuc);
                        String tk = jo.getJSONObject("content").optString("user_devicetoken");
                        String id=jo.getString("id");
                        String token = FirebaseInstanceId.getInstance().getToken();
                        FirebaseMessaging.getInstance().subscribeToTopic("android").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.i("Topicubsciption","Success");
                            }
                        });
                        if(!tk.trim().equals(token)) {
                            JSONArray fields = new JSONArray();
                            JSONObject f = new JSONObject();
                            f.put("field", "user_devicetoken");
                            f.put("value", token);
                            fields.put(f);
                            JSONObject so = new JSONObject();
                            so.put("id", id);
                            so.put("fields", fields);
                            ApiClass ac = new ApiClass();
                            ac.updateCollection(so, null, tk_handler, act);
                        }
                    }catch (Exception ex){}
                    break;
            }
        }
    };
    static Activity act;
    public static void updateToken(Activity act,String id) throws JSONException {
        getUser(id,act);
    }
    static Handler tk_handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
        }
    };
}
