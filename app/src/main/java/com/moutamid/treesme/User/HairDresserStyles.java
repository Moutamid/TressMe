package com.moutamid.treesme.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;

import com.moutamid.treesme.Adapters.HairDresserStylesListAdaper;
import com.moutamid.treesme.Model.Hairstyles;
import com.moutamid.treesme.R;
import com.moutamid.treesme.SharedPreferencesManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HairDresserStyles extends AppCompatActivity {

    GridView hairStylesList;
    DatabaseReference db1;
    List<Hairstyles> hairstyles;
    ImageView backImg;
    private SharedPreferencesManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hair_dresser_styles);
        hairStylesList = findViewById(R.id.gridview);
        backImg = findViewById(R.id.back);
        manager = new SharedPreferencesManager(HairDresserStyles.this);
        getLocale();
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HairDresserStyles.this,MainScreenDashboard.class);
                startActivity(intent);
            }
        });
        getHairStyles();
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

    private void getHairStyles() {
        hairstyles = new ArrayList<>();
        db1 = FirebaseDatabase.getInstance().getReference().child("Hairstyles");
        db1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Hairstyles model = ds.getValue(Hairstyles.class);
                        hairstyles.add(model);
                    }
                    HairDresserStylesListAdaper adapter = new HairDresserStylesListAdaper(HairDresserStyles.this,
                            hairstyles);

                   hairStylesList.setAdapter(adapter);
                   adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}