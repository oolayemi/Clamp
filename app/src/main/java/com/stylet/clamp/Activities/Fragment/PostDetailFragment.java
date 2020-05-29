package com.stylet.clamp.Activities.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.stylet.clamp.Adapters.BlogRecyclerAdapter;
import com.stylet.clamp.Model.BlogPost;
import com.stylet.clamp.R;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class PostDetailFragment extends Fragment {

    private String postid;
    private RecyclerView recyclerView;
    private BlogRecyclerAdapter blogRecyclerAdapter;
    private List<BlogPost> blogPostList;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_post_detail, container, false);

        SharedPreferences preferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);

        postid = preferences.getString("postid", "none");

        recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        blogPostList = new ArrayList<>();
        blogRecyclerAdapter = new BlogRecyclerAdapter(blogPostList);
        recyclerView.setAdapter(blogRecyclerAdapter);

        readPost();

        return root;
    }

    private void readPost() {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Posts").document(postid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                blogPostList.clear();
                BlogPost blogPost = documentSnapshot.toObject(BlogPost.class);
                blogPostList.add(blogPost);

                blogRecyclerAdapter.notifyDataSetChanged();

            }
        });
    }
}