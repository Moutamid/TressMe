package com.example.treesme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.treesme.Adapters.FaqsListAdapter;
import com.example.treesme.Model.Faqs;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FaqActivity extends AppCompatActivity {

    private ImageView backImg;
    private SharedPreferencesManager manager;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private List<Faqs> faqsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        backImg = (ImageView) findViewById(R.id.back);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        manager = new SharedPreferencesManager(FaqActivity.this);
        getLocale();
        faqsList = new ArrayList<>();
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FaqActivity.this,ModifyProfileScreen.class));
            }
        });
        linearLayoutManager = new LinearLayoutManager(FaqActivity.this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        getFaqsList();
    }

    private void getFaqsList() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Faqs");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    faqsList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Faqs model = ds.getValue(Faqs.class);
                        faqsList.add(model);
                    }
                    FaqsListAdapter adapter = new FaqsListAdapter(FaqActivity.this,faqsList);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
}