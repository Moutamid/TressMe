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
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.moutamid.treesme.Model.User;
import com.moutamid.treesme.ProfileImage;
import com.moutamid.treesme.R;
import com.moutamid.treesme.SharedPreferencesManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class SignUp extends AppCompatActivity {

    Button signUpBtn;
    private TextInputEditText nameTxt,emailTxt,phoneTxt,passwordTxt,cpasswordTxt;
    private String name,email,password,phone,cpass;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference db;
    private Switch dresserOn;
    ProgressDialog pd;
    private String userType = "user";
    private SharedPreferencesManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        signUpBtn = (Button) findViewById(R.id.signUp);
        manager = new SharedPreferencesManager(SignUp.this);
        getLocale();
        nameTxt = findViewById(R.id.name);
        emailTxt = findViewById(R.id.email);
        phoneTxt = findViewById(R.id.phone);
        passwordTxt = findViewById(R.id.password);
        cpasswordTxt = findViewById(R.id.cpassword);
        dresserOn = findViewById(R.id.dresserOn);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference().child("Users");
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUpBtn.setClickable(false);
                if(validInfo()) {
                    signUpBtn.setClickable(true);
                    pd = new ProgressDialog(SignUp.this);
                    pd.setMessage("Creating Account....");
                    pd.show();
                    registerUser();

                }
                else{
                    signUpBtn.setClickable(true);
                }

            }
        });
        dresserOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    userType = "hair dresser";
                }else {
                    userType = "user";
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
    private void registerUser() {
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            if (mAuth.getCurrentUser() != null){
                                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                manager.storeString("utype",userType);
                                User model = new User(firebaseUser.getUid(),userType,name,email,phone,
                                        password,"","");
                                db.child(firebaseUser.getUid()).setValue(model);
                                sendActivityToProfileImage();
                                pd.dismiss();
                            }
                        }
                    }
                });

    }

    private void sendActivityToProfileImage() {
        Intent intent = new Intent(SignUp.this, ProfileImage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("type",userType);
        startActivity(intent);
        finish();
    }

    //Validate Input Fields
    public boolean validInfo() {

        email = emailTxt.getText().toString();
        password = passwordTxt.getText().toString();
        name = nameTxt.getText().toString();
        phone = phoneTxt.getText().toString();
        cpass = cpasswordTxt.getText().toString();

        if (name.isEmpty()) {
            nameTxt.setError("Input Name!");
            nameTxt.requestFocus();
            return false;
        }

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
        if (phone.isEmpty()) {
            phoneTxt.setError("Input phone!");
            phoneTxt.requestFocus();
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
        if (cpass.isEmpty()) {
            cpasswordTxt.setError("Input Confirm password!");
            cpasswordTxt.requestFocus();
            return false;
        }
        if (!password.equals(cpass)) {
            Toast.makeText(SignUp.this,"Passwords not matched!",Toast.LENGTH_LONG).
                    show();
            return false;
        }
        return true;
    }
}