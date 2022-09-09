package com.example.treesme.User.ui.home;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.treesme.Adapters.HairStylesListAdaper;
import com.example.treesme.Adapters.ProductTestingListAdaper;
import com.example.treesme.Adapters.SkincareTipListAdaper;
import com.example.treesme.Model.Hairstyles;
import com.example.treesme.Model.ProductModel;
import com.example.treesme.Model.User;
import com.example.treesme.R;
import com.example.treesme.SharedPreferencesManager;
import com.example.treesme.User.HairDresserStyles;
import com.example.treesme.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private RecyclerView hairStylesList,tipsList,productList;
    private TextView username,hairStyleShow;
    private CircleImageView profileImg;
    private FirebaseAuth mAuth;
    private DatabaseReference db,db1,skinDB,productDB;
    List<Hairstyles> hairstyles;
    private SharedPreferencesManager pref;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        hairStylesList = root.findViewById(R.id.hair_styles_list);
        tipsList = root.findViewById(R.id.skin_tips_list);
        productList = root.findViewById(R.id.product_list);
        username = root.findViewById(R.id.name);
        hairStyleShow = root.findViewById(R.id.show_all);
        profileImg = root.findViewById(R.id.profile);
        pref = new SharedPreferencesManager(getActivity());
        getLocale();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference().child("Users");
        skinDB = FirebaseDatabase.getInstance().getReference().child("SkinTips");
        productDB = FirebaseDatabase.getInstance().getReference().child("Products");
        getUserDetails();
        getHairStyles();
        getSkincareTips();
        getProducts();
        hairStyleShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), HairDresserStyles.class);
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

    private void getHairStyles() {
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(RecyclerView.HORIZONTAL);
        hairStylesList.setLayoutManager(manager);
        hairstyles = new ArrayList<>();
        db1 = FirebaseDatabase.getInstance().getReference().child("Hairstyles");
        db1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Hairstyles model = ds.getValue(Hairstyles.class);
                        hairstyles.add(model);
                    }
                    HairStylesListAdaper adapter = new HairStylesListAdaper(getActivity(),hairstyles);
                    hairStylesList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getSkincareTips() {
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(RecyclerView.HORIZONTAL);
        tipsList.setLayoutManager(manager);
        List<ProductModel> hairstyles = new ArrayList<>();

        skinDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()){
                        ProductModel model = ds.getValue(ProductModel.class);
                        hairstyles.add(model);
                    }
                    SkincareTipListAdaper adapter = new SkincareTipListAdaper(getActivity(),hairstyles);
                    tipsList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getProducts() {
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(RecyclerView.HORIZONTAL);
        productList.setLayoutManager(manager);
        List<ProductModel> hairstyles = new ArrayList<>();

        productDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()){
                        ProductModel model = ds.getValue(ProductModel.class);
                        hairstyles.add(model);
                    }
                    ProductTestingListAdaper adapter = new ProductTestingListAdaper(getActivity(),hairstyles);
                    productList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
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
                    username.setText(model.getName());
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