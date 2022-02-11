package com.mubo.socialapp.single_post;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mubo.socialapp.R;
import com.mubo.socialapp.UserProfile;
import com.mubo.socialapp.api.ApiClass;
import com.mubo.socialapp.api.NotificationClass;
import com.mubo.socialapp.api.PushNotifictionHelper;
import com.mubo.socialapp.helpers.Ayarlar;
import com.mubo.socialapp.helpers.CircularTransform;
import com.mubo.socialapp.helpers.statics;
import com.mubo.socialapp.login_signup.login_fragment;
import com.mubo.socialapp.login_signup.signup_fragment;
import com.mubo.socialapp.main.ui.home.PostData;
import com.mubo.socialapp.main.ui.settings.FollowersFragment;
import com.mubo.socialapp.main.ui.settings.FollowingFragment;
import com.mubo.socialapp.main.ui.settings.PostsFragment;
import com.mubo.socialapp.main.ui.settings.ProfileLikesFragment;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SinglePost extends AppCompatActivity {

    PostData pd;
    TextView name,time,content,like_size,comment_size;
    ImageView person,image_content,like;
    LinearLayout profile,like_lin;
    Context cx;
    Activity act;
    RelativeLayout back;
    LinearLayout log_lin;
    FragmentManager fragmentManager;
    LinearLayout.LayoutParams p;
    TextView tw;
    RelativeLayout relTab;
    int selected_lin=R.id.log_lin;
    RelativeLayout main;
    int val=0;
    String id=null;
    ImageButton send;
    RelativeLayout comment_rel;
    EditText comment_edit;
    TextView counter;
    NestedScrollView nested;
    boolean isLoading=false;
    boolean isLastPage=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post);
        act = this;
        cx = this;
        pd = this.getIntent().getParcelableExtra("post");
        back = findViewById(R.id.back);
        main = findViewById(R.id.main);
        main.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                main.getWindowVisibleDisplayFrame(r);
                int screenHeight = main.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;

                if (keypadHeight > screenHeight * 0.15) {
                    main.setY((float) (getSoftButtonsBarHeight() - keypadHeight));
                } else {
                    main.setY(0F);
                }


            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                act.finish();
            }
        });
        name = findViewById(R.id.name);
        time = findViewById(R.id.time);
        content = findViewById(R.id.content);
        like_size = findViewById(R.id.like_size);
        comment_size = findViewById(R.id.comment_size);
        like = findViewById(R.id.like);
        person = findViewById(R.id.person);
        image_content = findViewById(R.id.image_content);
        profile = findViewById(R.id.profile);
        Ayarlar ayarlar = new Ayarlar(cx);
        id = ayarlar.get_pref_string("id");
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userid = pd.getId();
                Intent i = new Intent(cx, UserProfile.class);
                i.putExtra("userid", userid);
                startActivity(i);
            }
        });
        like_lin = findViewById(R.id.like_lin);
        like_lin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView im = view.findViewById(R.id.like);
                TextView tw = view.findViewById(R.id.like_size);
                String s = pd.getLike_size();
                long ss = Long.parseLong(s);
                boolean selfLiked = pd.isSelf_liked();
                if (selfLiked) {
                    ss--;
                    ;
                    val = -1;
                    try {
                        statics.removeLike(pd.getLike_id(), p_hnd, act);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    pd.setSelf_liked(false);
                    im.setImageDrawable(getDrawable(R.drawable.empty_heart));
                } else {
                    ss++;
                    val = 1;
                    try {
                        statics.addLike(pd.getPost_id(), id, p_hnd, act);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    pd.setSelf_liked(true);
                    im.setImageDrawable(getDrawable(R.drawable.full_heart));
                }
                pd.setLike_size(String.valueOf(ss));
                like_size.setText(pd.getLike_size());
            }
        });
        name.setText(pd.getName());
        time.setText(pd.getTime());
        String person_url = pd.getPerson();
        if (person_url.trim().isEmpty())
            person.setImageDrawable(cx.getDrawable(R.mipmap.no_image));
        else
            Picasso.get().load(pd.getPerson()).transform(new CircularTransform()).resize(150,150).into(person);
        String type = pd.getType();
        if (type.toLowerCase().equals("text")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                content.setText(Html.fromHtml(pd.getContent(), Html.FROM_HTML_MODE_COMPACT));
            } else {
                content.setText(Html.fromHtml(pd.getContent()));
            }
            //content.setText(pd.getContent());
            image_content.setVisibility(View.GONE);
            content.setVisibility(View.VISIBLE);
        } else {
            image_content.setVisibility(View.VISIBLE);
            content.setVisibility(View.GONE);
            Picasso.get().load(pd.getContent()).resize(720, 405).centerInside().into(image_content);
        }
        like_size.setText(pd.getLike_size());
        comment_size.setText(pd.getComment_size());
        boolean self_like = pd.isSelf_liked();
        if(self_like) {
            like.setImageDrawable(cx.getDrawable(R.drawable.full_heart));
            like_size.setTextColor(cx.getColor(R.color.white));
        }else {
            like.setImageDrawable(cx.getDrawable(R.drawable.empty_heart));
            like_size.setTextColor(cx.getColor(R.color.white_30));
        }

        log_lin=findViewById(R.id.log_lin);
        relTab=findViewById(R.id.bottom_rel);
        p= (LinearLayout.LayoutParams) relTab.getLayoutParams();
        log_lin.postDelayed(new Runnable() {
            @Override
            public void run() {
                p.setMargins(20,0,0,0);
                p.width = (int) (log_lin.getWidth()*0.5);
                relTab.setLayoutParams(p);
            }
        },100);
        nested = findViewById(R.id.nested);
        send = findViewById(R.id.send_button);
        send.setOnClickListener(send_cl);
        comment_rel = findViewById(R.id.comment_rel);
        comment_edit = findViewById(R.id.comment_edit);
        fragmentManager = getSupportFragmentManager();
        CommentsFragment f= CommentsFragment.newInstance(pd.getPost_id(),pd.getId());
        fragmentManager.beginTransaction()
                .replace(R.id.frag,f, null)
                .setReorderingAllowed(true)
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .commit();
        counter=findViewById(R.id.counter);
        comment_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String[] ss=counter.getText().toString().split("/");
                int c=editable.toString().length();
                String t=String.valueOf(c)+" / "+ ss[1].trim();
                counter.setText(t);
            }
        });
        nested.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                View lastChild = nested.getChildAt(nested.getChildCount() - 1);

                if (lastChild != null) {

                    if ((i1 >= (lastChild.getMeasuredHeight() - nested.getMeasuredHeight())) && i1 > i3 && !isLoading && !isLastPage) {
                        Fragment f = fragmentManager.findFragmentById(R.id.frag);
                        if(f instanceof CommentsFragment)
                            ((CommentsFragment) f).getNew();
                    }
                }
            }
        });
    }
    Handler p_hnd=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 1:
                    try {
                        if(val>0) {
                            JSONArray j = new JSONArray((String) msg.obj);
                            String like_id=j.getJSONObject(0).getString("postLike_id");
                            pd.setLike_id(like_id);
                            add_notification(pd.getPost_id(),pd.getId());
                            NotificationClass nc=new NotificationClass("New Like","$ liked your post","",String.valueOf(R.drawable.full_heart));
                            PushNotifictionHelper.sendNotification(pd.getId(),act,nc);
                        }else{
                            pd.setLike_id("");
                        }
                        statics.updateOthers(pd);
                        statics.increase_like(pd.getPost_id(),"post_like_size",val,sizehnd,act);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }
        }
    };
    void add_notification(String postID,String userID) throws JSONException {
        JSONArray relations=new JSONArray();
        JSONArray contents=new JSONArray();
        JSONObject relation=new JSONObject();
        relation.put("relationName","userRelations");
        relation.put("id",userID);
        relations.put(relation);
        JSONObject content=new JSONObject();
        JSONObject i_content=new JSONObject();
        content.put("collectionName","notification");
        i_content.put("notification_data","like");
        i_content.put("notification_createdDate",System.currentTimeMillis());
        content.put("content",i_content);

        JSONArray innerData=new JSONArray();
        JSONObject i1=new JSONObject();
        i1.put("relationName","userRelations");
        i1.put("id",id);
        JSONArray fs1=new JSONArray();
        fs1.put("user_username");
        fs1.put("user_image");
        i1.put("fields",fs1);

        JSONObject i2=new JSONObject();
        i2.put("relationName","userRelations");
        i2.put("id",postID);
        JSONArray fs2=new JSONArray();
        fs2.put("post_data");
        fs2.put("post_type");
        fs2.put("post_createdDate");
        i2.put("fields",fs2);

        innerData.put(i1);
        innerData.put(i2);
        content.put("innerData",innerData);
        contents.put(content);
        ApiClass ac=new ApiClass();
        JSONObject m=new JSONObject();
        m.put("relations",relations);
        m.put("contents",contents);
        ac.addRelation(m,null,hnd_notification,act);
    }
    Handler hnd_notification=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
        }
    };
    Handler sizehnd=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
        }
    };
    private int getSoftButtonsBarHeight() {
        // getRealMetrics is only available with API 17 and +
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int usableHeight = metrics.heightPixels;
            getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
            int realHeight = metrics.heightPixels;
            if (realHeight > usableHeight)
                return realHeight - usableHeight;
            else
                return 0;
        }
        return 0;
    }
    View.OnClickListener send_cl=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String ce=comment_edit.getText().toString();
            if(!ce.isEmpty()) {
                new AlertDialog.Builder(act)
                        .setTitle("Comment")
                        .setMessage("Are you sure you want to send this comment?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    send_comment(ce);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        }
    };
    void send_comment(String comment) throws JSONException {
        long createdDate=System.currentTimeMillis();
        JSONArray relations=new JSONArray();
        JSONArray contents=new JSONArray();
        JSONObject rel=new JSONObject();
        rel.put("relationName","postRelations");
        rel.put("id",pd.getPost_id());
        relations.put(rel);
        JSONObject contentdata=new JSONObject();
        contentdata.put("collectionName","comment");
        JSONObject content =new JSONObject();
        content.put("comment_data",comment);
        content.put("comment_createdDate",createdDate);
        content.put("comment_isActive",true);
        JSONArray innerData=new JSONArray();
        JSONObject idata=new JSONObject();
        idata.put("relationName","postRelations");
        idata.put("id",id);
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
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String time = sdf.format(new Date(createdDate));
        commentData=new CommentData("","",time,"",comment,"");
        ApiClass ac=new ApiClass();
        ac.addRelation(send_data ,null,hndsend,act);
        comment_edit.setText("");
    }
    void getUser(){
        JSONObject jo=new JSONObject();
        try {
            jo.put("_id",id);
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
                        commentData.setId(jo.getString("id"));
                        commentData.setName(jo.getJSONObject("content").getString("user_username"));
                        commentData.setPerson(p_url.trim());
                        Fragment f = fragmentManager.findFragmentById(R.id.frag);
                        if(f instanceof CommentsFragment)
                            ((CommentsFragment) f).addNew(commentData);
                        commentData=null;
                        int csize=Integer.valueOf(comment_size.getText().toString());
                        csize++;
                        comment_size.setText(String.valueOf(csize));
                        pd.setComment_size(String.valueOf(csize));
                        statics.updateOthers(pd);
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
    CommentData commentData=null;
    Handler hndsend=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 1:
                    try {

                        JSONArray j=new JSONArray((String)msg.obj);
                        statics.show_ok(act);
                        String comment_id = j.getJSONObject(0).getString("comment_id");
                        commentData.setComment_id(comment_id);
                        getUser();
                        add_notification(comment_id);
                        NotificationClass nc=new NotificationClass("New Comment","$ commented on your post","",String.valueOf(R.drawable.notification_comment));
                        PushNotifictionHelper.sendNotification(pd.getId(),act,nc);
                        statics.increase_like(pd.getPost_id(),"post_comment_size",1,sizehnd,act);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }
        }
    };
    void add_notification(String comment_id) throws JSONException {
        JSONArray relations=new JSONArray();
        JSONArray contents=new JSONArray();
        JSONObject relation=new JSONObject();
        relation.put("relationName","userRelations");
        relation.put("id",pd.getId());
        relations.put(relation);
        JSONObject content=new JSONObject();
        JSONObject i_content=new JSONObject();
        content.put("collectionName","notification");
        i_content.put("notification_data","comment");
        i_content.put("notification_createdDate",System.currentTimeMillis());
        content.put("content",i_content);

        JSONArray innerData=new JSONArray();
        JSONObject i1=new JSONObject();
        i1.put("relationName","userRelations");
        i1.put("id",id);
        JSONArray fs1=new JSONArray();
        fs1.put("user_username");
        fs1.put("user_image");
        i1.put("fields",fs1);

        JSONObject i2=new JSONObject();
        i2.put("relationName","userRelations");
        i2.put("id",pd.getPost_id());
        JSONArray fs2=new JSONArray();
        fs2.put("post_data");
        fs2.put("post_type");
        fs2.put("post_createdDate");
        i2.put("fields",fs2);

        JSONObject i3=new JSONObject();
        i3.put("relationName","userRelations");
        i3.put("id",comment_id);
        JSONArray fs3=new JSONArray();
        fs3.put("comment_data");
        fs3.put("comment_createdDate");
        i3.put("fields",fs3);
        innerData.put(i1);
        innerData.put(i2);
        innerData.put(i3);
        content.put("innerData",innerData);
        contents.put(content);
        ApiClass ac=new ApiClass();
        JSONObject m=new JSONObject();
        m.put("relations",relations);
        m.put("contents",contents);
        ac.addRelation(m,null,hnd_notification,act);
    }
}