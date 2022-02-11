package com.mubo.socialapp.main.ui.home;

import android.os.Parcel;
import android.os.Parcelable;

public class PostData implements Parcelable {
    private String id;
    private String name;
    private String time;
    private String like_size;
    private String comment_size;
    private boolean self_liked;
    private String person;
    private String content;
    private String type;
    private String post_id;
    private String like_id;

    public PostData(String id, String name, String time, String like_size,
                    String comment_size, boolean self_liked, String person,
                    String image_content, String content,String type,String post_id,String like_id) {
        this.id=id;
        this.name = name;
        this.time = time;
        this.like_size = like_size;
        this.comment_size = comment_size;
        this.self_liked = self_liked;
        this.person = person;
        this.content = content;
        this.type = type;
        this.post_id=post_id;
        this.like_id=like_id;
    }

    protected PostData(Parcel in) {
        id = in.readString();
        name = in.readString();
        time = in.readString();
        like_size = in.readString();
        comment_size = in.readString();
        self_liked = in.readByte() != 0;
        person = in.readString();
        content = in.readString();
        type = in.readString();
        post_id = in.readString();
        like_id = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(time);
        dest.writeString(like_size);
        dest.writeString(comment_size);
        dest.writeByte((byte) (self_liked ? 1 : 0));
        dest.writeString(person);
        dest.writeString(content);
        dest.writeString(type);
        dest.writeString(post_id);
        dest.writeString(like_id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PostData> CREATOR = new Creator<PostData>() {
        @Override
        public PostData createFromParcel(Parcel in) {
            return new PostData(in);
        }

        @Override
        public PostData[] newArray(int size) {
            return new PostData[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLike_size() {
        return like_size;
    }

    public void setLike_size(String like_size) {
        this.like_size = like_size;
    }

    public String getComment_size() {
        return comment_size;
    }

    public void setComment_size(String comment_size) {
        this.comment_size = comment_size;
    }

    public boolean isSelf_liked() {
        return self_liked;
    }

    public void setSelf_liked(boolean self_liked) {
        this.self_liked = self_liked;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getLike_id() {
        return like_id;
    }

    public void setLike_id(String like_id) {
        this.like_id = like_id;
    }
}
