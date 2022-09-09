package com.example.treesme.User.ui.profile;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.treesme.Adapters.CommunityPostsListAdaper;
import com.example.treesme.Adapters.FavoriteListAdapter;
import com.example.treesme.Adapters.ProductTestingListAdaper;
import com.example.treesme.MessagesActivity;
import com.example.treesme.Model.Favorite;
import com.example.treesme.Model.Hairstyles;
import com.example.treesme.Model.Post;
import com.example.treesme.Model.User;
import com.example.treesme.ModifyProfileScreen;
import com.example.treesme.R;
import com.example.treesme.SharedPreferencesManager;
import com.example.treesme.User.PostDetail;
import com.example.treesme.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    TextView nameTxt;
    ImageView profileImg,favouritBtn,savedBtn;
    Button modifyBtn;
    RecyclerView recyclerView;
    private FirebaseAuth mAuth;
    private DatabaseReference db,db1,likeDB,favoriteDB;
    private SharedPreferencesManager pref;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference().child("Users");
        db1 = FirebaseDatabase.getInstance().getReference().child("Posts");
        likeDB = FirebaseDatabase.getInstance().getReference().child("UserPosts").
                child("Likes").child(mAuth.getCurrentUser().getUid());
        favoriteDB = FirebaseDatabase.getInstance().getReference().child("UserPosts").
                child("Saved").child(mAuth.getCurrentUser().getUid());
        nameTxt = root.findViewById(R.id.username);
        modifyBtn = root.findViewById(R.id.modify);
        profileImg = root.findViewById(R.id.profile);
        favouritBtn = root.findViewById(R.id.favourite);
        savedBtn = root.findViewById(R.id.saved);
        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));
        getUserDetails();
        pref = new SharedPreferencesManager(getActivity());
        getLocale();

        savedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favouritBtn.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                savedBtn.setImageResource(R.drawable.selected_bookmark);
                getSavedList();
            }
        });
        favouritBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favouritBtn.setImageResource(R.drawable.selected_favorite);
                savedBtn.setImageResource(R.drawable.ic_baseline_bookmark_border_24);
                favouriteList();
            }
        });
        modifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ModifyProfileScreen.class);
                startActivity(intent);
            }
        });
        return root;
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
        getActivity().getBaseContext().getResources().updateConfiguration(configuration,getActivity().getBaseContext().getResources().getDisplayMetrics());
        pref.storeString("lang",lng);
    }

    private void favouriteList() {
        ArrayList<Favorite> postList = new ArrayList<>();
        Query query = FirebaseDatabase.getInstance().getReference().child("Favorite")
                .orderByChild("userId").equalTo(mAuth.getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Favorite model = ds.getValue(Favorite.class);
                        postList.add(model);
                    }
                    FavoriteListAdapter adapter = new FavoriteListAdapter(getActivity(),
                            postList);

                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getSavedList() {
        List<Post> postList = new ArrayList<>();
        favoriteDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()){
                        String pId = ds.getKey().toString();
                        db1.child(pId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    Post post = snapshot.getValue(Post.class);
                                    postList.add(post);

                                    CommunityPostsListAdaper adapter = new CommunityPostsListAdaper(getActivity(),
                                            postList);

                                    recyclerView.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void getUserDetails() {
        db.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    User model = snapshot.getValue(User.class);
                    nameTxt.setText(model.getName());
                    Picasso.with(getActivity())
                            .load(model.getImageUrl())
                            .into(profileImg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}