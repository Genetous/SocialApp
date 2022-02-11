package com.mubo.socialapp.single_post;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mubo.socialapp.R;
import com.mubo.socialapp.main.ui.home.PostData;
import com.mubo.socialapp.main.ui.home.postAdaptor;
import com.squareup.picasso.Picasso;

import java.util.List;

public class commentAdaptor extends RecyclerView.Adapter<commentAdaptor.ViewHolder> {

    private List<CommentData> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context cx;
    View lastview = null;

    public commentAdaptor(Context context, List<CommentData> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.cx = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.comment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.name.setText(mData.get(position).getName());
        holder.time.setText(mData.get(position).getTime());
        holder.content.setText(mData.get(position).getContent());
        String person_url=mData.get(position).getPerson();
        if(person_url.trim().isEmpty())
            holder.person.setImageDrawable(cx.getDrawable(R.mipmap.no_image));
        else
            Picasso.get().load(mData.get(position).getPerson()).resize(150,150).into(holder.person);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name, time, content, like_size, comment_size;
        ImageView person, image_content, like;
        RelativeLayout rel;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            time = itemView.findViewById(R.id.time);
            content = itemView.findViewById(R.id.content);
            person = itemView.findViewById(R.id.person);


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

    CommentData getItem(int id) {
        return mData.get(id);
    }

    public void setClickListener(commentAdaptor.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

}
