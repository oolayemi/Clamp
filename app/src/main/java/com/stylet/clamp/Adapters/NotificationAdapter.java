package com.stylet.clamp.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.stylet.clamp.Activities.Fragment.ProfileFragment;
import com.stylet.clamp.Model.BlogPost;
import com.stylet.clamp.Model.Notification;
import com.stylet.clamp.Model.User;
import com.stylet.clamp.R;

import java.util.List;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private Context mContext;
    private List<Notification> mNotification;
    private FirebaseFirestore firebaseFirestore;

    public NotificationAdapter(Context mContext, List<Notification> mNotification) {
        this.mContext = mContext;
        this.mNotification = mNotification;
    }

    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent,false);

        firebaseFirestore = FirebaseFirestore.getInstance();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Notification notification = mNotification.get(position);

        holder.comment.setText(notification.getText());

        getUserInfo(holder.user_image, holder.username, notification.getUserid());

        if (notification.isIspost()){
            holder.post_image.setVisibility(View.VISIBLE);
            getPostImage(holder.post_image, notification.getPostid());
            //getNotification(holder.user_image, holder.post_image, holder.username, holder.comment, notification.getUserid());
        }else {
            holder.post_image.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notification.isIspost()){
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor.putString("postid", notification.getPostid());
                    editor.apply();

                    //((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PostDetailFragment()).commit();
                }else {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor.putString("userid", notification.getUserid());
                    editor.apply();

                    //((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mNotification.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private CircleImageView user_image;
        private TextView username;
        private TextView comment;
        private ImageView post_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            user_image = mView.findViewById(R.id.notification_image_profile);
            username = mView.findViewById(R.id.notification_username);
            comment = mView.findViewById(R.id.notification_comment);
            post_image = mView.findViewById(R.id.notification_post_image);
        }
    }

    private void getUserInfo(final ImageView imageView, final TextView username, String userid){

        firebaseFirestore.collection("User").document(userid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()){
                    User user = documentSnapshot.toObject(User.class);
                    Glide.with(mContext).load(user.getUserimage()).into(imageView);
                    username.setText(user.getUsername());
                }
            }
        });

                /*.document(userid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){

                    User user = task.getResult().toObject(User.class);
                    Glide.with(mContext).load(user.getUserimage()).into(imageView);
                    username.setText(user.getUsername());
                }
            }
        });*/
    }

    private void getPostImage(final ImageView imageView, String postid){
        firebaseFirestore.collection("Posts").document(postid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    BlogPost blogPost = documentSnapshot.toObject(BlogPost.class);
                    Glide.with(mContext).load(blogPost.getImage_url()).into(imageView);
                }
            }
        });



    /*.document(postid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    BlogPost blogPost = task.getResult().toObject(BlogPost.class);
                    Glide.with(mContext).load(blogPost.getImage_url()).into(imageView);

                }
            }
        });*/
    }

    private void getNotification(final ImageView userimage, final ImageView postimage, final TextView username, final TextView comment, final String userid ){
        firebaseFirestore.collection("Notifications").document(userid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()){
                    Notification notification = documentSnapshot.toObject(Notification.class);
                    Glide.with(mContext).load(notification.getUserimage()).into(userimage);
                    Glide.with(mContext).load(notification.getPostimage()).into(postimage);
                    username.setText(notification.getUsername());
                    comment.setText(notification.getText());


                }
            }
        });

    }
}
