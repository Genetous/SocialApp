package com.mubo.socialapp.single_post;

public class CommentData {
    private String id;
    private String name;
    private String time;
    private String person;
    private String content;
    private String comment_id;

    public CommentData(String id, String name, String time, String person, String content, String comment_id) {
        this.id = id;
        this.name = name;
        this.time = time;
        this.person = person;
        this.content = content;
        this.comment_id = comment_id;
    }

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

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }
}
