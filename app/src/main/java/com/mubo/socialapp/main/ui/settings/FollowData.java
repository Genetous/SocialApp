package com.mubo.socialapp.main.ui.settings;

public class FollowData {
    private String id;
    private String name;
    private String time;
    private String person;
    private boolean followed;
    private boolean visible;
    private String followed_id;
    private String follower_id;

    public FollowData(String id, String name, String time, String person, boolean followed, boolean visible, String followed_id, String follower_id) {
        this.id = id;
        this.name = name;
        this.time = time;
        this.person = person;
        this.followed = followed;
        this.visible = visible;
        this.followed_id = followed_id;
        this.follower_id = follower_id;
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

    public boolean isFollowed() {
        return followed;
    }

    public void setFollowed(boolean followed) {
        this.followed = followed;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getFollowed_id() {
        return followed_id;
    }

    public void setFollowed_id(String followed_id) {
        this.followed_id = followed_id;
    }

    public String getFollower_id() {
        return follower_id;
    }

    public void setFollower_id(String follower_id) {
        this.follower_id = follower_id;
    }
}
