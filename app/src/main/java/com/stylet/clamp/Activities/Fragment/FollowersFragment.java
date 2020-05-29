package com.stylet.clamp.Activities.Fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;
import com.stylet.clamp.Adapters.UserAdapter;
import com.stylet.clamp.Model.User;
import com.stylet.clamp.R;

import java.util.ArrayList;
import java.util.List;

public class FollowersFragment extends Fragment {


    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> mUsers;

    private EditText search_bar;
    private FirebaseFirestore firebaseFirestore;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_followers, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        firebaseFirestore.setFirestoreSettings(settings);

        recyclerView = root.findViewById(R.id.search_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        search_bar = root.findViewById(R.id.search_bar);

        mUsers = new ArrayList<>();
        userAdapter = new UserAdapter(getContext(), mUsers, true);
        recyclerView.setAdapter(userAdapter );


        readUsers();
        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUsers(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        return root;
    }

    private void searchUsers(String s){

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Users")
            .orderBy("username")
            .startAt(s)
            .endAt(s + "\uf8ff")

            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (!documentSnapshots.isEmpty()) {
                    mUsers.clear();
                    for (DocumentSnapshot doc : documentSnapshots.getDocuments()) {

                        String userId = doc.getId();
                        User user = doc.toObject(User.class).withId(userId);
                        mUsers.add(user);

                        }
                    userAdapter.notifyDataSetChanged();
                }
                }
            });
    }

    private void readUsers(){


        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Users")

                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        if (!documentSnapshots.isEmpty()) {

                            if (search_bar.getText().toString().equals("")) {

                                mUsers.clear();
                                for (DocumentSnapshot snapshot : documentSnapshots.getDocuments()) {
                                    User user = snapshot.toObject(User.class);
                                    mUsers.add(user);
                                }
                                userAdapter.notifyDataSetChanged();


                            }
                        }
                    }
                });

    }
}