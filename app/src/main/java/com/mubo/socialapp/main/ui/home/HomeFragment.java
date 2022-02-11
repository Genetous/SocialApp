package com.mubo.socialapp.main.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.mubo.socialapp.R;
import com.mubo.socialapp.ShowImage;
import com.mubo.socialapp.UserProfile;
import com.mubo.socialapp.api.ApiClass;
import com.mubo.socialapp.api.NotificationClass;
import com.mubo.socialapp.api.PushNotifictionHelper;
import com.mubo.socialapp.helpers.Ayarlar;
import com.mubo.socialapp.helpers.DividerItemDecoration;
import com.mubo.socialapp.helpers.PaginationListener;
import com.mubo.socialapp.helpers.statics;
import com.mubo.socialapp.main.Home;
import com.mubo.socialapp.single_post.SinglePost;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.mubo.socialapp.helpers.PaginationListener.PAGE_START;

public class HomeFragment extends Fragment {


    RecyclerView rView;
    FrameLayout frm;
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    int itemcount=5;
    private boolean isLoading = false;
    ProgressBar pb;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home, container, false);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rView = v.findViewById(R.id.post_list);
        pb = v.findViewById(R.id.progressBar);
        rView.setLayoutManager(layoutManager);
        rView.addItemDecoration(
                new DividerItemDecoration(getActivity().getDrawable(R.drawable.divider),
                        false, false));
        rView.addOnScrollListener(new PaginationListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage++;
                if(currentPage<size) {
                    isLoading=true;
                    pb.setVisibility(View.VISIBLE);
                    try {
                        int last_ind=statics.home_data.size();
                        getPosts(false, last_ind, itemcount);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public boolean isLastPage() {
                return isLastPage;
            }
            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
        frm=v.findViewById(R.id.frm);
        Ayarlar ayarlar=new Ayarlar(getContext());
        id=ayarlar.get_pref_string("id");
        try {
            getPosts(true,0,itemcount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            statics.updateToken(getActivity(), id);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return v;
    }
    String id=null;
    int size=0;
    boolean ndata=true;
    public void getPosts(boolean newData,int start,int count) throws JSONException {
        if(newData)
            statics.home_data.clear();
        ndata=newData;

        JSONObject sendObject=new JSONObject();
        //And fields in query
        sendObject.put("relationName","userRelations");
        sendObject.put("to",id);

        // "getByResult" allows the data of the fields to be used from the data
        // obtained as a result of the first query to be used as an "or" query.

        // Depending on the result, you can create a new query.
        JSONObject gbr=new JSONObject();
        gbr.put("relationName","postRelations");
        gbr.put("from",start);
        gbr.put("size",count);
        gbr.put("content.user._id", "$content.follower.follower_user._id");
        JSONObject sort=new JSONObject();
        sort.put("field","collection_content.content.post_createdDate");
        sort.put("type","desc");
        gbr.put("orderby",sort);

        // Genetous stores deep nested data.
        // You must specify the specific fields you want in the queries.
        JSONArray f=new JSONArray();
        f.put("collection_content");
        f.put("content.user");
        f.put("content.postLike");
        gbr.put("fields",f);
        sendObject.put("getByResult",gbr);

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
                        System.out.println(j.toString());
                        bind(j);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
                case 2:
                    Toast.makeText(getContext(), "Hata", Toast.LENGTH_SHORT).show();
            }
        }
    };


    void bind(JSONObject j) throws JSONException {
        JSONArray values=j.getJSONArray("values");
        size=j.getInt("size");
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
        if(ndata) {
            statics.home_adaptor = new postAdaptor(getContext(), statics.home_data);
            statics.home_adaptor.setClickListener(new postAdaptor.ItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    String tag=view.getTag().toString();
                    if(tag.equals("profile")) {
                        String userid = statics.home_data.get(position).getId();
                        Intent i = new Intent(getContext(), UserProfile.class);
                        i.putExtra("userid", userid);
                        startActivity(i);
                    }else if(tag.equals("main")){
                        PostData postData = statics.home_data.get(position);
                        Intent i = new Intent(getContext(), SinglePost.class);
                        i.putExtra("post", postData);
                        startActivity(i);
                    }else if(tag.equals("image")){
                        String url = statics.home_data.get(position).getContent();
                        Intent i = new Intent(getContext(), ShowImage.class);
                        i.putExtra("image", url);
                        startActivity(i);
                    }else if(tag.equals("like")) {
                        postData = statics.home_data.get(position);
                        ImageView im = view.findViewById(R.id.like);
                        TextView tw = view.findViewById(R.id.like_size);
                        String s=postData.getLike_size();
                        long ss=Long.parseLong(s);
                        boolean selfLiked = postData.isSelf_liked();
                        if (selfLiked) {
                            ss--;;
                            val=-1;
                            try {
                                statics.removeLike(postData.getLike_id(),p_hnd,getActivity());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            postData.setSelf_liked(false);
                            im.setImageDrawable(getContext().getDrawable(R.drawable.empty_heart));
                        } else {
                            ss++;
                            val=1;
                            try {
                                statics.addLike(postData.getPost_id(),id,p_hnd,getActivity());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            postData.setSelf_liked(true);
                            im.setImageDrawable(getContext().getDrawable(R.drawable.full_heart));
                        }
                        postData.setLike_size(String.valueOf(ss));
                        tw.setText(statics.setSize(String.valueOf(ss)));
                    }
                }
            });
            rView.setAdapter(statics.home_adaptor);
        }else {
            statics.home_adaptor.notifyDataSetChanged();
        }
        isLoading=false;
        pb.setVisibility(View.GONE);
        statics.PostAdaptorList[0]=statics.home_adaptor;
    }
    PostData postData;
    int val=0;
    Handler p_hnd=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 1:
                    try {
                        if(val>0) {
                            JSONArray j = new JSONArray((String) msg.obj);
                            String like_id=j.getJSONObject(0).getString("postLike_id");
                            postData.setLike_id(like_id);
                            add_notification(postData.getPost_id(),postData.getId());
                            NotificationClass nc=new NotificationClass("New Like","$ liked your post","",String.valueOf(R.drawable.full_heart));
                            PushNotifictionHelper.sendNotification(postData.getId(),getActivity(),nc);
                        }else{
                            postData.setLike_id("");
                        }
                        statics.updateOthers(postData);
                        statics.increase_like(postData.getPost_id(),"post_like_size",val,sizehnd,getActivity());
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {


    }
}