package com.moutamid.treesme.Hairdresser.ui.profile;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.moutamid.treesme.Adapters.BenefitsListAdapter;
import com.moutamid.treesme.Adapters.CommunityPostsListAdaper;
import com.moutamid.treesme.Model.Benefits;
import com.moutamid.treesme.Model.Post;
import com.moutamid.treesme.Model.User;
import com.moutamid.treesme.ModifyProfileScreen;
import com.moutamid.treesme.R;
import com.moutamid.treesme.SharedPreferencesManager;
import com.moutamid.treesme.databinding.FragmentHairDresserProfileBinding;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.taufiqrahman.reviewratings.BarLabels;
import com.taufiqrahman.reviewratings.RatingReviews;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class ProfileFragment extends Fragment {

    private FragmentHairDresserProfileBinding binding;
    private ImageView addImg, profileImg;
    private TextView username, type, card, aboutBtn, reviewBtn, photosBtn;
    private RecyclerView benefits_list, photos_list;
    private EditText bioTxt;
    private DatabaseReference db;
    private ImageView addPost;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Bitmap bitmap;
    private Button saveBio;
    private LinearLayout about_layout, photo_layout, review_layout;
    private List<Benefits> benefits = new ArrayList<>();
    private String uId;
    private ProgressDialog dialog;
    private static final int PICK_IMAGE_REQUEST = 1;
    Uri uri;
    StorageReference mStorage;
    private RatingReviews ratingReviews;
    private String category;
    private String[] hairstyles = {"Tresses (Braids)","Vanilles (Twists)","Crochet Braids",
    "Tissages","Passion twists","Nattes collees","Fausses locks","Cornrows","Locks","Ponytail",
    "Tresses enfants","Lace","Perruque","Extension de cheveux","Maquillage","Soins","Coupes",
    "Lissages","Coiffures","Cauleur","Mariage/Chignon"};
    private DatabaseReference db1,db2;
    private SharedPreferencesManager pref;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHairDresserProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        addImg = root.findViewById(R.id.edit);
        profileImg = root.findViewById(R.id.profile);
        username = root.findViewById(R.id.username);
        addPost = root.findViewById(R.id.add);
        type = root.findViewById(R.id.type);
        card = root.findViewById(R.id.card);
        bioTxt = root.findViewById(R.id.bio);
        saveBio = root.findViewById(R.id.add_bio);
        aboutBtn = root.findViewById(R.id.aboutBtn);
        reviewBtn = root.findViewById(R.id.reviewBtn);
        photosBtn = root.findViewById(R.id.photoBtn);
        db = FirebaseDatabase.getInstance().getReference().child("Users");
        db1 = FirebaseDatabase.getInstance().getReference().child("Posts");
        db2 = FirebaseDatabase.getInstance().getReference().child("Benefits");
        pref = new SharedPreferencesManager(getActivity());
        getLocale();
        mAuth = FirebaseAuth.getInstance();
        uId = mAuth.getCurrentUser().getUid();
        mStorage = FirebaseStorage.getInstance().getReference();
        bioTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0){
                    saveBio.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        saveBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bio =bioTxt.getText().toString();
                if (!bio.isEmpty()){
                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put("bio",bio);
                    db.child(uId).updateChildren(hashMap);
                    saveBio.setVisibility(View.GONE);
                }
            }
        });
        benefits_list = root.findViewById(R.id.benfits_list);
        photos_list = root.findViewById(R.id.photo_list);
        about_layout = root.findViewById(R.id.about_layout);
        photo_layout = root.findViewById(R.id.photo_layout);
        review_layout = root.findViewById(R.id.review_layout);
        ratingReviews = (RatingReviews) root.findViewById(R.id.rating_reviews);
        getUserDetails();
        addImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ModifyProfileScreen.class);
                startActivity(intent);
            }
        });
        aboutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aboutBtn.setTextColor(Color.WHITE);
                aboutBtn.setBackgroundResource(R.drawable.button_selected_background);
                reviewBtn.setTextColor(Color.BLACK);
                reviewBtn.setBackgroundResource(R.drawable.button_unselected);
                photosBtn.setTextColor(Color.BLACK);
                photosBtn.setBackgroundResource(R.drawable.button_unselected);
                about_layout.setVisibility(View.VISIBLE);
                review_layout.setVisibility(View.GONE);
                photo_layout.setVisibility(View.GONE);
                getBenefitsList();
            }
        });
        reviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reviewBtn.setTextColor(Color.WHITE);
                reviewBtn.setBackgroundResource(R.drawable.button_selected_background);
                aboutBtn.setTextColor(Color.BLACK);
                aboutBtn.setBackgroundResource(R.drawable.button_unselected);
                photosBtn.setTextColor(Color.BLACK);
                photosBtn.setBackgroundResource(R.drawable.button_unselected);
                about_layout.setVisibility(View.GONE);
                review_layout.setVisibility(View.VISIBLE);
                photo_layout.setVisibility(View.GONE);
                getReviews();
            }
        });

        photosBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photosBtn.setTextColor(Color.WHITE);
                photosBtn.setBackgroundResource(R.drawable.button_selected_background);
                aboutBtn.setTextColor(Color.BLACK);
                aboutBtn.setBackgroundResource(R.drawable.button_unselected);
                reviewBtn.setTextColor(Color.BLACK);
                reviewBtn.setBackgroundResource(R.drawable.button_unselected);
                about_layout.setVisibility(View.GONE);
                review_layout.setVisibility(View.GONE);
                photo_layout.setVisibility(View.VISIBLE);
                getPhotos();
            }
        });

        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHairstyles();
                //openGallery();
            }
        });

        return root;
    }

    private void showHairstyles() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View add_view = inflater.inflate(R.layout.hairstyle_layout,null);
        ListView listView = add_view.findViewById(R.id.listView);
        @SuppressLint("ResourceType") ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.category_list_layout,R.id.text,hairstyles);
        listView.setAdapter(adapter);
        builder.setView(add_view);
        AlertDialog alertDialog = builder.create();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                category = adapterView.getItemAtPosition(i).toString();
                openGallery();
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void getReviews() {


        int colors[] = new int[]{
                Color.parseColor("#0e9d58"),
                Color.parseColor("#bfd047"),
                Color.parseColor("#ffc105"),
                Color.parseColor("#ef7e14"),
                Color.parseColor("#d36259")};

        int raters[] = new int[]{
                new Random().nextInt(100),
                new Random().nextInt(100),
                new Random().nextInt(100),
                new Random().nextInt(100),
                new Random().nextInt(100)
        };

        ratingReviews.createRatingBars(100, BarLabels.STYPE1, colors, raters);
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

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"SELECT IMAGE"),PICK_IMAGE_REQUEST);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK &&
                data != null && data.getData() != null) {
            uri = data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                saveInformation();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void saveInformation() {
        dialog = new ProgressDialog(getActivity());
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setMessage("Uploading My Post....");
        dialog.show();
        if (uri != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream);
            byte[] thumb_byte_data = byteArrayOutputStream.toByteArray();

            final StorageReference reference = mStorage.child("Post").child(System.currentTimeMillis() + ".jpg");
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
                                String key = db1.push().getKey();
                                Post post = new Post(key,downloadUri.toString(),category,System.currentTimeMillis(),
                                        uId,0,false);
                                db1.child(key).setValue(post);
                                getPhotos();
                                Toast.makeText(getActivity(), "upload successfully: ", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }else {
            Toast.makeText(getActivity(), "Please Select Image ", Toast.LENGTH_LONG).show();

        }
    }
    private void getUserDetails() {
        db.child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    User model = snapshot.getValue(User.class);
                    username.setText(model.getName());
                    Picasso.with(getActivity())
                            .load(model.getImageUrl())
                            .into(profileImg);
                    bioTxt.setText(model.getBio());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getBenefitsList() {
     //   Toast.makeText(getActivity(),"Hello",Toast.LENGTH_LONG).show();
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(RecyclerView.VERTICAL);
        benefits_list.setLayoutManager(manager);
        db2.child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    benefits.clear();
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Benefits model = ds.getValue(Benefits.class);
                        benefits.add(model);
                    }
                    BenefitsListAdapter adapter = new BenefitsListAdapter(getActivity(),benefits,null);
                    benefits_list.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getPhotos() {
        ArrayList<Post> postList = new ArrayList<>();
        photos_list.setLayoutManager(new GridLayoutManager(getActivity(),3));
        Query query = db1.orderByChild("dresserId").equalTo(uId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Post model = ds.getValue(Post.class);
                        postList.add(model);
                    }
                    CommunityPostsListAdaper adapter = new CommunityPostsListAdaper(getActivity(),
                            postList);

                    photos_list.setAdapter(adapter);
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