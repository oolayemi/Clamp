package com.stylet.clamp.Activities.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.stylet.clamp.R;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    private CircleImageView usersImage;
    private TextView usersEmail;
    private TextView usersName;
    TextView followingcount, postcount;
    TextView followerscount;

    String profile_id;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        usersImage = root.findViewById(R.id.usersImage);
        usersEmail = root.findViewById(R.id.usersEmail);
        usersName = root.findViewById(R.id.usersName);
        followerscount = root.findViewById(R.id.followercount);
        followingcount = root.findViewById(R.id.followingcount);
        postcount = root.findViewById(R.id.postcount);

        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        firebaseFirestore.setFirestoreSettings(settings);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        usersName.setText(user.getDisplayName());
        usersEmail.setText(user.getEmail());

        Glide.with(getActivity())
                .load(user.getPhotoUrl())
                .into(usersImage);

        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profile_id = prefs.getString("profileid", "none");


        getMyPosts();
        getFollowers();



        return root;

    }

    private void getFollowers(){


        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection("Follow/" + user.getUid() + "/Followers").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                try{
                    if (!documentSnapshots.isEmpty()) {

                        int count = documentSnapshots.size();
                        followerscount.setText(""+ count);
                    }
                }
                catch (NullPointerException npe){
                    npe.printStackTrace();
                }

            }
        });


        firebaseFirestore.collection("Follow/" + user.getUid() + "/Following").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                try{
                    if (!documentSnapshots.isEmpty()) {

                        int count = documentSnapshots.size();
                        followingcount.setText(""+count);
                    }
                }
                catch (NullPointerException npe){
                    npe.printStackTrace();
                }

            }
        });


        /*DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profile_id).child("Followers");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followerscount.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        /*DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profile_id).child("Following");

        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingcount.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

    }

    private void getMyPosts(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collection = db.collection("Posts");

        Query query = collection.whereEqualTo("user_id", user.getUid());

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    int i = 0;
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            i++;
                    }
                    postcount.setText(""+i);

                }
            }
        });

    }

    private void addNotification(){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("username", user.getDisplayName());
        hashMap.put("userimage", user.getPhotoUrl().toString());
        hashMap.put("postimage", "");
        hashMap.put("userid", user.getUid());
        hashMap.put("text", "started following you!✌");
        hashMap.put("postid", "");
        hashMap.put("ispost", false);


        firebaseFirestore.collection("Notifications").document(profile_id).set(hashMap);
    }

}