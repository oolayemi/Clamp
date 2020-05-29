package com.stylet.clamp.Model;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class UserId {

    @Exclude
    public String UserId;

    public <T extends UserId> T withId(@NonNull final String id) {
        this.UserId = id;
        return (T) this;
    }

}




/*
package com.stylet.clamp.Model;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class BlogPostId {

    @Exclude
    public String BlogPostId;

    public <T extends BlogPostId> T withId(@NonNull final String id) {
        this.BlogPostId = id;
        return (T) this;
    }

}
*/
