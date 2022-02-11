package com.mubo.socialapp.main.ui.settings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mubo.socialapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class followAdaptor extends RecyclerView.Adapter<followAdaptor.ViewHolder> {

    private List<FollowData> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context cx;
    View lastview = null;

    public followAdaptor(Context context, List<FollowData> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.cx = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.follow_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.name.setText(mData.get(position).getName());
        String person_url=mData.get(position).getPerson();
        if(person_url.trim().isEmpty())
            holder.person.setImageDrawable(cx.getDrawable(R.mipmap.no_image));
        else
            Picasso.get().load(mData.get(position).getPerson()).resize(150,150).into(holder.person);
        if(mData.get(position).isFollowed()){
            holder.follow_text.setText("Unfollow");
        }else{
            holder.follow_text.setText("Follow");
        }
    }
    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name,follow_text;
        ImageView person,image_content,like;
        RelativeLayout follow;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            person = itemView.findViewById(R.id.person);
            follow_text = itemView.findViewById(R.id.follow_text);
            follow = itemView.findViewById(R.id.follow);
            follow.setTag("follow");
            itemView.setTag("main");
            follow.setOnClickListener(this);
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

    FollowData getItem(int id) {
        return mData.get(id);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
