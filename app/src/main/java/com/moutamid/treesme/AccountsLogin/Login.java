package com.moutamid.treesme.AccountsLogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.moutamid.treesme.Hairdresser.MainDashboard;
import com.moutamid.treesme.R;
import com.moutamid.treesme.SharedPreferencesManager;
import com.moutamid.treesme.User.MainScreenDashboard;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class Login extends AppCompatActivity {

    Button loginBtn;
    TextView forgotPassword;
    private TextInputEditText emailTxt,passwordTxt;
    private String email,password;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference db;
    ProgressDialog pd;
    private String usertype;
    private String type = "";
    private SharedPreferencesManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        manager = new SharedPreferencesManager(Login.this);
        getLocale();
        loginBtn = (Button) findViewById(R.id.login);
        emailTxt = findViewById(R.id.email);
        passwordTxt = findViewById(R.id.password);
        usertype = getIntent().getStringExtra("userType");
        forgotPassword = (TextView) findViewById(R.id.forget);
        db = FirebaseDatabase.getInstance().getReference().child("Users");
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ForgotPassword.class));
                finish();
            }
        });
        mAuth = FirebaseAuth.getInstance();
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginBtn.setClickable(false);
                if(validInfo()) {
                    loginBtn.setClickable(true);
                    pd = new ProgressDialog(Login.this);
                    pd.setMessage("Login....");
                    pd.show();
                    LoginUser();

                }
                else{
                    loginBtn.setClickable(true);
                }

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
    private void LoginUser() {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            pd.dismiss();
                            if (usertype.equals("user")) {
                                manager.storeString("utype",usertype);
                                startActivity(new Intent(getApplicationContext(),
                                        MainScreenDashboard.class));
                                finish();
                            }else {
                                manager.storeString("utype",usertype);
                                startActivity(new Intent(getApplicationContext(),
                                        MainDashboard.class));
                                finish();
                            }
                            Toast.makeText(Login.this, "Logged In",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            pd.dismiss();
                            Toast.makeText(Login.this, "Wrong Email and Password",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        e.printStackTrace();
                    }
                });


    }

    public boolean validInfo() {
        email = emailTxt.getText().toString();
        password = passwordTxt.getText().toString();

        if (email.isEmpty()) {
            emailTxt.setError("Input email!");
            emailTxt.requestFocus();
            return false;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailTxt.setError("Please input valid email!");
            emailTxt.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            passwordTxt.setError("Input password!");
            passwordTxt.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            passwordTxt.setError("password must be atleast 6 character!");
            passwordTxt.requestFocus();
            return false;
        }

        return true;
    }
}