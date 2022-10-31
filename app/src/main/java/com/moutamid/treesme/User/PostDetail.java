package com.moutamid.treesme.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.moutamid.treesme.Model.Post;
import com.moutamid.treesme.Model.User;
import com.moutamid.treesme.R;
import com.moutamid.treesme.SharedPreferencesManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostDetail extends AppCompatActivity {

    private ImageView backImg,postImg,likesImg,savedImg;
    private CircleImageView profileImg;
    private TextView nameTxt,dateTxt,likeTxt,saveTxt;
    private FirebaseAuth mAuth;
    private DatabaseReference db,db1,likeDB,favoriteDB;
    private String dresserId = "";
    private String postId="";
    private SharedPreferencesManager manager;
    private int like_count = 0;
    private boolean isSaved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        backImg = findViewById(R.id.back);
        postImg = findViewById(R.id.post);
        likesImg = findViewById(R.id.likes);
        savedImg = findViewById(R.id.saved);
        saveTxt = findViewById(R.id.save_state);
        nameTxt = findViewById(R.id.username);
        dateTxt = findViewById(R.id.date);
        likeTxt = findViewById(R.id.like_count);
        profileImg = findViewById(R.id.profile);
        postId = getIntent().getStringExtra("pId");
        dresserId = getIntent().getStringExtra("dresserId");
        mAuth = FirebaseAuth.getInstance();
        manager = new SharedPreferencesManager(PostDetail.this);
        getLocale();
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            // startActivity(new Intent(PostDetail.this,MainScreenDashboard.class));
            }
        });
        db1 = FirebaseDatabase.getInstance().getReference().child("Users").child(dresserId);
        db = FirebaseDatabase.getInstance().getReference().child("Posts");
        likeDB = FirebaseDatabase.getInstance().getReference().child("UserPosts").
                child("Likes").child(postId);
        favoriteDB = FirebaseDatabase.getInstance().getReference().child("UserPosts").
                child("Saved").child(mAuth.getCurrentUser().getUid()).child(postId);
        getDresserDetails();
        getPostDetails();
        likesImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveLikes();
            }
        });
        savedImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isSaved){
                    isSaved = false;
                    saveTxt.setText("Save");
                    deletefavorite();
                }else {
                    saveFavorites();
                    isSaved = true;
                    saveTxt.setText("Unsave");
                }
            }
        });

        checkLikes();
        getLikes();
        checkFavorites();
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
    private void checkLikes() {
        likeDB.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    likesImg.setImageResource(R.drawable.selected_favorite);
                }else {
                    likesImg.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void deletefavorite() {
        favoriteDB.removeValue();
        savedImg.setImageResource(R.drawable.ic_baseline_bookmark_border_24);
    }

    private void saveFavorites() {

        favoriteDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    savedImg.setImageResource(R.drawable.selected_bookmark);
                }else {
                    HashMap<String ,String> hashMap = new HashMap<>();
                    hashMap.put("pId",postId);
                    favoriteDB.setValue(hashMap);
                    savedImg.setImageResource(R.drawable.selected_bookmark);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void saveLikes() {
        //Query query = likeDB.orderByChild("uId").equalTo(mAuth.getCurrentUser().getUid());
        likeDB.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    likesImg.setImageResource(R.drawable.selected_favorite);
                }else {
                    HashMap<String ,String> hashMap = new HashMap<>();
                    hashMap.put("uId",mAuth.getCurrentUser().getUid());
                    likeDB.child(mAuth.getCurrentUser().getUid()).setValue(hashMap);
                    getLikes();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getLikes() {

        likeDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()){
                        like_count = (int) ds.getChildrenCount();
                        likeTxt.setText(String.valueOf(like_count) + "Likes");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkFavorites() {
        favoriteDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    savedImg.setImageResource(R.drawable.selected_bookmark);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void getDresserDetails() {
        db1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    User model = snapshot.getValue(User.class);
                    nameTxt.setText(model.getName());
                    Picasso.with(PostDetail.this)
                            .load(model.getImageUrl())
                            .into(profileImg);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getPostDetails() {
        db.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Post post = snapshot.getValue(Post.class);
                    Picasso.with(PostDetail.this)
                            .load(post.getPostUrl())
                            .into(postImg);
                    likeTxt.setText(String.valueOf(like_count));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}