package com.mubo.socialapp.main.ui.search;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mubo.socialapp.R;
import com.mubo.socialapp.helpers.CircularTransform;
import com.mubo.socialapp.helpers.statics;
import com.mubo.socialapp.main.ui.home.PostData;
import com.mubo.socialapp.main.ui.settings.FollowData;
import com.squareup.picasso.Picasso;

import java.util.List;

public class searchAdaptor extends RecyclerView.Adapter<searchAdaptor.ViewHolder> {

    private List<SearchData> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context cx;
    View lastview = null;

    public searchAdaptor(Context context, List<SearchData> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.cx = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.search_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SearchData sd=mData.get(position);
        if(sd.getPd() != null) {
            holder.postlin.setVisibility(View.VISIBLE);
            holder.follow.setVisibility(View.GONE);
            PostData pd=sd.getPd();
            holder.name.setText(pd.getName());
            holder.time.setText(pd.getTime());
            String person_url = pd.getPerson();
            if (person_url.trim().isEmpty())
                holder.person.setImageDrawable(cx.getDrawable(R.mipmap.no_image));
            else
                Picasso.get().load(pd.getPerson()).transform(new CircularTransform()).resize(150,150).into(holder.person);
            String type = pd.getType();
            if (type.toLowerCase().equals("text")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    holder.content.setText(Html.fromHtml(pd.getContent(), Html.FROM_HTML_MODE_COMPACT));
                } else {
                    holder.content.setText(Html.fromHtml(pd.getContent()));
                }
                //holder.content.setText(pd.getContent());
                holder.image_content.setVisibility(View.GONE);
                holder.content.setVisibility(View.VISIBLE);
            } else {
                holder.image_content.setVisibility(View.VISIBLE);
                holder.content.setVisibility(View.GONE);
                Picasso.get().load(pd.getContent()).resize(720, 405).centerInside().into(holder.image_content);
            }
            holder.like_size.setText(statics.setSize(pd.getLike_size()));
            holder.comment_size.setText(statics.setSize(pd.getComment_size()));
            boolean self_like = pd.isSelf_liked();
            if (self_like) {
                holder.like.setImageDrawable(cx.getDrawable(R.drawable.full_heart));
                holder.like_size.setTextColor(cx.getColor(R.color.white));
            } else {
                holder.like.setImageDrawable(cx.getDrawable(R.drawable.empty_heart));
                holder.like_size.setTextColor(cx.getColor(R.color.white_30));
            }
        }else if(sd.getFd() !=null){
            holder.postlin.setVisibility(View.GONE);
            holder.follow.setVisibility(View.VISIBLE);
            FollowData fd=sd.getFd();
            holder.name.setText(fd.getName());
            String person_url=fd.getPerson();
            holder.time.setText(fd.getTime());
            if(person_url.trim().isEmpty())
                holder.person.setImageDrawable(cx.getDrawable(R.mipmap.no_image));
            else
                Picasso.get().load(fd.getPerson()).transform(new CircularTransform()).resize(150,150).into(holder.person);
            if(fd.isFollowed()){
                holder.follow_text.setText("Unfollow");
            }else{
                holder.follow_text.setText("Follow");
            }
        }
    }
    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name,time,content,like_size,comment_size,follow_text;
        ImageView person,image_content,like;
        LinearLayout postlin,profile,like_lin;
        RelativeLayout follow;
        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            time = itemView.findViewById(R.id.time);
            content = itemView.findViewById(R.id.content);
            like_size = itemView.findViewById(R.id.like_size);
            comment_size = itemView.findViewById(R.id.comment_size);
            like = itemView.findViewById(R.id.like);
            person = itemView.findViewById(R.id.person);
            image_content = itemView.findViewById(R.id.image_content);
            profile = itemView.findViewById(R.id.profile);
            like_lin = itemView.findViewById(R.id.like_lin);
            postlin = itemView.findViewById(R.id.postlin);
            follow_text = itemView.findViewById(R.id.follow_text);
            follow = itemView.findViewById(R.id.follow);
            follow.setTag("follow");
            profile.setTag("profile");
            like_lin.setTag("like");
            image_content.setTag("image");
            profile.setOnClickListener(this);
            like_lin.setOnClickListener(this);
            image_content.setOnClickListener(this);
            follow.setOnClickListener(this);
            itemView.setTag("main");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public void remove_lastview() {
        if (lastview != null) {
            lastview.setBackgroundColor(cx.getColor(R.color.white));
            lastview = null;
        }
    }

    SearchData getItem(int id) {
        return mData.get(id);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
