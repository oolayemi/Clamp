package com.stylet.clamp.Activities.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.stylet.clamp.Activities.MainActivity;
import com.stylet.clamp.Activities.NewPostActivity;
import com.stylet.clamp.Activities.NewStatusActivity;
import com.stylet.clamp.Adapters.BlogRecyclerAdapter;
import com.stylet.clamp.Adapters.StatusRecyclerAdapter;
import com.stylet.clamp.Adapters.SuggestFollowersAdapter;
import com.stylet.clamp.Model.BlogPost;
import com.stylet.clamp.Model.StatusPost;
import com.stylet.clamp.Model.User;
import com.stylet.clamp.R;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {

    private List<BlogPost> blog_list;
    private List<StatusPost> status_list;
    private List<String> followingList;
    private List<String> followerList;
    private List<User> suggest_followers_list;

    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    private BlogRecyclerAdapter blogRecyclerAdapter;
    private StatusRecyclerAdapter statusRecyclerAdapter;
    private SuggestFollowersAdapter suggestFollowersAdapter;

    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad = true;
    private Boolean isFirstStatusFirstLoad = true;

    private FirebaseFirestore firebaseFirestore;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        CircleImageView toolbar_userimage = view.findViewById(R.id.toolbar_userimage);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        user = firebaseAuth.getCurrentUser();


        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        //firebaseFirestore.setFirestoreSettings(settings);

        Glide.with(getContext()).load(user.getPhotoUrl()).into(toolbar_userimage);

        FloatingActionButton fab2 = view.findViewById(R.id.add_status_btn);
        fab2.setVisibility(View.INVISIBLE);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent newStatus = new Intent(getActivity(), NewStatusActivity.class);
                startActivity(newStatus);

            }
        });

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent newPost = new Intent(getActivity(), NewPostActivity.class);
                startActivity(newPost);

                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });


        blog_list = new ArrayList<>();
        RecyclerView blog_list_view = view.findViewById(R.id.blog_list_view);

        status_list = new ArrayList<>();
        final RecyclerView status_list_view = view.findViewById(R.id.status_list_view);

        suggest_followers_list = new ArrayList<>();
        RecyclerView suggest_list_view = view.findViewById(R.id.suggest_list_view);

        progressBar = view.findViewById(R.id.pro);


        blogRecyclerAdapter = new BlogRecyclerAdapter(blog_list);
        blog_list_view.setLayoutManager(new LinearLayoutManager(container.getContext()));
        blog_list_view.setAdapter(blogRecyclerAdapter);
        blog_list_view.setHasFixedSize(true);

        statusRecyclerAdapter = new StatusRecyclerAdapter(getContext(), status_list);
        status_list_view.setLayoutManager(new LinearLayoutManager (container.getContext(), LinearLayoutManager.HORIZONTAL, false));
        status_list_view.setAdapter(statusRecyclerAdapter);
        status_list_view.setHasFixedSize(true);

        suggestFollowersAdapter = new SuggestFollowersAdapter(suggest_followers_list, getContext());
        suggest_list_view.setLayoutManager(new LinearLayoutManager (container.getContext(), LinearLayoutManager.HORIZONTAL, false));
        suggest_list_view.setAdapter(suggestFollowersAdapter);
        suggest_list_view.setHasFixedSize(true);


        Query firstStatusQuery = firebaseFirestore.collection("Status").orderBy("timeset", Query.Direction.DESCENDING);
        firstStatusQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (!documentSnapshots.isEmpty()) {


                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String statusPostId = doc.getDocument().getId();
                            final StatusPost blogPost = doc.getDocument().toObject(StatusPost.class).withId(statusPostId);

                            status_list.add(blogPost);

                            statusRecyclerAdapter.notifyDataSetChanged();
                        }
                    }

                }else {
                    status_list_view.setVisibility(View.GONE);
                }
            }

        });



        checkFollowing();

        getFollowers();



        return view;

    }


    private void getFollowers(){
        followerList = new ArrayList<>();
        firebaseFirestore.collection("Follow/" + user.getUid() + "/Followers")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        suggest_followers_list.clear();
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){
                                followerList.add(documentSnapshot.getId());
                            }
                            suggest();
                    }
                });
    }

    private void suggest() {
        Query query = firebaseFirestore.collection("Users");

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (!documentSnapshots.isEmpty()) {
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            String userId = doc.getDocument().getId();
                            final User blogPost = doc.getDocument().toObject(User.class).withId(userId);
                            for (String id : followerList) {
                                if (!blogPost.getUserId().equals(id)) {
                                    suggest_followers_list.add(blogPost);
                                }
                            }
                            /*if (blogPost.getUserId().equals(user.getUid())) {
                                suggest_followers_list.add(blogPost);
                            }*/
                        }
                    }
                }
                suggestFollowersAdapter.notifyDataSetChanged();
            }
        });
    }


    private void checkFollowing(){
        followingList = new ArrayList<>();

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Follow/" + user.getUid() + "/Following")
        .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                blog_list.clear();
                if (queryDocumentSnapshots != null)
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){
                    followingList.add(documentSnapshot.getId());
                }

                readPost();

            }
        });

    }


    private void readPost(){

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        Query firstQuery = firebaseFirestore.collection("Posts").orderBy("timestamp", Query.Direction.DESCENDING);


        firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (!documentSnapshots.isEmpty()) {

                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String blogPostId = doc.getDocument().getId();
                            final BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(blogPostId);
                            for (String id : followingList){
                                if (blogPost.getUser_id().equals(id)){
                                    blog_list.add(blogPost);
                                }
                            }
                            if (blogPost.getUser_id().equals(user.getUid())) {
                                blog_list.add(blogPost);
                            }
                        }
                    }
                }
                blogRecyclerAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

        });

    }
}
