package com.stylet.clamp.Model;

public class User extends UserId {

    private String userId;
    private String username, userimage, useremail;


    public User(String username, String userimage, String useremail, String userId) {

        this.username = username;
        this.userimage = userimage;
        this.useremail = useremail;
        this.userId = userId;
    }

    public User() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getUseremail() {
        return useremail;
    }

    public void setUseremail(String useremail) {
        this.useremail = useremail;
    }
}
