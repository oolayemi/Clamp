package com.stylet.clamp.Model;

public class SuggessFollowers {

    private String username, userimage, user_id;

    public SuggessFollowers(String username, String userimage, String user_id) {
        this.username = username;
        this.userimage = userimage;
        this.user_id = user_id;
    }

    public SuggessFollowers() {
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserimage() {
        return userimage;
    }

    public void setUserimage(String userimage) {
        this.userimage = userimage;
    }
}
