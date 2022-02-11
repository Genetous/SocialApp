package com.mubo.socialapp.main.ui.settings;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mubo.socialapp.R;
import com.mubo.socialapp.UserProfile;
import com.mubo.socialapp.api.ApiClass;
import com.mubo.socialapp.api.NotificationClass;
import com.mubo.socialapp.api.PushNotifictionHelper;
import com.mubo.socialapp.helpers.Ayarlar;
import com.mubo.socialapp.helpers.DividerItemDecoration;
import com.mubo.socialapp.helpers.PaginationListener;
import com.mubo.socialapp.helpers.statics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.mubo.socialapp.helpers.PaginationListener.PAGE_START;

public class FollowingFragment extends Fragment {

    private static final String UserID = "";
    public FollowingFragment() {
        // Required empty public constructor
    }

    public static FollowingFragment newInstance(String userid) {
        FollowingFragment fragment = new FollowingFragment();
        Bundle args = new Bundle();
        args.putString(UserID, userid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userid = getArguments().getString(UserID);
        }
    }
    RecyclerView rView;
    FrameLayout frm;
    String id=null;
    String userid="";
    followAdaptor fa;
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    int itemcount=5;
    private boolean isLoading = false;
    int size=0;
    boolean ndata=true;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_following, container, false);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rView = v.findViewById(R.id.following_list);
        rView.setLayoutManager(layoutManager);
        frm=v.findViewById(R.id.frm);
        Ayarlar ayarlar=new Ayarlar(getContext());
        id=ayarlar.get_pref_string("id");
        rView.addItemDecoration(
                new DividerItemDecoration(getActivity().getDrawable(R.drawable.divider),
                        false, false));

        try {
            ndata=true;
            getFollowed(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return v;
    }
    public void getNew(){
        try {
            int last_ind=fds.size();
            if(last_ind<size) {
                ndata = false;
                getFollowed(last_ind);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    void getFollowed(int start) throws JSONException {
        if(ndata)
            fds.clear();
        JSONObject sendObject=new JSONObject();
        sendObject.put("relationName","userRelations");
        sendObject.put("content.follower.follower_user._id",userid);
        JSONObject gbr=new JSONObject();
        JSONArray f=new JSONArray();
        f.put("collection_content");
        f.put("content.follower");
        f.put("content.followed");
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
    ArrayList<FollowData> fds=new ArrayList<>();
    void bind(JSONObject j) throws JSONException {

        JSONArray values=j.getJSONArray("values");
        size=j.getInt("size");
        for(int i=0;i<values.length();++i){
            JSONObject p=values.getJSONObject(i);
            JSONObject cc=p.getJSONObject("collection_content");
            String user_id=cc.getString("_id");
            JSONObject content=cc.getJSONObject("content");
            String username=content.getString("user_username");
            JSONObject v_content=p.getJSONObject("content");
            boolean selfFollowed=false;
            String f_folowed_id="";
            String f_folower_id="";
            if(v_content!=null) {
                JSONArray followed = v_content.optJSONArray("follower");
                try {
                    f_folower_id = followed.getJSONObject(0).getString("_id");
                }catch (Exception ex){}
                //user bilgilerini al
                if (followed != null) {
                    for (int c = 0; c < followed.length(); ++c) {
                        JSONObject f_user = followed.optJSONObject(c);
                        try {
                            String f_userid = f_user.getJSONArray("follower_user").getJSONObject(0).getString("_id");
                            if (f_userid.equals(id)) {
                                selfFollowed = true;

                            }
                        }catch (Exception ex){}
                        try {
                           f_folowed_id = f_user.getJSONArray("follower_followed").getJSONObject(0).getString("_id");

                        }catch (Exception ex){}
                    }
                }
            }
            FollowData fd=new FollowData(user_id,username,"","",selfFollowed,true,f_folowed_id,f_folower_id);
            /*PostData pd = new PostData(user_id,username,time,String.valueOf(likeSize),
                    String.valueOf(commentSize),selfLiked,"","",postData,"text",post_id);
            fds.add(fd);*/
            fds.add(fd);
        }
        if(ndata) {
            fa = new followAdaptor(getContext(), fds);
            fa.setClickListener(new followAdaptor.ItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    String tag = view.getTag().toString();
                    if (tag.equals("follow")) {
                        TextView tw = view.findViewById(R.id.follow_text);
                        if (tw.getText().toString().toLowerCase().equals("follow")) {
                            tw.setText("Unfollow");
                            try {
                                uid = fds.get(position).getId();
                                pos = position;
                                addFollow();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            tw.setText("Follow");
                            String fd_id = fds.get(position).getFollowed_id();
                            String fr_id = fds.get(position).getFollower_id();
                            fds.get(position).setFollowed_id("");
                            fds.get(position).setFollower_id("");
                            removeFollow(fd_id);
                            removeFollow(fr_id);
                        }
                    } else {
                        String userid = fds.get(position).getId();
                        Intent i = new Intent(getContext(), UserProfile.class);
                        i.putExtra("userid", userid);
                        startActivity(i);
                    }
                }
            });
            rView.setAdapter(fa);
        }else{
            fa.notifyDataSetChanged();
        }
        isLoading=false;
    }
    String uid=null;
    int pos = -1;
    void addFollow() throws JSONException {
        JSONArray relations=new JSONArray();
        JSONArray contents=new JSONArray();
        JSONObject rel=new JSONObject();
        rel.put("relationName","userRelations");
        rel.put("id",id);
        relations.put(rel);
        JSONObject contentdata=new JSONObject();
        contentdata.put("collectionName","followed");
        JSONObject content =new JSONObject();
        JSONArray innerData=new JSONArray();
        JSONObject idata=new JSONObject();
        idata.put("relationName","userRelations");
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
        ac.addRelation(send_data ,null,hndfollowed,getActivity());
    }
    void addFollower(String f_id) throws JSONException {
        JSONArray relations=new JSONArray();
        JSONArray contents=new JSONArray();
        JSONObject rel=new JSONObject();
        rel.put("relationName","userRelations");
        rel.put("id",uid);
        relations.put(rel);
        JSONObject contentdata=new JSONObject();
        contentdata.put("collectionName","follower");
        JSONObject content =new JSONObject();
        JSONArray innerData=new JSONArray();
        JSONObject idata=new JSONObject();
        JSONObject idata2=new JSONObject();
        idata.put("relationName","userRelations");
        idata2.put("relationName","userRelations");
        idata.put("id",id);
        idata2.put("id",f_id);
        JSONArray fields=new JSONArray();
        JSONArray fields2=new JSONArray();
        fields.put("user_username");
        fields.put("user_image");
        fields2.put("_id");
        idata.put("fields",fields);
        idata2.put("fields",fields2);
        innerData.put(idata);
        innerData.put(idata2);
        contentdata.put("content",content);
        contentdata.put("innerData",innerData);
        contents.put(contentdata);
        JSONObject send_data=new JSONObject();
        send_data.put("relations",relations);
        send_data.put("contents",contents);
        ApiClass ac=new ApiClass();
        ac.addRelation(send_data ,null,hndfollower,getActivity());
    }
    void removeFollow(String id){
        try {
            statics.removeFollow(id,hndsend,getActivity());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    Handler hndsend =new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
        }
    };
    Handler hndfollowed =new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 1:
                    String sonuc=(String)msg.obj;
                    try {
                        JSONArray j=new JSONArray(sonuc);
                        String f_id=j.getJSONObject(0).getString("followed_id");
                        addFollower(f_id);
                        fds.get(pos).setFollowed_id(f_id);
                        add_notification(fds.get(pos).getId());
                        NotificationClass nc=new NotificationClass("New Follow","$ is following you now","",String.valueOf(R.drawable.notification_person));
                        PushNotifictionHelper.sendNotification(fds.get(pos).getId(),getActivity(),nc);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
                case 2:
                    Toast.makeText(getContext(), "Hata", Toast.LENGTH_SHORT).show();
            }
        }
    };
    void add_notification(String userID) throws JSONException {
        JSONArray relations=new JSONArray();
        JSONArray contents=new JSONArray();
        JSONObject relation=new JSONObject();
        relation.put("relationName","userRelations");
        relation.put("id",userID);
        relations.put(relation);
        JSONObject content=new JSONObject();
        JSONObject i_content=new JSONObject();
        content.put("collectionName","notification");
        i_content.put("notification_data","follow");
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

        innerData.put(i1);
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
    Handler hndfollower =new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 1:
                    String sonuc=(String)msg.obj;
                    try {
                        JSONArray j=new JSONArray(sonuc);
                        String f_id=j.getJSONObject(0).getString("follower_id");
                        fds.get(pos).setFollower_id(f_id);
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
}