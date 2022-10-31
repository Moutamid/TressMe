package com.moutamid.treesme.User;

import android.content.res.Configuration;
import android.os.Bundle;

import com.moutamid.treesme.Model.User;
import com.moutamid.treesme.R;
import com.moutamid.treesme.SharedPreferencesManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.moutamid.treesme.databinding.ActivityMainScreenDashboardBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class MainScreenDashboard extends AppCompatActivity {

    private ActivityMainScreenDashboardBinding binding;
    AppBarConfiguration appBarConfiguration;
    private FirebaseAuth mAuth;
    private DatabaseReference db;
    private SharedPreferencesManager manager;
    private BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainScreenDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        navView = findViewById(R.id.nav_view);
        getSupportActionBar().hide();
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home ,R.id.navigation_community, R.id.navigation_chat_support,
                R.id.navigation_message,
                R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this,
                R.id.nav_host_fragment_activity_main_screen_dashboard);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        manager = new SharedPreferencesManager(MainScreenDashboard.this);
        getLocale();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference().child("Users");
        //getUserData();
    }
    private void getLocale(){

        String lang = manager.retrieveString("lang","");
        setLocale(lang);
    }

    private void setLocale(String lng) {

        Locale locale = new Locale(lng);
        Locale.setDefault(locale);

        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration,getBaseContext().getResources().getDisplayMetrics());
        manager.storeString("lang",lng);
    }
    private void getUserData() {
        db.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    User model = snapshot.getValue(User.class);
                    if (model.getUserType().equals("user")){
                        navView.getMenu().getItem(R.id.navigation_community).setVisible(true);
                        navView.getMenu().getItem(R.id.navigation_pie_chart).setVisible(false);
                    }else {
                        navView.getMenu().getItem(R.id.navigation_community).setVisible(false);
                        navView.getMenu().getItem(R.id.navigation_pie_chart).setVisible(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this,
                R.id.nav_host_fragment_activity_main_screen_dashboard);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}