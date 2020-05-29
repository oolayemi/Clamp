package com.stylet.clamp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stylet.clamp.Model.StatusPost;
import com.stylet.clamp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class StatusRecyclerAdapter extends RecyclerView.Adapter<StatusRecyclerAdapter.ViewHolder> {

    public Context context;
    public List<StatusPost> status_list;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    public StatusRecyclerAdapter(Context context, List<StatusPost> status_list) {
        this.context = context;
        this.status_list = status_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, final int position) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.status_items, parent, false);
        context = parent.getContext();

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.setIsRecyclable(false);


        final String statusPostId = status_list.get(position).StatusPostId;
        String desc = status_list.get(position).getDesc();
        String userimage = status_list.get(position).getUser_image();

        String username = status_list.get(position).getUsername();
        String image = status_list.get(position).getImage_url();
        holder.setStatusData(userimage, username, image);

        Timestamp timestamp = status_list.get(position).getTimeset();

        Date statusPostTime = timestamp.toDate();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd HH:mm");

        Calendar cal = Calendar.getInstance();
        cal.setTime(statusPostTime);
        Date timePosted = cal.getTime();
        Date now = new Date();

        System.out.println("*****________________------------------_______" + timePosted + "________________---------------_______*****");

        cal.add(Calendar.MINUTE, 10);
        Date timeToExpire = cal.getTime();
        System.out.println("________________------------------_______" + timeToExpire + "________________------------------_______");


        holder.statusview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                PopupMenu popupMenu = new PopupMenu(context, holder.statusview);
                if (currentUser.getUid().equals(status_list.get(position).getUser_id())){

                    popupMenu.inflate(R.menu.status_option_menu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()){
                                case R.id.delete_status:
                                    firebaseFirestore.collection("Status").document(statusPostId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                status_list.remove(position);
                                                notifyDataSetChanged();
                                                Snackbar.make(v, "Status Deleted", Snackbar.LENGTH_LONG)
                                                        .setAction("Action", null).show();
                                                //Toast.makeText(context, "Status deleted", Toast.LENGTH_SHORT).show();
                                            } else {
                                                String errorMessage = task.getException().toString();
                                                Toast.makeText(context, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();

                                            }


                                        }
                                    });

                                    break;
                            }
                            return false;
                        }
                    });

                    popupMenu.show();
                }
                return false;
            }
        });


    }

    @Override
    public int getItemCount() {
        return status_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private TextView title, status_user_name;
        private TextView timeSet;
        private ImageView statusImage;
        private CircleImageView userimage;
        private ConstraintLayout statusview;



        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;


            statusImage = mView.findViewById(R.id.status_image);
            statusview = mView.findViewById(R.id.status_view);
            userimage = mView.findViewById(R.id.status_user_image);
            status_user_name = mView.findViewById(R.id.status_user_name);

        }

        private void setStatusData(String user_image, String username, String image) {

            status_user_name.setText(username);

            Glide.with(context).load(user_image).into(userimage);

            //timeExp.setText(timeset);

            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.profile_placeholder);

            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(image).into(statusImage);

        }
    }
}
