package com.mubo.socialapp.api;

public class NotificationClass {
    private String title;
    private String body;
    private String image;
    private String icon_id;
    private String token;

    public NotificationClass(String title, String body, String image, String icon_id) {
        this.title = title;
        this.body = body;
        this.image = image;
        this.icon_id = icon_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIcon_id() {
        return icon_id;
    }

    public void setIcon_id(String icon_id) {
        this.icon_id = icon_id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
