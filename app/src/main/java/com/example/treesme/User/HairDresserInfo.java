package com.example.treesme.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.treesme.Adapters.AvailableHairDresserListAdaper;
import com.example.treesme.Model.Planning;
import com.example.treesme.Model.Review;
import com.example.treesme.Model.User;
import com.example.treesme.R;
import com.example.treesme.SharedPreferencesManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HairDresserInfo extends AppCompatActivity {

    private TextView titleTxt;
    private RecyclerView recyclerView;
    private String style = "";
    List<Planning> planningList;
    ImageView backImg;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String time ="";
    private String date = "";
    private SharedPreferencesManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hair_dresser_info);
        titleTxt = findViewById(R.id.title);
        backImg = findViewById(R.id.back);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        manager = new SharedPreferencesManager(HairDresserInfo.this);
        getLocale();
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HairDresserInfo.this, MainScreenDashboard.class);
                startActivity(intent);
            }
        });
        style = getIntent().getStringExtra("style");
        time = getIntent().getStringExtra("time");
        date = getIntent().getStringExtra("date");
        titleTxt.setText(style);
        reference = FirebaseDatabase.getInstance().getReference().child("Planning");
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(HairDresserInfo.this);
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(manager);
        planningList = new ArrayList<>();
        getReviews();
       // getUserChatList();
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

    private void getReviews() {

        Query query = reference.orderByChild("date").equalTo(date);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    planningList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Planning model = ds.getValue(Planning.class);
                        if (model.getTime().equals(time)){
                            planningList.add(model);
                        }
                    }
                    AvailableHairDresserListAdaper adaper = new AvailableHairDresserListAdaper(HairDresserInfo.this,
                            planningList);
                    recyclerView.setAdapter(adaper);
                    adaper.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}