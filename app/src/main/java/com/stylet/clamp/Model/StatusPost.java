package com.stylet.clamp.Model;

import com.google.firebase.Timestamp;

public class StatusPost extends StatusPostId {

    public String username, user_image, desc, image_url, user_id;
    private Timestamp timeset;

    public StatusPost(String username, String user_image, String user_id, String image_url, String desc, Timestamp timeset) {
        this.image_url = image_url;
        this.username = username;
        this.user_id = user_id;
        this.user_image = user_image;
        this.desc = desc;
        this.timeset = timeset;
    }

    public StatusPost() {
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Timestamp getTimeset() {
        return timeset;
    }

    public void setTimeset(Timestamp timeset) {
        this.timeset = timeset;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


}





/*
package com.stylet.clamp.Model;

import com.google.firebase.Timestamp;

public class StatusPost extends StatusPostId {

    public String username, user_image, desc, image_url, user_id;
    private Timestamp timeset;

    public StatusPost(String username, String user_image, String user_id, String image_url, String desc, Timestamp timeset) {
        this.image_url = image_url;
        this.username = username;
        this.user_id = user_id;
        this.user_image = user_image;
        this.desc = desc;
        this.timeset = timeset;
    }

    public StatusPost() {
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Timestamp getTimeset() {
        return timeset;
    }

    public void setTimeset(Timestamp timeset) {
        this.timeset = timeset;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


}
*/
