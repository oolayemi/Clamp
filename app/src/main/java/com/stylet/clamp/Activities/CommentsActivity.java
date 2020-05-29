package com.stylet.clamp.Activities;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.stylet.clamp.Adapters.CommentsRecyclerAdapter;
import com.stylet.clamp.Model.Comments;
import com.stylet.clamp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsActivity extends AppCompatActivity {

    ImageView imgPost;
    CircleImageView imgPostUser, currentuser_img;
    TextView txtPostDesc, txtPostDateName, blog_user_name, blog_like_count, blog_comment_count;
    EditText editTextComment;
    Button btnAddComment;
    CardView mainpost;

    private RecyclerView comment_list;
    private CommentsRecyclerAdapter commentsRecyclerAdapter;
    private List<Comments> commentsList;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private FirebaseFirestore firebaseFirestore;

    private String postImage;

    private String blog_post_id, post_user_image, post_user_name;
    private String current_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);


        imgPost = findViewById(R.id.post_detail_img);
        imgPostUser = findViewById(R.id.post_detail_user_img);
        currentuser_img = findViewById(R.id.currentuser_img);

        txtPostDesc = findViewById(R.id.post_detail_desc);
        //txtPostDateName = findViewById(R.id.post_detail_date_name);

        editTextComment = findViewById(R.id.post_detail_comment);
        btnAddComment = findViewById(R.id.post_detail_add_comment_btn);

        comment_list = findViewById(R.id.rv_comment);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        current_user_id = firebaseAuth.getCurrentUser().getUid();
        blog_post_id = getIntent().getStringExtra("blog_post_id");
        post_user_image = getIntent().getStringExtra("post_user_image");
        post_user_name = getIntent().getStringExtra("post_user_name");
        blog_user_name = findViewById(R.id.blog_user_name);
        blog_comment_count = findViewById(R.id.blog_comment_count);
        blog_like_count = findViewById(R.id.blog_like_count);

        commentsList = new ArrayList<>();
        commentsRecyclerAdapter = new CommentsRecyclerAdapter(commentsList);
        comment_list.setHasFixedSize(true);
        comment_list.setLayoutManager(new LinearLayoutManager(this));
        comment_list.setAdapter(commentsRecyclerAdapter);


        postImage = getIntent().getExtras().getString("postImage");
        mainpost = findViewById(R.id.post_detail_img_card);
        if (postImage == null){

            mainpost.setVisibility(View.GONE);
        }
        Glide.with(getApplicationContext())
                .load(firebaseUser.getPhotoUrl())
                .into(currentuser_img);
        Glide.with(this).load(postImage).into(imgPost);


        String postDescription = getIntent().getExtras().getString("description");
        if (postDescription == null){
            txtPostDesc.setVisibility(View.GONE);
        }
        txtPostDesc.setText(postDescription);
        //blog_user_name.setText(""+ blog_user_name);


        Glide.with(this).load(post_user_image).into(imgPostUser);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        firebaseFirestore.collection("Posts/" + blog_post_id + "/Comments")
                .addSnapshotListener(CommentsActivity.this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                        if (!documentSnapshots.isEmpty()) {

                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                                if (doc.getType() == DocumentChange.Type.ADDED) {

                                    String commentId = doc.getDocument().getId();
                                    Comments comments = doc.getDocument().toObject(Comments.class).withId(commentId);
                                    commentsList.add(comments);
                                    commentsRecyclerAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                });


        comment_list.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Toast.makeText(getApplicationContext(), "item comment", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String comment_message = editTextComment.getText().toString();

                if (!comment_message.isEmpty()) {

                    Map<String, Object> commentsMap = new HashMap<>();
                    commentsMap.put("message", comment_message);
                    commentsMap.put("user_id", current_user_id);
                    commentsMap.put("blog_id", blog_post_id);
                    commentsMap.put("name", firebaseUser.getDisplayName());
                    commentsMap.put("image", firebaseUser.getPhotoUrl().toString());
                    commentsMap.put("timestamp", FieldValue.serverTimestamp());



                    firebaseFirestore.collection("Posts/" + blog_post_id + "/Comments").add(commentsMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {

                            if (!task.isSuccessful()) {

                                Toast.makeText(CommentsActivity.this, "Error Posting Comment: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                            } else {
                                editTextComment.setText("");

                            }

                        }
                    });

                }else {
                    Toast.makeText(CommentsActivity.this, "Fill at least one field", Toast.LENGTH_SHORT).show();
                }
            }
        });


        //Get Likes Count
        firebaseFirestore.collection("Posts/" + blog_post_id + "/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                try{
                    if (!documentSnapshots.isEmpty()) {

                        int count = documentSnapshots.size();
                        updateLikesCount(count);
                    } else {
                        updateLikesCount(0);
                    }
                }
                catch (NullPointerException npe){
                    npe.printStackTrace();
                }

            }
        });


        //Get Comments Count
        firebaseFirestore.collection("Posts/" + blog_post_id + "/Comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                try{
                    if (!documentSnapshots.isEmpty()) {

                        int count = documentSnapshots.size();
                        updateCommentsCount(count);
                    } else {
                        updateCommentsCount(0);
                    }
                }
                catch (NullPointerException npe){
                    npe.printStackTrace();
                }

            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateSlideDown(this);
    }

    private void updateLikesCount(int count){

        String likesCount;

        TextView blogLikeCount = findViewById(R.id.blog_like_count);
        if (count == 1){
            likesCount = count + " Like";
        }else{
            likesCount = count + " Likes";
        }
        blogLikeCount.setText(likesCount);
    }

    private void updateCommentsCount(int i) {

        String commentsCount;

        TextView blogCommentCount = findViewById(R.id.blog_comment_count);
        if (i == 1){
            commentsCount = i + " Comment";
        }else{
            commentsCount = i + " Comments";
        }
        blogCommentCount.setText(commentsCount);
    }

    private void addNotification(){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("username", firebaseUser.getDisplayName());
        hashMap.put("userimage", firebaseUser.getPhotoUrl().toString());
        hashMap.put("postimage", postImage);
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "commented " + editTextComment.getText().toString() + " on your post");
        hashMap.put("postid", blog_post_id);
        hashMap.put("ispost", true);


        firebaseFirestore.collection("Notifications").document(firebaseUser.getUid()).set(hashMap);
    }

}
