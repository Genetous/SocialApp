package com.mubo.socialapp.main.ui.notifications;

import com.mubo.socialapp.main.ui.home.PostData;
import com.mubo.socialapp.single_post.CommentData;

public class notificationData {
    private String id;
    private String type;
    private String content;
    private String time;
    private CommentData comment;
    private PostData post;
    private String person;
    private String username;
    private String userid;

    public notificationData(String id, String type, String content, String time,
                            CommentData comment, PostData post, String person, String username, String userid) {
        this.id = id;
        this.type = type;
        this.content = content;
        this.time = time;
        this.comment = comment;
        this.post = post;
        this.person = person;
        this.username = username;
        this.userid = userid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public CommentData getComment() {
        return comment;
    }

    public void setComment(CommentData comment) {
        this.comment = comment;
    }

    public PostData getPost() {
        return post;
    }

    public void setPost(PostData post) {
        this.post = post;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
