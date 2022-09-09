package com.example.treesme.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.treesme.Model.Favorite;
import com.example.treesme.R;
import com.example.treesme.SharedPreferencesManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Locale;

public class ProductTestingDetails extends AppCompatActivity {

    String style,image;
    TextView titleTxt;
    ImageView backImg,productImg;
    ImageView favoriteImg;
    private DatabaseReference reference;
    private boolean isFavorite;
    private FirebaseAuth mAuth;
    private String uId;
    private String id = "";
    private String url = "";
    private String type = "";
    private DatabaseReference productDB;
    private SharedPreferencesManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_testing_details);
        titleTxt = findViewById(R.id.title);
        productImg = findViewById(R.id.image);
        backImg = findViewById(R.id.back);
        favoriteImg = findViewById(R.id.favorite);
        mAuth = FirebaseAuth.getInstance();
        uId = mAuth.getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("Favorite");
        manager = new SharedPreferencesManager(ProductTestingDetails.this);
        getLocale();
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductTestingDetails.this, MainScreenDashboard.class);
                startActivity(intent);
            }
        });
        style = getIntent().getStringExtra("style");
        image = getIntent().getStringExtra("image");
        titleTxt.setText(style);

        Picasso.with(ProductTestingDetails.this)
                .load(image)
                .into(productImg);

        favoriteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFavorite){
                    isFavorite = false;
                    deleteFavorite();
                }else {
                    saveFavorite();
                    isFavorite = true;
                }

            }
        });
        getFavorites();
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
    private void getFavorites() {
        Query query = reference.orderByChild("productName").equalTo(style);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    favoriteImg.setImageResource(R.drawable.white_favorite_24);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void deleteFavorite() {

        favoriteImg.setImageResource(R.drawable.ic_baseline_favorite_border_24);
    }

    private void saveFavorite() {
        String key = reference.push().getKey();
        Favorite model = new Favorite(key,uId,style,image,true);
        reference.child(key).setValue(model);
        favoriteImg.setImageResource(R.drawable.white_favorite_24);
    }
}
