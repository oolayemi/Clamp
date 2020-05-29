package com.stylet.clamp.Model;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

class CommentsId {
    @Exclude
    public String CommentsId;

    public <T extends CommentsId> T withId(@NonNull final String id) {
        this.CommentsId = id;
        return (T) this;
    }
}




/*
package com.stylet.clamp.Model;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

class CommentsId {
    @Exclude
    public String CommentsId;

    public <T extends CommentsId> T withId(@NonNull final String id) {
        this.CommentsId = id;
        return (T) this;
    }
}*/
