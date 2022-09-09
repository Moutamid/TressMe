package com.example.treesme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.treesme.Adapters.BenefitsListAdapter;
import com.example.treesme.Adapters.CommunityPostsListAdaper;
import com.example.treesme.Model.Appointments;
import com.example.treesme.Model.Benefits;
import com.example.treesme.Model.Hairstyles;
import com.example.treesme.Model.Post;
import com.example.treesme.Model.Review;
import com.example.treesme.Model.User;
import com.example.treesme.User.FilterHairStylist;
import com.example.treesme.User.HairDresserInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

public class HairDresserProfile extends AppCompatActivity {

    private ImageView chatImg,profileImg;
    private TextView username,type,card,benefitsBtn,availibalityBtn,photosBtn;
    private RecyclerView benefits_list,photos_list;
    private TextView timeTxt1,timeTxt2,timeTxt3;
    private DatabaseReference db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private LinearLayout availability_layout;
    private User users;
    private ArrayList<Benefits> benefits = new ArrayList<>();
    private String order_type;
    private DatabaseReference db1;
    private Button saveBtn;
    private String time="";
    private String date="";
    private SharedPreferencesManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hair_dresser_profile);

        chatImg = findViewById(R.id.chat);
        profileImg = findViewById(R.id.profile);
        username = findViewById(R.id.username);
        type = findViewById(R.id.type);
        card = findViewById(R.id.card);
        benefitsBtn = findViewById(R.id.benefitBtn);
        availibalityBtn = findViewById(R.id.availBtn);
        photosBtn = findViewById(R.id.photoBtn);
        manager = new SharedPreferencesManager(HairDresserProfile.this);
        benefits_list = findViewById(R.id.benfits_list);
        photos_list = findViewById(R.id.photo_list);
        availability_layout = findViewById(R.id.available);
        timeTxt1 = findViewById(R.id.time1);
        timeTxt2 = findViewById(R.id.time2);
        timeTxt3 = findViewById(R.id.time3);

        saveBtn = (Button)findViewById(R.id.save);
        getLocale();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        users = getIntent().getParcelableExtra("user");
        order_type = getIntent().getStringExtra("type");
        db1 = FirebaseDatabase.getInstance().getReference().child("Posts");
        username.setText(users.getName());
        Picasso.with(HairDresserProfile.this)
                .load(users.getImageUrl())
                .into(profileImg);
        type.setText(order_type);

        chatImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HairDresserProfile.this,MessagesActivity.class);
                intent.putExtra(MessagesActivity.EXTRAS_USER, users);
                intent.putExtra("userUid", users.getuId());
                startActivity(intent);
            }
        });

        benefitsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                benefitsBtn.setTextColor(Color.WHITE);
                benefitsBtn.setBackgroundResource(R.drawable.button_selected_background);
                availibalityBtn.setTextColor(Color.BLACK);
                availibalityBtn.setBackgroundResource(R.drawable.button_unselected);
                photosBtn.setTextColor(Color.BLACK);
                photosBtn.setBackgroundResource(R.drawable.button_unselected);
                benefits_list.setVisibility(View.VISIBLE);
                availability_layout.setVisibility(View.GONE);
                photos_list.setVisibility(View.GONE);
                getBenefitsList();
            }
        });

        availibalityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                availibalityBtn.setTextColor(Color.WHITE);
                availibalityBtn.setBackgroundResource(R.drawable.button_selected_background);
                benefitsBtn.setTextColor(Color.BLACK);
                benefitsBtn.setBackgroundResource(R.drawable.button_unselected);
                photosBtn.setTextColor(Color.BLACK);
                photosBtn.setBackgroundResource(R.drawable.button_unselected);
                benefits_list.setVisibility(View.GONE);
                availability_layout.setVisibility(View.VISIBLE);
                photos_list.setVisibility(View.GONE);
            }
        });

        photosBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photosBtn.setTextColor(Color.WHITE);
                photosBtn.setBackgroundResource(R.drawable.button_selected_background);
                benefitsBtn.setTextColor(Color.BLACK);
                benefitsBtn.setBackgroundResource(R.drawable.button_unselected);
                availibalityBtn.setTextColor(Color.BLACK);
                availibalityBtn.setBackgroundResource(R.drawable.button_unselected);
                benefits_list.setVisibility(View.GONE);
                availability_layout.setVisibility(View.GONE);
                photos_list.setVisibility(View.VISIBLE);
                getPhotos();
            }
        });
     //   getPhotos();
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -1);

        /* ends after 1 month from now */
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 1);

        // on below line we are setting up our horizontal calendar view and passing id our calendar view to it.
        HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(this, R.id.calendarView)
                // on below line we are adding a range
                // as start date and end date to our calendar.
                .range(startDate, endDate)
                // on below line we are providing a number of dates
                // which will be visible on the screen at a time.
                .datesNumberOnScreen(5)
                // at last we are calling a build method
                // to build our horizontal recycler view.
                .build();
        // on below line we are setting calendar listener to our calendar view.
        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar dates, int position) {
                // on below line we are printing date
                // in the logcat which is selected.
                String[] mons = new DateFormatSymbols(Locale.ENGLISH).getShortMonths();
                int month = dates.get(Calendar.MONTH);
                int day = dates.get(Calendar.DAY_OF_MONTH)+1;
                date = mons[month] + " "+ day;
                //Toast.makeText(HairDresserProfile.this,date,Toast.LENGTH_LONG).show();
            }
        });
        timeTxt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                time = timeTxt1.getText().toString();
                timeTxt1.setBackgroundResource(R.drawable.selected_input);
                timeTxt2.setBackgroundResource(R.drawable.input);
                timeTxt3.setBackgroundResource(R.drawable.input);
                if (!date.isEmpty()){
                    Intent intent = new Intent(HairDresserProfile.this,MessagesActivity.class);
                    intent.putExtra(MessagesActivity.EXTRAS_USER, users);
                    intent.putExtra("userUid", users.getuId());
                    intent.putExtra("message","Are you available on" + date+ "("+time+")");
                    startActivity(intent);
                }
            }
        });
        timeTxt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time = timeTxt2.getText().toString();
                timeTxt1.setBackgroundResource(R.drawable.input);
                timeTxt2.setBackgroundResource(R.drawable.selected_input);
                timeTxt3.setBackgroundResource(R.drawable.input);
                if (!date.isEmpty()){
                    Intent intent = new Intent(HairDresserProfile.this,MessagesActivity.class);
                    intent.putExtra(MessagesActivity.EXTRAS_USER, users);
                    intent.putExtra("userUid", users.getuId());
                    intent.putExtra("message","Are you available on" + date+ "("+time+")");
                    startActivity(intent);
                }
            }
        });
        timeTxt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time = timeTxt3.getText().toString();
                timeTxt1.setBackgroundResource(R.drawable.input);
                timeTxt2.setBackgroundResource(R.drawable.input);
                timeTxt3.setBackgroundResource(R.drawable.selected_input);
                if (!date.isEmpty()){
                    Intent intent = new Intent(HairDresserProfile.this,MessagesActivity.class);
                    intent.putExtra(MessagesActivity.EXTRAS_USER, users);
                    intent.putExtra("userUid", users.getuId());
                    intent.putExtra("message","Hello "+users.getName()+", are you available on " + date+ "("+time+")");
                    startActivity(intent);
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
    private void getPhotos() {
        ArrayList<Post> postList = new ArrayList<>();
        photos_list.setLayoutManager(new GridLayoutManager(HairDresserProfile.this,3));
        Query query = db1.orderByChild("dresserId").equalTo(users.getuId());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Post model = ds.getValue(Post.class);
                        postList.add(model);
                    }
                    CommunityPostsListAdaper adapter = new CommunityPostsListAdaper(HairDresserProfile.this,
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

    private void getBenefitsList() {
        LinearLayoutManager manager = new LinearLayoutManager(HairDresserProfile.this);
        manager.setOrientation(RecyclerView.VERTICAL);
        benefits_list.setLayoutManager(manager);
        DatabaseReference db2 = FirebaseDatabase.getInstance().getReference().child("Benefits");
        db2.child(users.getuId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    benefits.clear();
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Benefits model = ds.getValue(Benefits.class);
                        benefits.add(model);
                    }
                    BenefitsListAdapter adapter = new BenefitsListAdapter(HairDresserProfile.this,
                            benefits,users);
                    benefits_list.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}