package com.mubo.socialapp.single_post;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
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
import com.mubo.socialapp.helpers.statics;
import com.mubo.socialapp.main.ui.home.PostData;
import com.mubo.socialapp.main.ui.home.postAdaptor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.mubo.socialapp.helpers.PaginationListener.PAGE_START;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CommentsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommentsFragment extends Fragment {

    private static final String PostID = "postId";
    private static final String UserID = "UserId";
    public CommentsFragment() {
        // Required empty public constructor
    }
    public static CommentsFragment newInstance(String postID,String userID) {
        CommentsFragment fragment = new CommentsFragment();
        Bundle args = new Bundle();
        args.putString(PostID, postID);
        args.putString(UserID, userID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            postID = getArguments().getString(PostID);
            userID = getArguments().getString(UserID);
        }
    }
    RecyclerView rView;
    FrameLayout frm;

    LinearLayout.LayoutParams p;
    TextView counter;

    commentAdaptor ca;
    String postID="";
    String userID="";
    ImageButton send;
    RelativeLayout comment_rel;
    EditText input;
    String id=null;
    EditText comment_edit;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_comments, container, false);
        rView = v.findViewById(R.id.comments_rec);
        frm= v.findViewById(R.id.frm);
        //send = v.findViewById(R.id.send_button);
       /* send.setOnClickListener(send_cl);
        comment_rel = v.findViewById(R.id.comment_rel);
        comment_edit = v.findViewById(R.id.comment_edit);*/
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rView.setLayoutManager(layoutManager);
        input=v.findViewById(R.id.comment_edit);
        counter=v.findViewById(R.id.counter);
      /*  input.addTextChangedListener(new TextWatcher() {
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
        });*/
        try {
            ndata=true;
            getComments(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Ayarlar ayarlar=new Ayarlar(getContext());
        id=ayarlar.get_pref_string("id");
        return v;
    }
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    int itemcount=5;
    private boolean isLoading = false;
    int size=0;
    boolean ndata=true;
    public void addNew(CommentData cd){
        cds.add(0,cd);
        if(ca==null) {
            ca = new commentAdaptor(getContext(), cds);
            ca.setClickListener(new commentAdaptor.ItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    String userid = cds.get(position).getId();
                    Intent i = new Intent(getContext(), UserProfile.class);
                    i.putExtra("userid", userid);
                    startActivity(i);
                }
            });
            rView.setAdapter(ca);
        }else{
            ca.notifyDataSetChanged();
        }

    }
    public void getNew(){
        try {
            int last_ind=cds.size();
            if(last_ind<size) {
                ndata = false;
                getComments(last_ind);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    View.OnClickListener send_cl=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String ce=comment_edit.getText().toString();
            if(!ce.isEmpty()) {
                new AlertDialog.Builder(getContext())
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
        rel.put("id",postID);
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
        ApiClass ac=new ApiClass();
        ac.addRelation(send_data ,null,hndsend,getActivity());
        comment_edit.setText("");
    }
    Handler hndsend=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 1:
                    try {
                        JSONArray j=new JSONArray((String)msg.obj);
                        String comment_id = j.getJSONObject(0).getString("comment_id");
                        add_notification(comment_id);
                        NotificationClass nc=new NotificationClass("New Comment","$ commented on your post","",String.valueOf(R.drawable.notification_comment));
                        PushNotifictionHelper.sendNotification(userID,getActivity(),nc);
                        statics.increase_like(postID,"post_comment_size",1,sizehnd,getActivity());
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
        relation.put("id",userID);
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
        i2.put("id",postID);
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
        ac.addRelation(m,null,hnd_notification,getActivity());
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
    void getComments(int start) throws JSONException {
        if(ndata)
            cds.clear();
        JSONObject sendObject=new JSONObject();

        sendObject.put("relationName","postRelations");
        sendObject.put("to",postID);
        JSONObject gbr=new JSONObject();
        JSONArray f=new JSONArray();
        JSONObject com = new JSONObject();
        JSONObject comField = new JSONObject();
        JSONObject sort = new JSONObject();
        sort.put("field", "content.comment_createdDate");
        sort.put("type", "desc");
        com.put("orderby", sort);
        com.put("from", 0);
        com.put("size", 10);
        comField.put("content.comment", com);
        f.put(comField);
        sendObject.put("fields",f);
        ApiClass ac=new ApiClass();
        if(ndata)
            ac.getRelations(sendObject,frm,hnd,getActivity());
        else
            ac.getRelations(sendObject,null,hnd,getActivity());
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
                        Toast.makeText(getContext(), "OK", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
                case 2:
                    Toast.makeText(getContext(), "Hata", Toast.LENGTH_SHORT).show();
            }
        }
    };
    ArrayList<CommentData> cds=new ArrayList<>();
    void bind(JSONObject j) throws JSONException {

        JSONArray values=j.getJSONArray("values");
        size=j.getInt("size");
        for(int i=0;i<values.length();++i) {
            JSONObject j_rel = values.getJSONObject(i);
            JSONObject comment = j_rel.getJSONObject("content").getJSONObject("comment");


            String comment_id = comment.getString("_id");
            JSONObject comment_content = comment.getJSONObject("content");
            long createdDate = comment_content.getLong("comment_createdDate");
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            String time = sdf.format(new Date(createdDate));
            String comment_data = comment_content.getString("comment_data");
            JSONArray user = comment.getJSONArray("comment_user");
            String userid = user.getJSONObject(0).getString("_id");
            String username = user.getJSONObject(0).getJSONObject("content").getString("user_username");
            String person = user.getJSONObject(0).getJSONObject("content").getString("user_image");
            CommentData cd = new CommentData(userid, username, time, person, comment_data, comment_id);
            cds.add(cd);

        }
        if(ndata) {
            ca = new commentAdaptor(getContext(), cds);
            ca.setClickListener(new commentAdaptor.ItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    String userid = cds.get(position).getId();
                    Intent i = new Intent(getContext(), UserProfile.class);
                    i.putExtra("userid", userid);
                    startActivity(i);
                }
            });
            rView.setAdapter(ca);
        }else{
            ca.notifyDataSetChanged();
        }

    }
}