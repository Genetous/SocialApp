package com.mubo.socialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mubo.socialapp.api.ApiClass;
import com.mubo.socialapp.helpers.Ayarlar;
import com.mubo.socialapp.helpers.CircularTransform;
import com.mubo.socialapp.helpers.statics;
import com.mubo.socialapp.main.ui.settings.FollowersFragment;
import com.mubo.socialapp.main.ui.settings.FollowingFragment;
import com.mubo.socialapp.main.ui.settings.PostsFragment;
import com.mubo.socialapp.main.ui.settings.ProfileLikesFragment;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UserProfile extends AppCompatActivity {
    LinearLayout post_lin,like_lin,following_lin,followers_lin;
    RelativeLayout relTab;
    FragmentManager fragmentManager;
    LinearLayout.LayoutParams p;
    String id=null;
    TextView name;
    ImageView person;
    Activity act;
    Context cx;
    String userid=null;
    RelativeLayout back_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        act=this;
        cx=this;
        Ayarlar ayarlar=new Ayarlar(cx);
        id=ayarlar.get_pref_string("id");
        userid=getIntent().getStringExtra("userid");
        back_button=findViewById(R.id.back);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                act.finish();
            }
        });
        name = findViewById(R.id.name);
        person = findViewById(R.id.person);
        post_lin=findViewById(R.id.post_lin);
        like_lin=findViewById(R.id.like_lin);
        following_lin=findViewById(R.id.following_lin);
        followers_lin=findViewById(R.id.followers_lin);
        post_lin.setOnTouchListener(tl);
        like_lin.setOnTouchListener(tl);
        following_lin.setOnTouchListener(tl);
        followers_lin.setOnTouchListener(tl);
        relTab=findViewById(R.id.bottom_rel);
        fragmentManager = getSupportFragmentManager();
        PostsFragment f= PostsFragment.newInstance(userid);
        fragmentManager.beginTransaction()
                .replace(R.id.frag_frag,f, null)
                .setReorderingAllowed(true)
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .commit();

        p= (LinearLayout.LayoutParams) relTab.getLayoutParams();
        post_lin.postDelayed(new Runnable() {
            @Override
            public void run() {
                p.setMargins(20,0,0,0);
                p.width = (int) (post_lin.getWidth()*0.5);
                relTab.setLayoutParams(p);
            }
        },100);
        if(!userid.isEmpty())
            getUser();
    }
    void getUser(){
        JSONObject jo=new JSONObject();
        try {
            jo.put("_id",userid);
            JSONArray ja =new JSONArray();
            ja.put("user_pass");
            jo.put("remove_fields",ja);
            ApiClass ac=new ApiClass();
            ac.getCollection(jo,null,hnd,act);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    Handler hnd=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 1:
                    String sonuc=(String)msg.obj;
                    try {
                        JSONObject jo = new JSONObject(sonuc);
                        String p_url=jo.getJSONObject("content").optString("user_image");
                        if(p_url!=null && !p_url.trim().isEmpty()){
                            Picasso.get().load(p_url).transform(new CircularTransform()).resize(150,150).into(person);
                        }else{
                            person.setImageDrawable(cx.getDrawable(R.mipmap.no_image));
                        }
                        name.setText(jo.getJSONObject("content").getString("user_username"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(cx, "OK", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(cx, "Hata", Toast.LENGTH_SHORT).show();
            }
        }
    };
    int selected_lin=R.id.post_lin;
    int last_index=0;
    TextView tw;
    View.OnTouchListener tl=new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                if(view.getId() == R.id.post_lin) {
                    tw=view.findViewById(R.id.posts);
                } else if(view.getId() == R.id.like_lin) {
                    tw=view.findViewById(R.id.likes);
                }else if(view.getId() == R.id.following_lin) {
                    tw=view.findViewById(R.id.following);
                }else if(view.getId() == R.id.followers_lin) {
                    tw=view.findViewById(R.id.followers);
                }
                selected_lin= view.getId();
                tw.setTextColor(act.getColor(R.color.white));
            }else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                TextView t;
                RelativeLayout rt;
                if(selected_lin == R.id.post_lin){
                    ((TextView)findViewById(R.id.likes)).setTextColor(act.getColor(R.color.white_30));;
                    ((TextView)findViewById(R.id.following)).setTextColor(act.getColor(R.color.white_30));;
                    ((TextView)findViewById(R.id.followers)).setTextColor(act.getColor(R.color.white_30));;
                    ((TextView)findViewById(R.id.likes)).setTextSize(statics.pixelsToSp(getResources().getDimension(R.dimen.tab_profile_text_low),act));
                    ((TextView)findViewById(R.id.following)).setTextSize(statics.pixelsToSp(getResources().getDimension(R.dimen.tab_profile_text_low),act));
                    ((TextView)findViewById(R.id.followers)).setTextSize(statics.pixelsToSp(getResources().getDimension(R.dimen.tab_profile_text_low),act));
                    PostsFragment f= PostsFragment.newInstance(userid);
                    if(last_index!=0)
                        if(last_index > 0) {
                            fragmentManager.beginTransaction()
                                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                                    .replace(R.id.frag_frag, f, null)
                                    .commit();
                        }else{
                            fragmentManager.beginTransaction()
                                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                                    .replace(R.id.frag_frag, f, null)
                                    .commit();
                        }
                    last_index=0;
                }else if(selected_lin == R.id.like_lin){
                    ((TextView)findViewById(R.id.posts)).setTextColor(act.getColor(R.color.white_30));;
                    ((TextView)findViewById(R.id.following)).setTextColor(act.getColor(R.color.white_30));;
                    ((TextView)findViewById(R.id.followers)).setTextColor(act.getColor(R.color.white_30));;
                    ((TextView)findViewById(R.id.posts)).setTextSize(statics.pixelsToSp(getResources().getDimension(R.dimen.tab_profile_text_low),act));
                    ((TextView)findViewById(R.id.following)).setTextSize(statics.pixelsToSp(getResources().getDimension(R.dimen.tab_profile_text_low),act));
                    ((TextView)findViewById(R.id.followers)).setTextSize(statics.pixelsToSp(getResources().getDimension(R.dimen.tab_profile_text_low),act));
                    ProfileLikesFragment f= ProfileLikesFragment.newInstance(userid);
                    if(last_index!=1)
                        if(last_index > 1) {
                            fragmentManager.beginTransaction()
                                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                                    .replace(R.id.frag_frag, f, null)
                                    .commit();
                        }else{
                            fragmentManager.beginTransaction()
                                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                                    .replace(R.id.frag_frag, f, null)
                                    .commit();
                        }
                    last_index=1;
                }else if(selected_lin == R.id.followers_lin){
                    ((TextView)findViewById(R.id.posts)).setTextColor(act.getColor(R.color.white_30));;
                    ((TextView)findViewById(R.id.following)).setTextColor(act.getColor(R.color.white_30));;
                    ((TextView)findViewById(R.id.likes)).setTextColor(act.getColor(R.color.white_30));;
                    ((TextView)findViewById(R.id.posts)).setTextSize(statics.pixelsToSp(getResources().getDimension(R.dimen.tab_profile_text_low),act));
                    ((TextView)findViewById(R.id.following)).setTextSize(statics.pixelsToSp(getResources().getDimension(R.dimen.tab_profile_text_low),act));
                    ((TextView)findViewById(R.id.likes)).setTextSize(statics.pixelsToSp(getResources().getDimension(R.dimen.tab_profile_text_low),act));
                    FollowersFragment f= FollowersFragment.newInstance(userid);
                    if(last_index!=3)
                        if(last_index > 3) {
                            fragmentManager.beginTransaction()
                                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                                    .replace(R.id.frag_frag, f, null)
                                    .commit();
                        }else{
                            fragmentManager.beginTransaction()
                                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                                    .replace(R.id.frag_frag, f, null)
                                    .commit();
                        }
                    last_index=3;
                }else{
                    ((TextView)findViewById(R.id.posts)).setTextColor(act.getColor(R.color.white_30));;
                    ((TextView)findViewById(R.id.followers)).setTextColor(act.getColor(R.color.white_30));;
                    ((TextView)findViewById(R.id.likes)).setTextColor(act.getColor(R.color.white_30));;
                    ((TextView)findViewById(R.id.posts)).setTextSize(statics.pixelsToSp(getResources().getDimension(R.dimen.tab_profile_text_low),act));
                    ((TextView)findViewById(R.id.followers)).setTextSize(statics.pixelsToSp(getResources().getDimension(R.dimen.tab_profile_text_low),act));
                    ((TextView)findViewById(R.id.likes)).setTextSize(statics.pixelsToSp(getResources().getDimension(R.dimen.tab_profile_text_low),act));
                    FollowingFragment f= FollowingFragment.newInstance(userid);
                    if(last_index!=2)
                        if(last_index > 2) {
                            fragmentManager.beginTransaction()
                                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                                    .replace(R.id.frag_frag, f, null)
                                    .commit();
                        }else{
                            fragmentManager.beginTransaction()
                                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                                    .replace(R.id.frag_frag, f, null)
                                    .commit();
                        }
                    last_index=2;
                }
                tw.setTextSize(statics.pixelsToSp(getResources().getDimension(R.dimen.tab_profile_text_cap),act));

                p= (LinearLayout.LayoutParams) relTab.getLayoutParams();
                tw.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        View parent=(View)tw.getParent();
                        p.setMargins(((int) parent.getX()+20),0,0,0);
                        p.width = (int) (parent.getWidth()*0.5);
                        a.setDuration(500);
                        relTab.startAnimation(a);

                    }
                },150);


            }
            return false;
        }
    };
    Animation a = new Animation() {

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            relTab.setLayoutParams(p);
        }
    };
}