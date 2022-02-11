package com.mubo.socialapp.main.ui.search;

import com.mubo.socialapp.main.ui.home.PostData;
import com.mubo.socialapp.main.ui.settings.FollowData;

public class SearchData {
    private PostData pd;
    private FollowData fd;
    private String type;

    public SearchData(PostData pd, FollowData fd, String type) {
        this.pd = pd;
        this.fd = fd;
        this.type = type;
    }

    public PostData getPd() {
        return pd;
    }

    public void setPd(PostData pd) {
        this.pd = pd;
    }

    public FollowData getFd() {
        return fd;
    }

    public void setFd(FollowData fd) {
        this.fd = fd;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
