package com.mubo.socialapp.main.ui.notifications;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mubo.socialapp.R;
import com.mubo.socialapp.helpers.CircularTransform;
import com.squareup.picasso.Picasso;

import java.util.List;

public class notificationAdaptor extends RecyclerView.Adapter<notificationAdaptor.ViewHolder> {

    private List<notificationData> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context cx;
    View lastview = null;

    public notificationAdaptor(Context context, List<notificationData> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.cx = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.notification_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.time.setText(mData.get(position).getTime());
        holder.content.setText(mData.get(position).getContent());
        String type=mData.get(position).getType();
        if(type.equals("follow")) {
            holder.follow.setVisibility(View.GONE);
            holder.post.setVisibility(View.GONE);
            holder.comment.setVisibility(View.GONE);
            holder.content.setText(mData.get(position).getContent());
            holder.type.setText("New Follower");
            String p_url = mData.get(position).getPerson();
            if (p_url != null && !p_url.trim().isEmpty()) {
                Picasso.get().load(p_url).transform(new CircularTransform()).resize(150,150).into(holder.person);
            } else {
                holder.person.setImageDrawable(cx.getDrawable(R.mipmap.no_image));
            }
            TextView ftx = holder.follow.findViewById(R.id.follow_text);
              /*  if(mData.get(position).isFollowed())
                    ftx.setText("Unfollow");
                else
                    ftx.setText("Follow");*/
            holder.icon.setImageDrawable(cx.getDrawable(R.drawable.notification_person));
        }
        else if(type.equals("like")) {
            holder.type.setText("New Like");
            holder.like_com.setVisibility(View.GONE);
            holder.profile.setVisibility(View.GONE);
            holder.follow.setVisibility(View.GONE);
            holder.post.setVisibility(View.VISIBLE);
            holder.comment.setVisibility(View.GONE);
            String p_url = mData.get(position).getPerson();
            if (p_url != null && !p_url.trim().isEmpty()) {
                Picasso.get().load(p_url).transform(new CircularTransform()).resize(150,150).into(holder.person);
            } else {
                holder.person.setImageDrawable(cx.getDrawable(R.mipmap.no_image));
            }
            TextView pcontent = holder.post.findViewById(R.id.content);
            ImageView pimage_content = holder.post.findViewById(R.id.image_content);
            String ptype = mData.get(position).getPost().getType();
            if (ptype.toLowerCase().equals("text")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    pcontent.setText(Html.fromHtml(mData.get(position).getPost().getContent(), Html.FROM_HTML_MODE_COMPACT));
                } else {
                    pcontent.setText(Html.fromHtml(mData.get(position).getPost().getContent()));
                }
                pimage_content.setVisibility(View.GONE);
                pcontent.setVisibility(View.VISIBLE);
            } else {
                pimage_content.setVisibility(View.VISIBLE);
                pcontent.setVisibility(View.GONE);
                Picasso.get().load(mData.get(position).getPost().getContent()).resize(720,405).centerCrop().into(pimage_content);
            }
            holder.icon.setImageDrawable(cx.getDrawable(R.drawable.full_heart));

        }
        else if(type.equals("comment")){
            holder.comment.setVisibility(View.VISIBLE);
            holder.type.setText("New Comment");
            holder.like_com.setVisibility(View.GONE);
            holder.profile.setVisibility(View.GONE);
            holder.follow.setVisibility(View.GONE);
            holder.post.setVisibility(View.VISIBLE);
            String p_url = mData.get(position).getPerson();
            if (p_url != null && !p_url.trim().isEmpty()) {
                Picasso.get().load(p_url).transform(new CircularTransform()).resize(150,150).into(holder.person);
            } else {
                holder.person.setImageDrawable(cx.getDrawable(R.mipmap.no_image));
            }
            TextView pcontent = holder.post.findViewById(R.id.content);
            ImageView pimage_content = holder.post.findViewById(R.id.image_content);
            String ptype = mData.get(position).getPost().getType();
            if (ptype.toLowerCase().equals("text")) {
                pcontent.setText(mData.get(position).getContent());
                pimage_content.setVisibility(View.GONE);
                pcontent.setVisibility(View.VISIBLE);
            } else {
                pimage_content.setVisibility(View.VISIBLE);
                pcontent.setVisibility(View.GONE);
                Picasso.get().load(mData.get(position).getPost().getContent()).resize(720,405).centerCrop().into(pimage_content);
            }
            holder.comment.setText(mData.get(position).getComment().getContent());
            holder.icon.setImageDrawable(cx.getDrawable(R.drawable.notification_comment));
        }
    }
    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView time,content,type,comment;
        ImageView person,icon;
        RelativeLayout follow;
        LinearLayout like_com,profile;
        View post;

        ViewHolder(View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.type);
            time = itemView.findViewById(R.id.time);
            content = itemView.findViewById(R.id.content);
            comment = itemView.findViewById(R.id.comment);
            icon = itemView.findViewById(R.id.icon);
            person = itemView.findViewById(R.id.person);
            follow = itemView.findViewById(R.id.follow);
            post = itemView.findViewById(R.id.post);
            like_com = itemView.findViewById(R.id.like_com);
            profile = itemView.findViewById(R.id.profile);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }
    }

    public void remove_lastview() {
        if (lastview != null) {
            lastview.setBackgroundColor(cx.getColor(R.color.white));
            lastview = null;
        }
    }

    notificationData getItem(int id) {
        return mData.get(id);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
