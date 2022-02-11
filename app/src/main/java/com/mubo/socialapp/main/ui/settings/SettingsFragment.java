package com.mubo.socialapp.main.ui.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mubo.socialapp.R;
import com.mubo.socialapp.ShowImage;
import com.mubo.socialapp.api.ApiClass;
import com.mubo.socialapp.api.HttpPostJson;
import com.mubo.socialapp.api.UrlList;
import com.mubo.socialapp.helpers.Ayarlar;
import com.mubo.socialapp.helpers.CircularTransform;
import com.mubo.socialapp.helpers.statics;
import com.mubo.socialapp.main.ui.post.ImagePostFragment;
import com.opensooq.supernova.gligar.GligarPicker;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SettingsFragment extends Fragment {


    LinearLayout post_lin,like_lin,following_lin,followers_lin;
    RelativeLayout relTab;
    FragmentManager fragmentManager;
    LinearLayout.LayoutParams p;
    View mainView;
    String id=null;
    TextView name;
    EditText name_edit;
    ImageView person;
    LinearLayout uname_edit;
    RelativeLayout okrel;
    NestedScrollView nested;
    private boolean isLoading = false;

    private boolean isLastPage = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
      View v =  inflater.inflate(R.layout.fragment_settings, container, false);
        Ayarlar ayarlar=new Ayarlar(getContext());
        id=ayarlar.get_pref_string("id");
        name = v.findViewById(R.id.name);
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uname_edit.setVisibility(View.VISIBLE);
                view.setVisibility(View.GONE);
                name_edit.setText(name.getText().toString());
            }
        });
        name_edit = v.findViewById(R.id.edit_name);
        uname_edit = v.findViewById(R.id.edit);
        okrel = v.findViewById(R.id.okrel);
        okrel.setOnClickListener(ok_edit);
        person = v.findViewById(R.id.person);
        person.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!p_url.isEmpty()) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Profile Photo")
                            .setMessage("Show or Edit?")
                            .setPositiveButton("Show", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent i = new Intent(getContext(), ShowImage.class);
                                    i.putExtra("image", p_url);
                                    startActivity(i);
                                }
                            })
                            .setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    new GligarPicker().requestCode(124).withActivity(getActivity()).limit(1).show();
                                }
                            })
                            .setNeutralButton("Cancel", null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }else{
                    new GligarPicker().requestCode(124).withActivity(getActivity()).limit(1).show();
                }
            }
        });
        post_lin=v.findViewById(R.id.post_lin);
        like_lin=v.findViewById(R.id.like_lin);
        following_lin=v.findViewById(R.id.following_lin);
        followers_lin=v.findViewById(R.id.followers_lin);
        nested=v.findViewById(R.id.nested);
        post_lin.setOnTouchListener(tl);
        like_lin.setOnTouchListener(tl);
        following_lin.setOnTouchListener(tl);
        followers_lin.setOnTouchListener(tl);
        relTab=v.findViewById(R.id.bottom_rel);
        fragmentManager = getActivity().getSupportFragmentManager();
        PostsFragment f= PostsFragment.newInstance(id);
        fragmentManager.beginTransaction()
                .replace(R.id.frag_frag,f, null)
                .setReorderingAllowed(true)
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .commit();
        mainView=v;
        p= (LinearLayout.LayoutParams) relTab.getLayoutParams();
        post_lin.postDelayed(new Runnable() {
            @Override
            public void run() {
                p.setMargins(20,0,0,0);
                p.width = (int) (post_lin.getWidth()*0.5);
                relTab.setLayoutParams(p);
            }
        },100);

        nested.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                View lastChild = nested.getChildAt(nested.getChildCount() - 1);

                if (lastChild != null) {

                    if ((i1 >= (lastChild.getMeasuredHeight() - nested.getMeasuredHeight())) && i1 > i3 && !isLoading && !isLastPage) {
                        Fragment f = fragmentManager.findFragmentById(R.id.frag_frag);
                        if(f instanceof ProfileLikesFragment)
                            ((ProfileLikesFragment) f).getNew();
                        else if(f instanceof FollowingFragment)
                            ((FollowingFragment) f).getNew();
                        else if(f instanceof FollowersFragment)
                            ((FollowersFragment) f).getNew();
                        else if(f instanceof PostsFragment)
                            ((PostsFragment) f).getNew();

                    }
                }
            }
        });



        getUser();
      return v;
    }
    void getUser(){
        JSONObject jo=new JSONObject();
        try {
            jo.put("_id",id);
            JSONArray ja =new JSONArray();
            ja.put("user_pass");
            jo.put("remove_fields",ja);
            ApiClass ac=new ApiClass();
            ac.getCollection(jo,null,hnd,getActivity());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    String p_url="";
    Handler hnd=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 1:
                    String sonuc=(String)msg.obj;
                    try {
                        JSONObject jo = new JSONObject(sonuc);
                        p_url=jo.getJSONObject("content").optString("user_image");
                        if(p_url!=null && !p_url.trim().isEmpty()){
                            Picasso.get().load(p_url).transform(new CircularTransform()).resize(150,150).into(person);
                        }else{
                            person.setImageDrawable(getContext().getDrawable(R.mipmap.no_image));
                        }
                        name.setText(jo.getJSONObject("content").getString("user_username"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getContext(), "OK", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(getContext(), "Hata", Toast.LENGTH_SHORT).show();
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
                tw.setTextColor(getActivity().getColor(R.color.white));
            }else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                TextView t;
                RelativeLayout rt;
                if(selected_lin == R.id.post_lin){
                    ((TextView)mainView.findViewById(R.id.likes)).setTextColor(getActivity().getColor(R.color.white_30));;
                    ((TextView)mainView.findViewById(R.id.following)).setTextColor(getActivity().getColor(R.color.white_30));;
                    ((TextView)mainView.findViewById(R.id.followers)).setTextColor(getActivity().getColor(R.color.white_30));;
                    ((TextView)mainView.findViewById(R.id.likes)).setTextSize(statics.pixelsToSp(getResources().getDimension(R.dimen.tab_profile_text_low),getActivity()));
                    ((TextView)mainView.findViewById(R.id.following)).setTextSize(statics.pixelsToSp(getResources().getDimension(R.dimen.tab_profile_text_low),getActivity()));
                    ((TextView)mainView.findViewById(R.id.followers)).setTextSize(statics.pixelsToSp(getResources().getDimension(R.dimen.tab_profile_text_low),getActivity()));
                    PostsFragment f= PostsFragment.newInstance(id);
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
                    ((TextView)mainView.findViewById(R.id.posts)).setTextColor(getActivity().getColor(R.color.white_30));;
                    ((TextView)mainView.findViewById(R.id.following)).setTextColor(getActivity().getColor(R.color.white_30));;
                    ((TextView)mainView.findViewById(R.id.followers)).setTextColor(getActivity().getColor(R.color.white_30));;
                    ((TextView)mainView.findViewById(R.id.posts)).setTextSize(statics.pixelsToSp(getResources().getDimension(R.dimen.tab_profile_text_low),getActivity()));
                    ((TextView)mainView.findViewById(R.id.following)).setTextSize(statics.pixelsToSp(getResources().getDimension(R.dimen.tab_profile_text_low),getActivity()));
                    ((TextView)mainView.findViewById(R.id.followers)).setTextSize(statics.pixelsToSp(getResources().getDimension(R.dimen.tab_profile_text_low),getActivity()));
                    ProfileLikesFragment f= ProfileLikesFragment.newInstance(id);
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
                    ((TextView)mainView.findViewById(R.id.posts)).setTextColor(getActivity().getColor(R.color.white_30));;
                    ((TextView)mainView.findViewById(R.id.following)).setTextColor(getActivity().getColor(R.color.white_30));;
                    ((TextView)mainView.findViewById(R.id.likes)).setTextColor(getActivity().getColor(R.color.white_30));;
                    ((TextView)mainView.findViewById(R.id.posts)).setTextSize(statics.pixelsToSp(getResources().getDimension(R.dimen.tab_profile_text_low),getActivity()));
                    ((TextView)mainView.findViewById(R.id.following)).setTextSize(statics.pixelsToSp(getResources().getDimension(R.dimen.tab_profile_text_low),getActivity()));
                    ((TextView)mainView.findViewById(R.id.likes)).setTextSize(statics.pixelsToSp(getResources().getDimension(R.dimen.tab_profile_text_low),getActivity()));
                    FollowersFragment f= FollowersFragment.newInstance(id);
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
                    ((TextView)mainView.findViewById(R.id.posts)).setTextColor(getActivity().getColor(R.color.white_30));;
                    ((TextView)mainView.findViewById(R.id.followers)).setTextColor(getActivity().getColor(R.color.white_30));;
                    ((TextView)mainView.findViewById(R.id.likes)).setTextColor(getActivity().getColor(R.color.white_30));;
                    ((TextView)mainView.findViewById(R.id.posts)).setTextSize(statics.pixelsToSp(getResources().getDimension(R.dimen.tab_profile_text_low),getActivity()));
                    ((TextView)mainView.findViewById(R.id.followers)).setTextSize(statics.pixelsToSp(getResources().getDimension(R.dimen.tab_profile_text_low),getActivity()));
                    ((TextView)mainView.findViewById(R.id.likes)).setTextSize(statics.pixelsToSp(getResources().getDimension(R.dimen.tab_profile_text_low),getActivity()));
                    FollowingFragment f= FollowingFragment.newInstance(id);
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
                tw.setTextSize(statics.pixelsToSp(getResources().getDimension(R.dimen.tab_profile_text_cap),getActivity()));

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
    View.OnClickListener ok_edit=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String u=name_edit.getText().toString().trim();
            if(!u.isEmpty()){
                JSONObject j =new JSONObject();
                try {
                    j.put("id",id);
                    JSONArray fields=new JSONArray();
                    JSONObject field=new JSONObject();
                    field.put("field","user_username");
                    field.put("value",u);
                    field.put("isUnique",true);
                    fields.put(field);
                    j.put("fields",fields);
                    update(j);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    void update(JSONObject j){
        ApiClass ac=new ApiClass();
        ac.updateCollection(j,null,hndupdate,getActivity());
        uname_edit.setVisibility(View.GONE);
        name.setVisibility(View.VISIBLE);
        statics.show_ok(getActivity());
    }
    public void setImagePath(String path){
        new AlertDialog.Builder(getContext())
                .setTitle("Update Ptofile Photo")
                .setMessage("Do you really want to update profile photo?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        update_image(path);
                    }
                })
                .setNegativeButton(android.R.string.no,null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    void update_image(String path){
        Map<String,String> params=new HashMap<>();
        params.put("bucket",ApiClass.applicationId);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = HttpPostJson.multipartRequest(UrlList.upload_object,params,path,"file","");
                String im=UrlList.upload_main_url+s;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject j = new JSONObject();
                            j.put("id", id);
                            JSONArray fields = new JSONArray();
                            JSONObject field = new JSONObject();
                            field.put("field", "user_image");
                            field.put("value", im);
                            fields.put(field);
                            j.put("fields", fields);
                            update(j);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                Log.i("Upload Result",s);
            }
        }).start();
    }
    Handler hndupdate=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            last_index=0;
            PostsFragment f= PostsFragment.newInstance(id);
            fragmentManager.beginTransaction()
                    .replace(R.id.frag_frag,f, null)
                    .setReorderingAllowed(true)
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .commit();
        }
    };
}