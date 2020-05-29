package com.stylet.clamp.Model;

import java.util.Date;

public class RepliedComments extends CommentsId{

    private String message;
    private String user_id;
    private String name;
    private String image;
    private Date timestamp;

    public RepliedComments(){

    }



    public RepliedComments(String message, String user_id, String name, String image, Date timestamp) {
        this.message = message;
        this.user_id = user_id;
        this.name = name;
        this.image = image;
        this.timestamp = timestamp;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

}






/*
package com.stylet.clamp.Model;

import java.util.Date;

public class RepliedComments extends CommentsId{

    private String message;
    private String user_id;
    private String name;
    private String image;
    private Date timestamp;

    public RepliedComments(){

    }



    public RepliedComments(String message, String user_id, String name, String image, Date timestamp) {
        this.message = message;
        this.user_id = user_id;
        this.name = name;
        this.image = image;
        this.timestamp = timestamp;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

}
*/
