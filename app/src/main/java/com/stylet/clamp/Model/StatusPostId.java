package com.stylet.clamp.Model;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class StatusPostId {

    @Exclude
    public String StatusPostId;

    public <T extends StatusPostId> T withId(@NonNull final String id) {
        this.StatusPostId = id;
        return (T) this;
    }
}




/*
package com.stylet.clamp.Model;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class StatusPostId {

    @Exclude
    public String StatusPostId;

    public <T extends StatusPostId> T withId(@NonNull final String id) {
        this.StatusPostId = id;
        return (T) this;
    }
}
*/
