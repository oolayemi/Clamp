package com.stylet.clamp.Model;

import java.util.Date;

public class Comments extends CommentsId {

    private String message;
    private String blog_id;
    private String user_id;
    private String name;
    private String image;
    private Date timestamp;

    public Comments(){

    }



    public Comments(String message, String user_id, String name, String blog_id, String image, Date timestamp) {
        this.blog_id = blog_id;
        this.message = message;
        this.user_id = user_id;
        this.timestamp = timestamp;
        this.name = name;
        this.image = image;
    }

    public String getBlog_id() {
        return blog_id;
    }

    public void setBlog_id(String blog_id) {
        this.blog_id = blog_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}








/*
package com.stylet.clamp.Model;

import java.util.Date;

public class Comments extends CommentsId {

    private String message;
    private String blog_id;
    private String user_id;
    private String name;
    private String image;
    private Date timestamp;

    public Comments(){

    }



    public Comments(String message, String user_id, String name, String blog_id, String image, Date timestamp) {
        this.blog_id = blog_id;
        this.message = message;
        this.user_id = user_id;
        this.timestamp = timestamp;
        this.name = name;
        this.image = image;
    }

    public String getBlog_id() {
        return blog_id;
    }

    public void setBlog_id(String blog_id) {
        this.blog_id = blog_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
*/
