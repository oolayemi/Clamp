package com.stylet.clamp.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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
import com.stylet.clamp.Adapters.RepliedCommentsRecyclerAdapter;
import com.stylet.clamp.Model.RepliedComments;
import com.stylet.clamp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReplyCommentActivity extends AppCompatActivity {

    EditText editText;
    CircleImageView circleImageView;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser user;
    private String blog_post_idd;

    private RepliedCommentsRecyclerAdapter repliedCommentsRecyclerAdapter;
    private List<RepliedComments> repliedCommentsList;
    String comment_id;


    private FirebaseAuth firebaseAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_comment);

        comment_id = getIntent().getStringExtra("comment_id");
        String comment_username = getIntent().getStringExtra("comment_user_name");

        firebaseAuth = FirebaseAuth.getInstance();

        editText = findViewById(R.id.reply_comment_field);
        circleImageView = findViewById(R.id.reply_comment_post_btn);
        blog_post_idd = getIntent().getStringExtra("blog_post_idd");
        firebaseFirestore = FirebaseFirestore.getInstance();
        user = firebaseAuth.getCurrentUser();
        RecyclerView replied_comment_list = findViewById(R.id.replied_comment_list);

        repliedCommentsList = new ArrayList<>();


        repliedCommentsRecyclerAdapter = new RepliedCommentsRecyclerAdapter(repliedCommentsList);
        replied_comment_list.setHasFixedSize(true);
        replied_comment_list.setLayoutManager(new LinearLayoutManager(this));
        replied_comment_list.setAdapter(repliedCommentsRecyclerAdapter);



        firebaseFirestore.collection("Posts/" + blog_post_idd + "/Comments/" + comment_id + "/RepliedComments")
                .addSnapshotListener(ReplyCommentActivity.this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {


                        if (!documentSnapshots.isEmpty()) {

                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                                if (doc.getType() == DocumentChange.Type.ADDED) {

                                    String repliedCommentId = doc.getDocument().getId();
                                    RepliedComments repliedComments = doc.getDocument().toObject(RepliedComments.class);

                                    repliedCommentsList.add(repliedComments);
                                    repliedCommentsRecyclerAdapter.notifyDataSetChanged();

                                    Toast.makeText(getApplicationContext(), blog_post_idd, Toast.LENGTH_SHORT).show();



                                }
                            }
                        }
                    }
                });

        String userImage = Objects.requireNonNull(user.getPhotoUrl()).toString();
        setUserImage(userImage);





        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editvalue = editText.getText().toString();

                Map<String, Object> commentsMap = new HashMap<>();
                commentsMap.put("message", editvalue);
                commentsMap.put("user_id", user.getUid());
                commentsMap.put("name", user.getDisplayName());
                commentsMap.put("image", user.getPhotoUrl().toString());
                commentsMap.put("timestamp", FieldValue.serverTimestamp());

                firebaseFirestore.collection("Posts/" + blog_post_idd + "/Comments/" + comment_id + "/RepliedComments").add(commentsMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Comment replied", Toast.LENGTH_SHORT).show();
                            editText.setText("");
                        }

                    }
                });
            }
        });
    }
    public void setUserImage(String image){

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.profile_placeholder);
        Glide.with(getApplicationContext()).applyDefaultRequestOptions(requestOptions).load(image).into(circleImageView);

    }
}