package com.moutamid.treesme.Hairdresser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.moutamid.treesme.Adapters.MyBenefitsListAdapter;
import com.moutamid.treesme.Model.Benefits;
import com.moutamid.treesme.R;
import com.moutamid.treesme.SharedPreferencesManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyBenefitsActivity extends AppCompatActivity {

    private ImageView backImg,addImg;
    private RecyclerView recyclerView;
    private String name,category,price = "";
    private FirebaseAuth mAuth;
    private String uId;
    private DatabaseReference db;
    private List<Benefits> benefitsList;
    private MyBenefitsListAdapter adapter;
    private SharedPreferencesManager pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_benefits);
        backImg = findViewById(R.id.back);
        addImg = findViewById(R.id.add);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(MyBenefitsActivity.this);
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(manager);
        pref = new SharedPreferencesManager(MyBenefitsActivity.this);
        getLocale();
        benefitsList = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        uId = mAuth.getCurrentUser().getUid();
        db = FirebaseDatabase.getInstance().getReference().child("Benefits").child(uId);
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyBenefitsActivity.this, MainDashboard.class));
            }
        });

        getBenefits();
        addImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddBenefitsDialogBox();
            }
        });
    }

    private void getLocale(){

        String lang = pref.retrieveString("lang","");
        setLocale(lang);
    }

    private void setLocale(String lng) {

        Locale locale = new Locale(lng);
        Locale.setDefault(locale);

        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration,getBaseContext().getResources().getDisplayMetrics());
        pref.storeString("lang",lng);
    }

    private void getBenefits() {
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    benefitsList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Benefits model = ds.getValue(Benefits.class);
                        benefitsList.add(model);
                    }
                    adapter = new MyBenefitsListAdapter(MyBenefitsActivity.this,
                            benefitsList);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showAddBenefitsDialogBox() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View add_view = inflater.inflate(R.layout.add_benefits_dialog_box,null);
        EditText nameTxt = add_view.findViewById(R.id.name);
        EditText priceTxt = add_view.findViewById(R.id.price);
        EditText others = add_view.findViewById(R.id.others);
        String othersTxt = others.getText().toString();
        Spinner spinner = add_view.findViewById(R.id.category);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category = adapterView.getItemAtPosition(i).toString();
                if (category.equals("Miscellaneous")){
                    others.setVisibility(View.VISIBLE);
                }else {
                    others.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        AppCompatButton cancelBtn = add_view.findViewById(R.id.cancel);
        AppCompatButton addBtn = add_view.findViewById(R.id.save);
        builder.setView(add_view);
        AlertDialog alertDialog = builder.create();
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = nameTxt.getText().toString();
                price = priceTxt.getText().toString();
                if (!name.isEmpty() && !price.isEmpty() && !category.isEmpty()){
                    if (category.equals("Miscellaneous")){
                        saveBenefits(name,price,othersTxt);
                    }else {
                        saveBenefits(name,price,category);
                    }
                    alertDialog.dismiss();
                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void saveBenefits(String name, String price, String category) {
        String key = db.push().getKey();
        Benefits benefits = new Benefits(key,name,category,price);
        db.child(key).setValue(benefits);
        getBenefits();
        Toast.makeText(MyBenefitsActivity.this,"Inserted Successfully",Toast.LENGTH_LONG).show();
    }


}