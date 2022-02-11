package com.mubo.socialapp.main.ui.post;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mubo.socialapp.R;
import com.mubo.socialapp.api.ApiClass;
import com.mubo.socialapp.api.HttpPostJson;
import com.mubo.socialapp.api.UrlList;
import com.mubo.socialapp.helpers.Ayarlar;
import com.mubo.socialapp.helpers.statics;
import com.opensooq.supernova.gligar.GligarPicker;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class ImagePostFragment extends Fragment {

    public ImagePostFragment() {
        // Required empty public constructor
    }

    public static ImagePostFragment newInstance() {
        ImagePostFragment fragment = new ImagePostFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    Button camera;
    Fragment f;
    ImageView image;
    LinearLayout.LayoutParams p;
    String id=null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        f=this;
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_image_post, container, false);
        camera = v.findViewById(R.id.camera);
        image = v.findViewById(R.id.image);
        Ayarlar ayarlar=new Ayarlar(getContext());
        id=ayarlar.get_pref_string("id");
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new GligarPicker().requestCode(123).withActivity(getActivity()).limit(1).show();
            }
        });
        return v;
    }
    File imgFile;
    String fPath="";
    public void setImage(String path){
        this.fPath=path;
       imgFile = new File(path);

        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            image.setImageBitmap(myBitmap);
            p= (LinearLayout.LayoutParams) image.getLayoutParams();
            int height=(image.getWidth()*9)/16;
            p.height=height;
            image.setLayoutParams(p);
        }
    }
    public void send(){
        if(imgFile!=null){
            new AlertDialog.Builder(getContext())
                    .setTitle("New Post")
                    .setMessage("Are you sure you want to create this post?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            senddata();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }else{
            new AlertDialog.Builder(getContext())
                    .setTitle("Error")
                    .setMessage("Please choose an image before send post!")
                    .setPositiveButton(android.R.string.ok,null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }
    void senddata(){

        Map<String,String> params=new HashMap<>();
        params.put("bucket",ApiClass.applicationId);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = HttpPostJson.multipartRequest(UrlList.upload_object,params,fPath,"file","");
                String im=UrlList.upload_main_url+s;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            createPost(im);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                Log.i("Upload Result",s);
            }
        }).start();
    }
    void createPost(String url) throws JSONException {
        long createdDate=System.currentTimeMillis();
        JSONObject j=new JSONObject();
        j.put("collectionName","post");
        JSONObject content=new JSONObject();
        content.put("post_type","image");
        content.put("post_data",url);
        content.put("post_createdDate",createdDate);
        content.put("post_isActive",true);
        content.put("post_like_size",0);
        content.put("post_comment_size",0);
        content.put("post_rating_size",0);
        j.put("content",content);
        ApiClass ac=new ApiClass();
        ac.addCollection(j,null,hndpost,getActivity());
    }
    Handler hndpost=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    try {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LinearLayout.LayoutParams p= (LinearLayout.LayoutParams) image.getLayoutParams();
                                image.setImageResource(0);
                                p.height=WRAP_CONTENT;
                                image.setLayoutParams(p);
                            }
                        });
                        imgFile=null;
                        fPath="";
                        statics.show_ok(getActivity());
                        JSONObject j = new JSONObject((String) msg.obj);
                        String post_id = j.getString("id");
                        relatePostWithUser(post_id);
                        relateUserWithPost(post_id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }
        }
    };
    void relatePostWithUser(String p_id) throws JSONException {
        JSONArray relations=new JSONArray();
        JSONArray contents=new JSONArray();
        JSONObject rel=new JSONObject();
        rel.put("relationName","userRelations");
        rel.put("to_collectionName","user");
        rel.put("to",id);
        relations.put(rel);
        JSONObject contentdata=new JSONObject();
        contentdata.put("id",p_id);
        contents.put(contentdata);
        JSONObject send_data=new JSONObject();
        send_data.put("relations",relations);
        send_data.put("contents",contents);
        ApiClass ac=new ApiClass();
        ac.addRelation(send_data ,null,hnd,getActivity());
    }
    void relateUserWithPost(String p_id) throws JSONException {
        JSONArray relations=new JSONArray();
        JSONArray contents=new JSONArray();
        JSONObject rel=new JSONObject();
        rel.put("relationName","postRelations");
        rel.put("to_collectionName","post");
        rel.put("to",p_id);
        relations.put(rel);
        JSONObject contentdata=new JSONObject();
        contentdata.put("id",id);
        contents.put(contentdata);
        JSONObject send_data=new JSONObject();
        send_data.put("relations",relations);
        send_data.put("contents",contents);
        ApiClass ac=new ApiClass();
        ac.addRelation(send_data ,null,hnd,getActivity());
    }
    Handler hnd=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            Log.i("ReturnData",(String)msg.obj);
        }
    };
}