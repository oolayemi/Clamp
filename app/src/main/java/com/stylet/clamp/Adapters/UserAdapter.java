package com.stylet.clamp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.stylet.clamp.Activities.Fragment.ProfileFragment;
import com.stylet.clamp.Activities.MainActivity;
import com.stylet.clamp.Model.User;
import com.stylet.clamp.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context context;
    private List<User> mUsers;
    private FirebaseUser firebaseUser;


    private Boolean isFragment;

    public UserAdapter(Context context, List<User> mUsers, Boolean isFragment) {
        this.context = context;
        this.mUsers = mUsers;
        this.isFragment = isFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final User user = mUsers.get(position);

        holder.btn_follow.setVisibility(View.VISIBLE);

        holder.username.setText(user.getUsername());
        holder.fullname.setText(user.getUseremail());

        Glide.with(context).load(user.getUserimage()).into(holder.image_profile);

        isFollowing(user.getUserId(), holder.btn_follow);

        if (user.getUserId().equals(firebaseUser.getUid())){
            holder.btn_follow.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFragment){
                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", user.getUserId());
                editor.apply();


                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
            }else {
                    Intent toMain = new Intent(context, MainActivity.class);
                    toMain.putExtra("userid", user.getUserId());
                    context.startActivity(toMain);
                }

            }
        });

        holder.btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.btn_follow.getText().toString().equals("Follow")){
                    Map<String, Object> likesMap = new HashMap<>();
                    likesMap.put("timestamp", FieldValue.serverTimestamp());
                    FirebaseFirestore.getInstance().collection("Follow/" + firebaseUser.getUid() + "/Following").document(user.getUserId()).set(likesMap);
                    FirebaseFirestore.getInstance().collection("Follow/" + user.getUserId() + "/Followers").document(firebaseUser.getUid()).set(likesMap);

                    addNotification(user.getUserId());
                }
                else {
                    FirebaseFirestore.getInstance().collection("Follow/" + firebaseUser.getUid() + "/Following").document(user.getUserId()).delete();
                    FirebaseFirestore.getInstance().collection("Follow/" + user.getUserId() + "/Followers").document(firebaseUser.getUid()).delete();
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView username, fullname;
        private Button btn_follow;
        private CircleImageView image_profile;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;

            username = mView.findViewById(R.id.username);
            fullname = mView.findViewById(R.id.fullname);
            btn_follow = mView.findViewById(R.id.btn_follow);
            image_profile = mView.findViewById(R.id.notification_image_profile);

        }
    }

    private void isFollowing(final String userid, final Button button){

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Follow/" + firebaseUser.getUid() + "/Following")
            .document(userid)
            .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (documentSnapshot.exists()){
                        button.setText("Following");
                    }else {
                        button.setText("Follow");
                    }
                }
            });
    }
    private void addNotification(String userid){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("username", firebaseUser.getDisplayName());
        hashMap.put("userimage", firebaseUser.getPhotoUrl().toString());
        hashMap.put("postimage", "");
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "started following you");
        hashMap.put("postid", "");
        hashMap.put("ispost", false);


        firebaseFirestore.collection("Notifications").document(userid).set(hashMap);
    }
}
