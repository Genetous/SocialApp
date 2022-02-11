package com.mubo.socialapp.main.ui.post;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.mubo.socialapp.R;
import com.mubo.socialapp.api.ApiClass;
import com.mubo.socialapp.helpers.Ayarlar;
import com.mubo.socialapp.helpers.statics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TextPostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TextPostFragment extends Fragment {

    public TextPostFragment() {
        // Required empty public constructor
    }

    public static TextPostFragment newInstance() {
        TextPostFragment fragment = new TextPostFragment();
        return fragment;
    }
    EditText input;
    TextView counter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    String id=null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_text_post, container, false);
        input=v.findViewById(R.id.post_text);
        counter=v.findViewById(R.id.counter);
        input.addTextChangedListener(new TextWatcher() {
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
        Ayarlar ayarlar=new Ayarlar(getContext());
        id=ayarlar.get_pref_string("id");
        return v;
    }
    public void send() {
        String msgtext= Html.toHtml(input.getText());
        if(!input.getText().toString().trim().isEmpty()){
            new AlertDialog.Builder(getContext())
                    .setTitle("New Post")
                    .setMessage("Are you sure you want to create this post?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                send_post(msgtext);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }else{
            new AlertDialog.Builder(getContext())
                    .setTitle("Error")
                    .setMessage("Please enter something before send post!")
                    .setPositiveButton(android.R.string.ok,null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }
    void send_post(String msgtext) throws JSONException {
        long createdDate=System.currentTimeMillis();
        JSONObject j=new JSONObject();
        j.put("collectionName","post");
        JSONObject content=new JSONObject();
        content.put("post_type","text");
        content.put("post_data",msgtext);
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
                                input.setText("");
                            }
                        });
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