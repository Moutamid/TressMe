package com.example.treesme.User.ui.community;


import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.treesme.Adapters.CommunityPostsListAdaper;
import com.example.treesme.Adapters.HairStylesGridListAdapter;
import com.example.treesme.Model.Hairstyles;
import com.example.treesme.Model.Post;
import com.example.treesme.R;
import com.example.treesme.SharedPreferencesManager;
import com.example.treesme.databinding.FragmentCommunityBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class CommunityFragment extends Fragment {

    private FragmentCommunityBinding binding;
    private RecyclerView stylesList;
    private RecyclerView postsList;
    DatabaseReference db1;
    List<Post> hairstyles;
    List<Hairstyles> hairstylesList;
    private FirebaseAuth mAuth;
    private SharedPreferencesManager pref;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentCommunityBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        stylesList = root.findViewById(R.id.hair_styles_list);
        postsList = root.findViewById(R.id.post_gridView);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(RecyclerView.HORIZONTAL);
        stylesList.setLayoutManager(manager);
        hairstylesList = new ArrayList<>();
        pref = new SharedPreferencesManager(getActivity());
        getLocale();

        mAuth = FirebaseAuth.getInstance();
        getHairStyles();
        getPostList();
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


    private void getPostList() {
        postsList.setLayoutManager(new GridLayoutManager(getActivity(),3));
        ArrayList<Post> postList = new ArrayList<>();
        db1 = FirebaseDatabase.getInstance().getReference().child("Posts");
        db1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Post model = ds.getValue(Post.class);
                        postList.add(model);
                    }
                    CommunityPostsListAdaper adapter = new CommunityPostsListAdaper(getActivity(),
                            postList);

                    postsList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getHairStyles() {
        db1 = FirebaseDatabase.getInstance().getReference().child("Hairstyles");
        db1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    hairstylesList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Hairstyles model = ds.getValue(Hairstyles.class);
                        hairstylesList.add(model);
                    }
                    HairStylesGridListAdapter adapter = new HairStylesGridListAdapter(getActivity(),
                            hairstylesList);

                    stylesList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
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