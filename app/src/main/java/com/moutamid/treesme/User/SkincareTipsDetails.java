package com.moutamid.treesme.User;

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

import com.moutamid.treesme.Model.Favorite;
import com.moutamid.treesme.Model.ProductModel;
import com.moutamid.treesme.R;
import com.moutamid.treesme.SharedPreferencesManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class SkincareTipsDetails extends AppCompatActivity {

    WebView webView;
    ImageView favoriteImg;
    ImageView backImg;
    private ProductModel productModel;
    private boolean isFavorite;
    private FirebaseAuth mAuth;
    private String uId;
    private DatabaseReference skinDB,reference;
    private SharedPreferencesManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skincare_tips_details);
       // titleTxt = findViewById(R.id.title);
        backImg = findViewById(R.id.back);
        favoriteImg = findViewById(R.id.favorite);
        mAuth = FirebaseAuth.getInstance();
        uId = mAuth.getCurrentUser().getUid();
        manager = new SharedPreferencesManager(SkincareTipsDetails.this);
        getLocale();
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SkincareTipsDetails.this,MainScreenDashboard.class);
                startActivity(intent);
            }
        });

        productModel = getIntent().getParcelableExtra("product");
        skinDB = FirebaseDatabase.getInstance().getReference().child("SkinTips");
        reference = FirebaseDatabase.getInstance().getReference().child("Favorite");
        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        final Activity activity = this;
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                activity.setProgress(progress * 100);
            }
        });

        webView.loadUrl(productModel.getUrl());
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
        Query query = reference.orderByChild("productName").equalTo(productModel.getName());
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
        Favorite model = new Favorite(key,uId,productModel.getName(),productModel.getImage(),true);
        reference.child(key).setValue(model);
        favoriteImg.setImageResource(R.drawable.white_favorite_24);
    }
    // Go back to previous upon clicking back button
    // instead of closing app.
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}