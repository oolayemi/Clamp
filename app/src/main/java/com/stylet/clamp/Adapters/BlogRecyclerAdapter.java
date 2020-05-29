package com.stylet.clamp.Adapters;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;
import com.stylet.clamp.Activities.CommentsActivity;
import com.stylet.clamp.Activities.ShowImageActivity;
import com.stylet.clamp.Model.BlogPost;
import com.stylet.clamp.R;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {


    private List<BlogPost> blog_list;
    public Context context;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private TextView popupsave, popupdelete, popupaddfav, popupcancel;
    private View popupviewdelete;

    private Dialog popupOption;


    public BlogRecyclerAdapter(List<BlogPost> blog_list){

        this.blog_list = blog_list;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_list_item, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        firebaseFirestore.setFirestoreSettings(settings);


        popupOption = new Dialog(context);
        popupOption.setContentView(R.layout.options_popup);
        popupOption.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupOption.getWindow().setLayout(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT);
        popupOption.getWindow().getAttributes().gravity = Gravity.CENTER;

        popupviewdelete = popupOption.findViewById(R.id.popupview_delete);
        popupsave = popupOption.findViewById(R.id.popup_save);
        popupdelete = popupOption.findViewById(R.id.popup_delete);
        popupaddfav = popupOption.findViewById(R.id.popup_addfav);
        popupcancel = popupOption.findViewById(R.id.popupcancel);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (firebaseAuth.getCurrentUser() != null){

            holder.setIsRecyclable(false);

            final String blogPostId = blog_list.get(position).BlogPostId;
            final String currentUserId = firebaseAuth.getCurrentUser().getUid();

            String desc_data = blog_list.get(position).getDesc();
            holder.setDescText(desc_data);

            String image_url = blog_list.get(position).getImage_url();
            String thumbUri = blog_list.get(position).getImage_thumb();
            holder.setBlogImage(image_url, thumbUri);

            final String blog_user_id = blog_list.get(position).getUser_id();

            String userName = blog_list.get(position).getUsername();
            final String userImage = blog_list.get(position).getUser_image();

            holder.setUserData(userName, userImage);

            try {

                //Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
                //calendar.setTimeInMillis(blog_list.get(position).getTimestamp().getTime());

                Timestamp timestamp = blog_list.get(position).getTimestamp();

                Date past = timestamp.toDate();

                //Date past = calendar.getTime();

                Date now = new Date();

                long seconds = TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
                long minutes = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
                long hours = TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
                long days = TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());

                String time;

                if(seconds<60)
                {
                    time = "Just now";
                }
                else if (seconds >60 && seconds < 120){
                    time = "About a minute ago";
                }
                else if(minutes<60)
                {
                    time = minutes+" minutes ago";
                }
                else if(hours<24)
                {
                    time = hours+" hours ago";
                }
                else
                {
                    time = days+" days ago";
                }


                //String date = DateFormat.format("MMM dd, yyyy hh-mm",calendar).toString();
                holder.setTime(time);

            } catch (Exception e) {
                e.printStackTrace();
                //Toast.makeText(context, "Exception: " + e.getMessage(), Toast.LENGTH_LONG).show();

            }

            holder.blogImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent toShowImage = new Intent(context, ShowImageActivity.class);
                    toShowImage.putExtra("blog_image", blog_list.get(position).getImage_url());
                    context.startActivity(toShowImage);

                    Animatoo.animateSwipeRight(context);


                }
            });


            holder.optPopup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    holder.optPopup.setImageDrawable(context.getDrawable(R.drawable.ic_arrow_up));
                    //Display Option menu

                    if (blog_user_id.equals(currentUserId)) {

                        popupOption.show();

                        popupsave.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                popupOption.dismiss();
                                holder.optPopup.setImageDrawable(context.getDrawable(R.drawable.ic_arrow_down));
                                final String image_url = blog_list.get(position).getImage_url();
                                final String filename = blog_list.get(position).getUsername();
                                firebaseFirestore.collection("Posts").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()){
                                            downloadFile(context, filename, DIRECTORY_DOWNLOADS, image_url);

                                        }
                                    }
                                });

                            }
                        });

                        popupdelete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                popupOption.dismiss();
                                holder.optPopup.setImageDrawable(context.getDrawable(R.drawable.ic_arrow_down));
                                firebaseFirestore.collection("Posts").document(blogPostId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            blog_list.remove(position);
                                            notifyDataSetChanged();
                                            Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show();
                                        } else {
                                            String errorMessage = task.getException().toString();
                                            Toast.makeText(context, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();

                                        }


                                    }
                                });
                            }
                        });

                        popupaddfav.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });

                        popupcancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                popupOption.dismiss();

                            }
                        });

                    } else {

                        popupOption.show();

                        popupsave.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                popupOption.dismiss();
                                holder.optPopup.setImageDrawable(context.getDrawable(R.drawable.ic_arrow_down));
                                final String image_url = blog_list.get(position).getImage_url();
                                final String filename = blog_list.get(position).getUsername();
                                firebaseFirestore.collection("Posts").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()){
                                            downloadFile(context, filename, DIRECTORY_DOWNLOADS, image_url);

                                        }
                                    }
                                });

                            }
                        });

                        popupdelete.setVisibility(View.GONE);
                        popupviewdelete.setVisibility(View.GONE);

                        popupaddfav.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });

                        popupcancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                holder.optPopup.setImageDrawable(context.getDrawable(R.drawable.ic_arrow_down));
                                popupOption.dismiss();

                            }
                        });

                    }
                }

                private void downloadFile(Context context, String fileName, String destinationDirectory, String url) {
                    DownloadManager downloadManager = (DownloadManager) context.
                            getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri = Uri.parse(url);
                    DownloadManager.Request request = new DownloadManager.Request(uri);

                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName + ".jpg");

                    if (downloadManager != null) {
                        downloadManager.enqueue(request);
                    }
                }
            });


            //Get Likes Count
            firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
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
            firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                    if (documentSnapshot.exists()) {

                        holder.blogLikeBtn.setImageDrawable(context.getDrawable(R.mipmap.action_like_accent));

                    } else {

                        holder.blogLikeBtn.setImageDrawable(context.getDrawable(R.mipmap.action_like_gray));

                    }
                }
            });

            //Likes Feature
            holder.blogLikeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (!task.getResult().exists()) {

                                Map<String, Object> likesMap = new HashMap<>();
                                likesMap.put("timestamp", FieldValue.serverTimestamp());

                                firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).set(likesMap);


                            } else {

                                firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).delete();

                            }

                        }
                    });
                }
            });

            //Get Comments Count
            firebaseFirestore.collection("Posts/" + blogPostId + "/Comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
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

            holder.blogCommentBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent commentIntent = new Intent(context, CommentsActivity.class);
                    commentIntent.putExtra("blog_post_id", blogPostId);
                    commentIntent.putExtra("post_user_image", blog_list.get(position).getUser_image());
                    commentIntent.putExtra("post_user_name", blog_list.get(position).getUsername());
                    commentIntent.putExtra("postImage", blog_list.get(position).getImage_thumb());
                    commentIntent.putExtra("description", blog_list.get(position).getDesc());
                    commentIntent.putExtra("userPhoto", blog_list.get(position).getUser_image());
                    commentIntent.putExtra("userName", blog_list.get(position).getUsername());

                    context.startActivity(commentIntent);
                    Animatoo.animateSlideUp(context);

                }
            });

        }
    }


    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private TextView blogDate;
        private ImageView optPopup;

        private ImageView blogLikeBtn;

        private ImageView blogCommentBtn;

        private ImageView blogImageView;



        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;


            blogLikeBtn = mView.findViewById(R.id.blog_like_btn);
            blogCommentBtn = mView.findViewById(R.id.blog_comment_icon);
            optPopup = mView.findViewById(R.id.arrow_down);
            blogDate = mView.findViewById(R.id.blog_date);

            blogImageView = mView.findViewById(R.id.blog_image);

            //Calendar calendar = Calendar.getInstance();
            //String date = DateFormat.format("MMM dd, yyyy",calendar).toString();

            //blogDate.setText(date);

        }

        private void setDescText(String descText){

            TextView descView = mView.findViewById(R.id.blog_desc);

            if (descText == null){
                descView.setVisibility(View.GONE);
            }
            descView.setText(descText);

        }

        private void setBlogImage(String downloadUri, String thumbUri){



            CardView blog_image_card = mView.findViewById(R.id.blog_image_card);
            if (downloadUri == null || thumbUri == null){
                blog_image_card.setVisibility(View.GONE);
            }
            Glide.with(context).load(thumbUri).into(blogImageView);


            // Glide.with(context).applyDefaultRequestOptions(requestOptions).load(downloadUri).thumbnail(Glide.with(context).load(thumbUri)).into(blogImageView);

        }

        private void setTime(String date) {


            blogDate.setText(date);

        }

        private void setUserData(String name, String image){

            TextView blogUserName = mView.findViewById(R.id.blog_user_name);
            CircleImageView blogUserImage = mView.findViewById(R.id.blog_user_image);

            blogUserName.setText(name);

            Glide.with(context).load(image).into(blogUserImage);

        }

        private void updateLikesCount(int count){

            String likesCount;

            TextView blogLikeCount = mView.findViewById(R.id.blog_like_count);

                likesCount = count +"";

            blogLikeCount.setText(likesCount);
        }

        private void updateCommentsCount(int i) {

            String commentsCount;

            TextView blogCommentCount = mView.findViewById(R.id.blog_comment_count);

            commentsCount = i + "";

            blogCommentCount.setText(commentsCount);
        }
    }

    private void addNotification(String userid, String postid, String postimage){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("username", firebaseUser.getDisplayName());
        hashMap.put("userimage", firebaseUser.getPhotoUrl().toString());
        hashMap.put("postimage", postimage);
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "liked your post");
        hashMap.put("postid", postid);
        hashMap.put("ispost", true);


        firebaseFirestore.collection("Notifications").document(userid).set(hashMap);
    }
}