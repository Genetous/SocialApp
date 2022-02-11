package com.mubo.socialapp.main.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mubo.socialapp.R;
import com.mubo.socialapp.UserProfile;
import com.mubo.socialapp.api.ApiClass;
import com.mubo.socialapp.helpers.Ayarlar;
import com.mubo.socialapp.helpers.DividerItemDecoration;
import com.mubo.socialapp.helpers.PaginationListener;
import com.mubo.socialapp.helpers.statics;
import com.mubo.socialapp.main.ui.home.PostData;
import com.mubo.socialapp.main.ui.settings.FollowData;
import com.mubo.socialapp.main.ui.settings.followAdaptor;
import com.mubo.socialapp.single_post.CommentData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.mubo.socialapp.helpers.PaginationListener.PAGE_START;

public class NotificationsFragment extends Fragment {


    RecyclerView rView;
    String id=null;
    ArrayList<notificationData> nds=new ArrayList<>();
    notificationAdaptor na;
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    int itemcount=5;
    private boolean isLoading = false;
    int size=0;
    boolean ndata=true;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notifications, container, false);
        rView = v.findViewById(R.id.notification_rec);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rView.setLayoutManager(layoutManager);
        Ayarlar ayarlar=new Ayarlar(getContext());
        id=ayarlar.get_pref_string("id");
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

                    try {
                        int last_ind= nds.size();
                        ndata=false;
                        getNotifications( last_ind);
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
        try {
            ndata=true;
            getNotifications(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return v;
    }
    void getNotifications(int start) throws JSONException {
        if (ndata)
            nds.clear();
        JSONObject sendObject = new JSONObject();
        sendObject.put("relationName", "userRelations");
        sendObject.put("to", id);
        JSONArray f = new JSONArray();
        JSONObject noti = new JSONObject();
        JSONObject notiField = new JSONObject();

        JSONObject sort = new JSONObject();
        sort.put("field", "content.notification_createdDate");
        sort.put("type", "desc");
        noti.put("orderby", sort);
        noti.put("from", 0);
        noti.put("size", 10);
        notiField.put("content.notification", noti);
        f.put(notiField);
        sendObject.put("fields", f);

        ApiClass ac = new ApiClass();

        ac.getRelations(sendObject, null, hnd, getActivity());
           /* JSONObject select=new JSONObject();
            JSONArray query_objects=new JSONArray();
            JSONArray fields=new JSONArray();

            JSONObject q1=new JSONObject();
            q1.put("relationName","userRelations");
            query_objects.put(q1);
            JSONObject q2=new JSONObject();
            q2.put("to",id);
            query_objects.put(q2);
            fields.put("content.notification");
            select.put("query_objects",query_objects);
            select.put("operator","and");
            JSONArray filters=new JSONArray();
            JSONObject fo=new JSONObject();
            fo.put("content.notification.collectionName","notification");
            select.put("fields",fields);
            fo.put("from",start);
            fo.put("size",itemcount);
            fo.put("sortBy","content.notification_createdDate");
            fo.put("order","desc");
            filters.put(fo);
            select.put("filters",filters);
            JSONObject m=new JSONObject();
            m.put("select",select);
            ApiClass ac=new ApiClass();
            ac.selectRelation(m,null,hnd,getActivity());*/
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
    void bind(JSONObject j) throws JSONException {
        JSONArray values=j.getJSONArray("values");
        size=j.getInt("size");
        for(int i=0;i<values.length();++i) {
            JSONObject j_rel = values.getJSONObject(i);
            JSONObject n = j_rel.getJSONObject("content").getJSONObject("notification");


            PostData pd = null;
            CommentData cd = null;
            String n_id = n.getString("_id");
            JSONObject v_content = n.getJSONObject("content");
            String type = v_content.getString("notification_data");

            long cDate = v_content.getLong("notification_createdDate");
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String time = sdf.format(new Date(cDate));
            JSONObject user = n.getJSONArray("notification_user").getJSONObject(0);
            String userid = user.getString("_id");
            JSONObject u_content = user.getJSONObject("content");
            String username = u_content.getString("user_username");
            String person = u_content.getString("user_image");
            if (type.equals("like") || type.equals("comment")) {
                JSONObject post = n.getJSONArray("notification_post").getJSONObject(0);
                String postid = post.getString("_id");
                JSONObject p_content = post.getJSONObject("content");
                String post_data = p_content.getString("post_data");
                String post_type = p_content.getString("post_type");
                long pDate = p_content.getLong("post_createdDate");
                String ptime = sdf.format(new Date(pDate));
                pd = new PostData(id, "", ptime, "0", "0",
                        true, "", "", post_data, post_type, postid, "");
            }
            if (type.equals("comment")) {
                JSONObject post = n.getJSONArray("notification_comment").getJSONObject(0);
                String cid = post.getString("_id");
                JSONObject p_content = post.getJSONObject("content");
                String c_data = p_content.getString("comment_data");
                long pDate = p_content.getLong("comment_createdDate");
                String ctime = sdf.format(new Date(pDate));
                cd = new CommentData(userid, username, ctime, person, c_data, cid);
            }
            String d_content = "";
            switch (type) {
                case "follow":
                    d_content = username + " started following you";
                    break;
                case "like":
                    d_content = username + " liked your post";
                    break;
                case "comment":
                    d_content = username + " commented on your post";
                    break;
            }
            notificationData nd = new notificationData(n_id, type, d_content, time, cd, pd, person, username, userid);
            nds.add(nd);

        }
        if(ndata) {
            na = new notificationAdaptor(getContext(), nds);
            rView.setAdapter(na);
        }else{
            na.notifyDataSetChanged();
        }
        isLoading=false;
    }
}