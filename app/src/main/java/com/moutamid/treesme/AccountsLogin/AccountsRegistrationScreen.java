package com.moutamid.treesme.AccountsLogin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.moutamid.treesme.Hairdresser.MainDashboard;
import com.moutamid.treesme.R;
import com.moutamid.treesme.SharedPreferencesManager;
import com.moutamid.treesme.User.MainScreenDashboard;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class AccountsRegistrationScreen extends AppCompatActivity {

    Button loginBtn,signUpBtn,dresserLogin;
    FirebaseAuth mAuth;
    private DatabaseReference db;
    private String utype;
    private SharedPreferencesManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts_registration_screen);
        loginBtn = (Button) findViewById(R.id.login);
        dresserLogin = (Button) findViewById(R.id.logindresser);
        signUpBtn = (Button) findViewById(R.id.signUp);
        manager = new SharedPreferencesManager(AccountsRegistrationScreen.this);
        getLocale();
        utype = manager.retrieveString("utype","");
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference().child("Users");
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AccountsRegistrationScreen.this, Login.class);
                intent.putExtra("userType","user");
                startActivity(intent);
            }
        });
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AccountsRegistrationScreen.this, SignUp.class);
                startActivity(intent);
            }
        });
        dresserLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AccountsRegistrationScreen.this, Login.class);
                intent.putExtra("userType","hair dresser");
                startActivity(intent);
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

    @Override
    protected void onResume() {
        super.onResume();
        if (!utype.equals("")){
            if (utype.equals("user")){
                Intent intent = new Intent(AccountsRegistrationScreen.this, MainScreenDashboard.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }else {
                Intent intent = new Intent(AccountsRegistrationScreen.this, MainDashboard.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        }
    }
}