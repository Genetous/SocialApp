package com.mubo.socialapp.main.ui.home;

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
import com.squareup.picasso.Picasso;

import java.util.List;

public class postAdaptor extends RecyclerView.Adapter<postAdaptor.ViewHolder> {

    private List<PostData> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context cx;
    View lastview = null;

    public postAdaptor(Context context, List<PostData> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.cx = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.post_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.name.setText(mData.get(position).getName());
        holder.time.setText(mData.get(position).getTime());
        String person_url=mData.get(position).getPerson();
        if(person_url.trim().isEmpty())
            holder.person.setImageDrawable(cx.getDrawable(R.mipmap.no_image));
        else
            Picasso.get().load(mData.get(position).getPerson()).transform(new CircularTransform()).resize(150,150).into(holder.person);
        String type=mData.get(position).getType();
        if(type.toLowerCase().equals("text")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.content.setText(Html.fromHtml(mData.get(position).getContent(), Html.FROM_HTML_MODE_COMPACT));
            } else {
                holder.content.setText(Html.fromHtml(mData.get(position).getContent()));
            }
            //holder.content.setText(mData.get(position).getContent());
            holder.image_content.setVisibility(View.GONE);
            holder.content.setVisibility(View.VISIBLE);
        }else{
            holder.image_content.setVisibility(View.VISIBLE);
            holder.content.setVisibility(View.GONE);
            Picasso.get().load(mData.get(position).getContent()).resize(720,405).centerInside().into(holder.image_content);
        }
        holder.like_size.setText(statics.setSize(mData.get(position).getLike_size()));
        holder.comment_size.setText(statics.setSize(mData.get(position).getComment_size()));
        boolean self_like=mData.get(position).isSelf_liked();
        if(self_like) {
            holder.like.setImageDrawable(cx.getDrawable(R.drawable.full_heart));
            holder.like_size.setTextColor(cx.getColor(R.color.white));
        }else {
            holder.like.setImageDrawable(cx.getDrawable(R.drawable.empty_heart));
            holder.like_size.setTextColor(cx.getColor(R.color.white_30));
        }
    }
    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name,time,content,like_size,comment_size;
        ImageView person,image_content,like;
        RelativeLayout rel;
        LinearLayout profile,like_lin;

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
            profile.setTag("profile");
            like_lin.setTag("like");
            image_content.setTag("image");
            profile.setOnClickListener(this);
            like_lin.setOnClickListener(this);
            image_content.setOnClickListener(this);
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

    PostData getItem(int id) {
        return mData.get(id);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

}
