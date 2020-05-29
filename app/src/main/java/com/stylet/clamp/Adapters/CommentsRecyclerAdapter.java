package com.stylet.clamp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.stylet.clamp.Activities.ReplyCommentActivity;
import com.stylet.clamp.Model.Comments;
import com.stylet.clamp.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsRecyclerAdapter extends RecyclerView.Adapter<CommentsRecyclerAdapter.ViewHolder> {

    public List<Comments> commentsList;
    public Context context;
    private FirebaseFirestore firebaseFirestore;
    FirebaseUser user;
    FirebaseAuth mAuth;


    public CommentsRecyclerAdapter(List<Comments> commentsList){

        this.commentsList = commentsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_item, parent, false);
        context = parent.getContext();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        holder.setIsRecyclable(false);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();


        final String commentMessage = commentsList.get(position).getMessage();
        final String comment_id = commentsList.get(position).CommentsId;
        final String blogPostId = commentsList.get(position).getBlog_id();


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent replyComment = new Intent(context, ReplyCommentActivity.class);
                replyComment.putExtra("blog_post_idd",commentsList.get(position).getBlog_id());
                replyComment.putExtra("comment_id", commentsList.get(position).CommentsId);
                replyComment.putExtra("comment_user_name", commentsList.get(position).getName());

                context.startActivity(replyComment);
            }
        });

        holder.setUserData(commentMessage, commentsList.get(position).getName(), commentsList.get(position).getImage());


        //Get Likes Count
        firebaseFirestore.collection("Posts/" + blogPostId + "/Comments/" + comment_id + "/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                try{
                    if (!documentSnapshots.isEmpty()) {

                        int count = documentSnapshots.size();
                        holder.updateLikesCount(count);
                    } else {
                        holder.updateLikesCount(0);
                    }
                }
                catch (NullPointerException npe){
                    npe.printStackTrace();
                }

            }
        });

        //Get Likes
        firebaseFirestore.collection("Posts/" + blogPostId + "/Comments/" + comment_id + "/Likes" ).document(user.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                if (documentSnapshot.exists()) {

                    holder.like.setImageDrawable(context.getDrawable(R.mipmap.action_like_accent));

                } else {

                    holder.like.setImageDrawable(context.getDrawable(R.mipmap.action_like_gray));

                }
            }
        });

        //Likes Feature
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseFirestore.collection("Posts/" + blogPostId + "/Comments/" + comment_id + "/Likes").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (!task.getResult().exists()) {

                            Map<String, Object> likesMap = new HashMap<>();
                            likesMap.put("timestamp", FieldValue.serverTimestamp());

                            firebaseFirestore.collection("Posts/" + blogPostId + "/Comments/" + comment_id + "/Likes").document(user.getUid()).set(likesMap);

                        } else {

                            firebaseFirestore.collection("Posts/" + blogPostId + "/Comments/" + comment_id + "/Likes").document(user.getUid()).delete();

                        }

                    }
                });
            }
        });

        //Get Comments Count
        firebaseFirestore.collection("Posts/" + blogPostId + "/Comments/" + comment_id + "/RepliedComments").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                try{
                    if (!documentSnapshots.isEmpty()) {

                        int count = documentSnapshots.size();
                        holder.updateCommentsCount(count);
                    } else {
                        holder.updateCommentsCount(0);
                    }
                }
                catch (NullPointerException npe){
                    npe.printStackTrace();
                }

            }
        });

    }


    @Override
    public int getItemCount() {

        if(commentsList != null) {
            return commentsList.size();
        } else { return 0;  }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private ImageView like;
        private TextView comment_count, likes_count;

        private TextView comment_message;
        private TextView comment_username;
        private CircleImageView comment_image;





        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            comment_count = mView.findViewById(R.id.cmt_replyCount);
            likes_count = mView.findViewById(R.id.cmt_likesCount);
            like = mView.findViewById(R.id.cmt_like_btn);
        }

        public void setUserData(String message,String name, String image ){

            comment_message = mView.findViewById(R.id.comment_message);
            comment_message.setText(message);

            comment_username = mView.findViewById(R.id.comment_username);
            comment_username.setText(name);

            comment_image = mView.findViewById(R.id.comment_image);
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.profile_placeholder);
            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(image).into(comment_image);
        }

        private void updateLikesCount(int count){

            String likesCount;

                likesCount = count + "";

            likes_count.setText(likesCount);
        }

        private void updateCommentsCount(int count) {

            String commentCount;

            commentCount = count + "";
            comment_count.setText(commentCount);
        }
    }


}
