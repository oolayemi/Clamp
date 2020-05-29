package com.stylet.clamp.Activities.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stylet.clamp.R;

public class MainHomeFragment extends Fragment {

    private BottomNavigationView bottombar;


    private FirebaseFirestore firebaseFirestore;

    private HomeFragment homeFragment;
    private SearchFragment searchFragment;
    private SettingFragment settingFragment;
    private NotificationFragment notificationFragment;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_home, container, false);

        bottombar = view.findViewById(R.id.bottombar);

        firebaseFirestore = FirebaseFirestore.getInstance();


        // FRAGMENTS
        homeFragment = new HomeFragment();
        searchFragment = new SearchFragment();
        settingFragment = new SettingFragment();
        notificationFragment = new NotificationFragment();


        /*FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        firebaseFirestore.setFirestoreSettings(settings);*/
        initializeFragment();

        bottombar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected( MenuItem item) {

                Fragment currentFragment = getParentFragmentManager().findFragmentById(R.id.main_container);

                switch (item.getItemId()) {

                    case R.id.btm_home:
                        replaceFragment(homeFragment, currentFragment);
                        return true;

                    case R.id.btm_search:
                        replaceFragment(searchFragment, currentFragment);
                        return true;

                    case R.id.btm_settings:
                        replaceFragment(settingFragment, currentFragment);
                        return true;

                    case R.id.btm_notification:
                        replaceFragment(notificationFragment, currentFragment);
                        return true;

                    default:
                        return false;


                }

            }
        });


        return view;

    }

    private void initializeFragment(){

        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();

        fragmentTransaction.add(R.id.main_container, homeFragment);
        fragmentTransaction.add(R.id.main_container, searchFragment);
        fragmentTransaction.add(R.id.main_container, notificationFragment);
        fragmentTransaction.add(R.id.main_container, settingFragment);

        fragmentTransaction.hide(searchFragment);
        fragmentTransaction.hide(notificationFragment);
        fragmentTransaction.hide(settingFragment);

        fragmentTransaction.commit();

    }

    private void replaceFragment(Fragment fragment, Fragment currentFragment){

        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        if(fragment == homeFragment){

            fragmentTransaction.hide(searchFragment);
            fragmentTransaction.hide(notificationFragment);
            fragmentTransaction.hide(settingFragment);

        }

        if(fragment == searchFragment){

            fragmentTransaction.hide(homeFragment);
            fragmentTransaction.hide(notificationFragment);
            fragmentTransaction.hide(settingFragment);

        }

        if(fragment == settingFragment){

            fragmentTransaction.hide(homeFragment);
            fragmentTransaction.hide(searchFragment);
            fragmentTransaction.hide(notificationFragment);

        }

        if(fragment == notificationFragment){

            fragmentTransaction.hide(homeFragment);
            fragmentTransaction.hide(searchFragment);
            fragmentTransaction.hide(settingFragment);

        }

        fragmentTransaction.show(fragment);

        //fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.commit();

    }
}
