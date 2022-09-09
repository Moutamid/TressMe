package com.example.treesme;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.treesme.AccountsLogin.AccountsRegistrationScreen;
import com.example.treesme.Hairdresser.MainDashboard;
import com.example.treesme.Model.User;
import com.example.treesme.User.MainScreenDashboard;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

public class ModifyProfileScreen extends AppCompatActivity {

    private EditText fnameTxt,emailTxt,phoneTxt,passwordTxt;
    private ImageView profileImg,backImg;
    private AppCompatButton saveBtn,signoutBtn;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference db;
    private static final int PICK_IMAGE_REQUEST = 1;
    Uri uri;
    String imageUrl;
    private TextView languageBtn,faqBtn;
    Bitmap bitmap;
    ProgressDialog dialog;
    private StorageReference mStorage;
    private String type = "";
    private SharedPreferencesManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_profile_screen);
        fnameTxt = findViewById(R.id.fname);
        emailTxt = findViewById(R.id.email);
        phoneTxt = findViewById(R.id.phone);
        backImg = findViewById(R.id.back);
        manager = new SharedPreferencesManager(ModifyProfileScreen.this);
        passwordTxt = findViewById(R.id.password);
        profileImg = findViewById(R.id.profile);
        saveBtn = findViewById(R.id.save);
        signoutBtn = findViewById(R.id.logout);
        faqBtn = findViewById(R.id.faq);
        languageBtn = findViewById(R.id.language);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        dialog = new ProgressDialog(ModifyProfileScreen.this);
        mStorage = FirebaseStorage.getInstance().getReference();
        db = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
        getUserDetails();
        getLocale();
        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("name",fnameTxt.getText().toString());
                hashMap.put("email",emailTxt.getText().toString());
                hashMap.put("phone",phoneTxt.getText().toString());
                hashMap.put("password",passwordTxt.getText().toString());
                hashMap.put("imageUrl",imageUrl);
                db.updateChildren(hashMap);
                Toast.makeText(ModifyProfileScreen.this,"updated successfully",Toast.LENGTH_LONG).show();
            }
        });
        signoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manager.storeString("utype","");
                mAuth.signOut();
                startActivity(new Intent(ModifyProfileScreen.this, AccountsRegistrationScreen.class));
                finish();
            }
        });
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type.equals("user")){
                    startActivity(new Intent(ModifyProfileScreen.this, MainScreenDashboard.class));
                    finish();
                }else {
                    startActivity(new Intent(ModifyProfileScreen.this, MainDashboard.class));
                    finish();
                }
            }
        });
        faqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ModifyProfileScreen.this,FaqActivity.class));
            }
        });

        languageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showLanguageDialogBox();
            }
        });
    }

    private void showLanguageDialogBox() {
        String[] listItems = {"English","French","German"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ModifyProfileScreen.this);
        builder.setTitle("Choose a Language");
        builder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i==0){
                    setLocale("en");
                    recreate();
                }
                else if (i == 1){
                    setLocale("fr");
                    recreate();
                }else if (i == 2){
                    setLocale("de");
                    recreate();
                }

                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

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

    private void getUserDetails() {
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    User model = snapshot.getValue(User.class);
                    type = model.getUserType();
                    fnameTxt.setText(model.getName());
                    emailTxt.setText(model.getEmail());
                    phoneTxt.setText(model.getPhone());
                    passwordTxt.setText(model.getPassword());
                    imageUrl = model.getImageUrl();
                    Picasso.with(ModifyProfileScreen.this)
                            .load(imageUrl)
                            .into(profileImg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"SELECT IMAGE"),PICK_IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK &&
                data != null && data.getData() != null) {
            uri = data.getData();
            profileImg.setImageURI(uri);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            saveInformation();
        }
    }

    private void saveInformation() {
        dialog = new ProgressDialog(ModifyProfileScreen.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setMessage("Uploading your profile....");
        dialog.show();
        if (uri != null) {
            profileImg.setDrawingCacheEnabled(true);
            profileImg.buildDrawingCache();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream);
            byte[] thumb_byte_data = byteArrayOutputStream.toByteArray();

            final StorageReference reference = mStorage.child("Profile Images").child(user.getUid() + ".jpg");
            final UploadTask uploadTask = reference.putBytes(thumb_byte_data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return reference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                imageUrl = downloadUri.toString();
                                dialog.dismiss();
                            }
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                }
            });
        }else {
            Toast.makeText(getApplicationContext(), "Please Select Image ", Toast.LENGTH_LONG).show();
            dialog.dismiss();
        }
    }

}