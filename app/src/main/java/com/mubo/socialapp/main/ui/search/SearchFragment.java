package com.mubo.socialapp.main.ui.search;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mubo.socialapp.R;
import com.mubo.socialapp.ShowImage;
import com.mubo.socialapp.UserProfile;
import com.mubo.socialapp.api.ApiClass;
import com.mubo.socialapp.api.NotificationClass;
import com.mubo.socialapp.api.PushNotifictionHelper;
import com.mubo.socialapp.helpers.Ayarlar;
import com.mubo.socialapp.helpers.DividerItemDecoration;
import com.mubo.socialapp.helpers.statics;
import com.mubo.socialapp.main.ui.home.PostData;
import com.mubo.socialapp.main.ui.home.postAdaptor;
import com.mubo.socialapp.main.ui.settings.FollowData;
import com.mubo.socialapp.main.ui.settings.followAdaptor;
import com.mubo.socialapp.single_post.SinglePost;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


public class SearchFragment extends Fragment {

    EditText search;
    RecyclerView postrec;
    TextView title;
    FrameLayout frm;
    searchAdaptor sa;
    ArrayList<SearchData> sds=new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        search=v.findViewById(R.id.inputSearch);
        postrec=v.findViewById(R.id.postrec);

        title=v.findViewById(R.id.title_text);
        frm=v.findViewById(R.id.frm);
        Ayarlar ayarlar=new Ayarlar(getContext());
        id=ayarlar.get_pref_string("id");
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        postrec.setLayoutManager(layoutManager);

