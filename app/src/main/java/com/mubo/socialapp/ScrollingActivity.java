package com.mubo.socialapp;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.mubo.socialapp.api.ApiClass;
import com.mubo.socialapp.helpers.Ayarlar;
import com.mubo.socialapp.helpers.DividerItemDecoration;
import com.mubo.socialapp.helpers.statics;
import com.mubo.socialapp.main.ui.home.PostData;
import com.mubo.socialapp.main.ui.home.postAdaptor;
import com.mubo.socialapp.single_post.SinglePost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ScrollingActivity extends AppCompatActivity {
    private String TAG="ExCol";

    private enum State {
        EXPANDED,
        COLLAPSED,
        IDLE
    }
    RecyclerView rView;
    AppBarLayout appBarLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle("Omar");
        appBarLayout=findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            private State state;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset == 0) {
                    if (state != State.EXPANDED) {
                        Log.d(TAG,"Expanded");
                        toolbar.setVisibility(View.GONE);
                    }
                    state = State.EXPANDED;
                } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                    if (state != State.COLLAPSED) {
                        Log.d(TAG,"Collapsed");
                        toolbar.setVisibility(View.VISIBLE);
                    }
                    state = State.COLLAPSED;
                } else {
                    if (state != State.IDLE) {
                        Log.d(TAG,"Idle");
                    }
                    state = State.IDLE;
                }
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rView = findViewById(R.id.list);
        rView.setLayoutManager(layoutManager);
        rView.addItemDecoration(
                new DividerItemDecoration(getDrawable(R.drawable.divider),
                        false, false));
        Ayarlar ayarlar=new Ayarlar(this);
        id=ayarlar.get_pref_string("id");
        try {
            getPosts(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    String id=null;
    public void getPosts(boolean newData) throws JSONException {
        if(newData)
            statics.home_data.clear();
        JSONObject select=new JSONObject();
        JSONArray query_objects=new JSONArray();
        JSONArray fields=new JSONArray();
        JSONObject getByResult=new JSONObject();
        JSONArray gbr_query_objects=new JSONArray();
        JSONArray gbr_fields=new JSONArray();

        JSONObject q1=new JSONObject();
        q1.put("relationName","userRelations");
        JSONObject q2=new JSONObject();
        q2.put("to",id);
        query_objects.put(q1);
        query_objects.put(q2);
        fields.put("content.followed");
        select.put("query_objects",query_objects);
        select.put("operator","and");
        select.put("fields",fields);

        JSONObject gbr_q1=new JSONObject();
        JSONObject nested=new JSONObject();
        nested.put("content.user.collectionName","user");
        nested.put("content.user.id","$content.followed.content.user.id");
        gbr_q1.put("nested",nested);
        gbr_query_objects.put(gbr_q1);
        gbr_fields.put("collection_content");
        gbr_fields.put("content.postLike");
        gbr_fields.put("content.user");
        getByResult.put("query_objects",gbr_query_objects);
        getByResult.put("operator","or");
        getByResult.put("fields",gbr_fields);
        getByResult.put("from",0);
        getByResult.put("size",10);
        getByResult.put("sortBy","_id");
        getByResult.put("order","desc");
        select.put("getByResult",getByResult);
        JSONObject m=new JSONObject();
        m.put("select",select);
        ApiClass ac=new ApiClass();
        ac.selectRelation(m,null,hnd,null);
    }
    Handler hnd=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 1:
                    String sonuc=(String)msg.obj;
                    try {
                        JSONObject j=new JSONObject(sonuc);
                        bind(j);
                        Toast.makeText(ScrollingActivity.this, "OK", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
                case 2:
                    Toast.makeText(ScrollingActivity.this, "Hata", Toast.LENGTH_SHORT).show();
            }
        }
    };


    void bind(JSONObject j) throws JSONException {

        JSONArray result=j.getJSONArray("result");
        JSONArray values=result.getJSONObject(0).getJSONArray("values");
        for(int i=0;i<values.length();++i){
            JSONObject p=values.getJSONObject(i);
            JSONObject cc=p.getJSONObject("collection_content");
            String post_id=cc.getString("_id");
            JSONObject content=cc.getJSONObject("content");
            String postData=content.getString("post_data");
            String postType=content.getString("post_type");
            int commentSize=content.getInt("post_comment_size");
            int likeSize=content.getInt("post_like_size");
            long cDate=content.getLong("post_createdDate");
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String time = sdf.format(new Date(cDate));
            JSONObject v_content=p.getJSONObject("content");
            JSONArray pLikes=v_content.optJSONArray("postLike");
            JSONArray user=v_content.getJSONArray("user");
            String user_id=user.getJSONObject(0).getString("_id");
            JSONObject user_content=user.getJSONObject(0).getJSONObject("content");
            String username=user_content.getString("user_username");
            String person=user_content.getString("user_image");
            boolean selfLiked=false;
            String like_id="";
            //user bilgilerini al
            if(pLikes!=null) {
                for (int c = 0; c < pLikes.length(); ++c) {
                    JSONObject pLike_user = pLikes.optJSONObject(c);
                    if(pLike_user!=null) {
                        JSONArray jsonArray= pLike_user.optJSONArray("postLike_user");
                        if(jsonArray!=null) {
                            JSONObject us=jsonArray.optJSONObject(0);
                            String plike_userid=us!=null?us.optString("_id"):"";
                            if (plike_userid.equals(id)) {
                                like_id = pLike_user.optString("_id");
                                if(like_id!=null && !like_id.isEmpty())
                                    selfLiked = true;
                                break;
                            }
                        }
                    }
                }
            }
            PostData pd = new PostData(user_id,username,time,String.valueOf(likeSize),
                    String.valueOf(commentSize),selfLiked,person,"",postData,postType,post_id,like_id);
            statics.home_data.add(pd);

        }
        statics.home_adaptor=new postAdaptor(ScrollingActivity.this,statics.home_data);
        statics.PostAdaptorList[0]=statics.home_adaptor;
        rView.setAdapter(statics.home_adaptor);
    }
    PostData postData;
    int val=0;
}