        postrec.addItemDecoration(
                new DividerItemDecoration(getActivity().getDrawable(R.drawable.divider),
                        false, false));
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    statics.hideKeyboard(getActivity());
                    try {
                        getSearch();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });
        try {
            getSearch();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return v;
    }
    void getSearch() throws JSONException {
        a_size=0;
        b_size=0;
        sds.clear();
        String q="*"+search.getText().toString().trim()+"*";
        JSONObject searchh=new JSONObject();
        JSONArray search_fields=new JSONArray();
        JSONArray get_fields=new JSONArray();
        get_fields.put("collection_content");
        get_fields.put("content");
        search_fields.put("collection_content.content.post_data");
        search_fields.put("collection_content.content.user_username");
        searchh.put("search_fields",search_fields);
        searchh.put("from",0);
        searchh.put("size",20);
        searchh.put("query",q);
        searchh.put("get_fields",get_fields);
        JSONObject m=new JSONObject();
        m.put("search",searchh);
        ApiClass ac=new ApiClass();
        ac.search(m,null,s_hnd,getActivity());
    }
    Handler s_hnd=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 1:
                    String sonuc=(String)msg.obj;
                    try {
                        JSONObject j=new JSONObject(sonuc);
                        bindresult(j);
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
    int a_size=0;
    int b_size=0;
    void bindresult(JSONObject j) throws JSONException {
        JSONArray result=j.getJSONArray("result");
        JSONArray values=result.getJSONObject(0).getJSONArray("values");
        for(int i=0;i<values.length();++i){
            JSONObject p=values.getJSONObject(i);
            JSONObject cc=p.getJSONObject("collection_content");
            JSONObject content=cc.getJSONObject("content");
            String collectionName=cc.getString("collectionName");
            if(collectionName.equals("post")) {
                String post_id=cc.getString("_id");
                String postData = content.getString("post_data");
                String postType = content.getString("post_type");
                int commentSize = content.getInt("post_comment_size");
                int likeSize = content.getInt("post_like_size");
                long cDate = content.getLong("post_createdDate");
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String time = sdf.format(new Date(cDate));
                JSONObject v_content = p.getJSONObject("content");
                JSONArray pLikes = v_content.optJSONArray("postLike");
                JSONArray user = v_content.getJSONArray("user");
                String user_id = user.getJSONObject(0).getString("_id");
                JSONObject user_content = user.getJSONObject(0).getJSONObject("content");
                String username = user_content.getString("user_username");
                String userimage = user_content.getString("user_image");
                String like_id = "";
                boolean selfLiked = false;
                //user bilgilerini al
                if (pLikes != null) {
                    for (int c = 0; c < pLikes.length(); ++c) {
                        JSONObject pLike_user = pLikes.optJSONObject(c);
                        if (pLike_user != null) {
                            JSONArray jsonArray = pLike_user.optJSONArray("postLike_user");
                            if (jsonArray != null) {
                                JSONObject us = jsonArray.optJSONObject(0);
                                String plike_userid = us != null ? us.optString("_id") : "";
                                if (plike_userid.equals(id)) {
                                    like_id = pLike_user.optString("_id");
                                    if (like_id != null && !like_id.isEmpty())
                                        selfLiked = true;
                                    break;
                                }
                            }
                        }
                    }
                }
                PostData pd = new PostData(user_id, username, time, String.valueOf(likeSize),
                        String.valueOf(commentSize), selfLiked, userimage, "", postData, postType, post_id, like_id);
                SearchData sd=new SearchData(pd,null,"b");
                b_size++;
                sds.add(sd);
            }else{
                String user_id=cc.getString("_id");
                String username=content.getString("user_username");
                String userimage=content.getString("user_image");
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
                FollowData fd=new FollowData(user_id,username,"",userimage,selfFollowed,true,f_folowed_id,f_folower_id);
                SearchData sd=new SearchData(null,fd,"a");
                a_size++;
                sds.add(sd);
            }

        }
        Collections.sort(sds, new Comparator<SearchData>(){
            public int compare(SearchData o1, SearchData o2){
                return o1.getType().compareToIgnoreCase(o2.getType());
            }
        });
        sa=new searchAdaptor(getContext(),sds);
        sa.setClickListener(new searchAdaptor.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String tag=view.getTag().toString();
                if(position<=a_size)
                    position--;
                else if(position>a_size)
                    position-=2;
                SearchData sd=sds.get(position);
                PostData pd = sd.getPd();
                FollowData fd= sd.getFd();
                if(tag.equals("profile")) {
                    String userid = pd!=null?pd.getId():fd.getId();
                    Intent i = new Intent(getContext(), UserProfile.class);
                    i.putExtra("userid", userid);
                    startActivity(i);
                }else if(tag.equals("image")){
                    String url = pd.getContent();
                    Intent i = new Intent(getContext(), ShowImage.class);
                    i.putExtra("image", url);
                    startActivity(i);
                }else if(tag.equals("main")){
                    Intent i=null;
                    if(pd!=null) {
                        i = new Intent(getContext(), SinglePost.class);
                        i.putExtra("post", pd);
                    }else{
                        String userid = pd!=null?pd.getId():fd.getId();
                        i = new Intent(getContext(), UserProfile.class);
                        i.putExtra("userid", userid);
                    }
                    startActivity(i);
                }else if(tag.equals("follow")){
                    TextView tw=view.findViewById(R.id.follow_text);
                    if(tw.getText().toString().toLowerCase().equals("follow")){
                        tw.setText("Unfollow");
                        try {
                            uid=fd.getId();
                            pos=position;
                            addFollow();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else{
                        tw.setText("Follow");
                        String fd_id=fd.getFollowed_id();
                        String fr_id=fd.getFollower_id();
                        fd.setFollowed_id("");
                        fd.setFollower_id("");
                        removeFollow(fd_id);
                        removeFollow(fr_id);
                    }
                }else if(tag.equals("like")) {
                    postData = pd;
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
                        pd.setSelf_liked(false);
                        im.setImageDrawable(getContext().getDrawable(R.drawable.empty_heart));
                    } else {
                        ss++;
                        val=1;
                        try {
                            statics.addLike(postData.getPost_id(),id,p_hnd,getActivity());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        pd.setSelf_liked(true);
                        im.setImageDrawable(getContext().getDrawable(R.drawable.full_heart));
                    }
                    pd.setLike_size(String.valueOf(ss));
                    tw.setText(statics.setSize(String.valueOf(ss)));
                }
            }
        });

        List<SimpleSectionedRecyclerViewAdapter.Section> sections =
                new ArrayList<SimpleSectionedRecyclerViewAdapter.Section>();

        //Sections
        sections.add(new SimpleSectionedRecyclerViewAdapter.Section(0,"Users"));
        sections.add(new SimpleSectionedRecyclerViewAdapter.Section(a_size,"Posts"));
        SimpleSectionedRecyclerViewAdapter.Section[] dummy = new SimpleSectionedRecyclerViewAdapter.Section[sections.size()];
        mSectionedAdapter = new
                SimpleSectionedRecyclerViewAdapter(getContext(),R.layout.section,R.id.section_text,sa);
        mSectionedAdapter.setSections(sections.toArray(dummy));
        mSectionedAdapter.setClickListener(new SimpleSectionedRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.i("pos",String.valueOf(position));
            }
        });
        postrec.setAdapter(mSectionedAdapter);
    }
    SimpleSectionedRecyclerViewAdapter mSectionedAdapter;
    String id=null;
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
                        sds.get(pos).getFd().setFollowed_id(f_id);
                        add_notification(sds.get(pos).getFd().getId());
                        NotificationClass nc=new NotificationClass("New Follow","$ is following you now","",String.valueOf(R.drawable.notification_person));
                        PushNotifictionHelper.sendNotification(sds.get(pos).getFd().getId(),getActivity(),nc);
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
                        sds.get(pos).getFd().setFollower_id(f_id);
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
    void getPopularPosts() throws JSONException {
        JSONObject select=new JSONObject();
        JSONArray query_objects=new JSONArray();
        JSONArray fields=new JSONArray();
        JSONObject getByResult=new JSONObject();
        JSONArray gbr_query_objects=new JSONArray();
        JSONArray gbr_fields=new JSONArray();

        JSONObject q1=new JSONObject();
        q1.put("relationName","postRelations");
        query_objects.put(q1);
        fields.put("collection_content");
        fields.put("content.user");
        fields.put("content.postLike");
        select.put("query_objects",query_objects);
        select.put("fields",fields);
        select.put("from",0);
        select.put("size",10);
        select.put("sortBy","post_like_size");
        select.put("order","desc");

        JSONObject m=new JSONObject();
        m.put("select",select);
        ApiClass ac=new ApiClass();
        //ac.selectRelation(m,frm,hnd,getActivity());
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
                        }
                        statics.increase_like(postData.getPost_id(),"post_like_size",val,sizehnd,getActivity());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }
        }
    };
    Handler sizehnd=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
        }
    };
}