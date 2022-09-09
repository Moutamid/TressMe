package com.example.treesme.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.treesme.Adapters.BenefitsListAdapter;
import com.example.treesme.Model.Appointments;
import com.example.treesme.Model.Benefits;
import com.example.treesme.Model.User;
import com.example.treesme.R;
import com.example.treesme.SharedPreferencesManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReservationScreen extends AppCompatActivity {

    private TextView dateTxt,timeTxt,fnameTxt,dresserPlace,myPlace;
    private CircleImageView profileImg;
    private AppCompatButton continueBtb;
    private Spinner spinner;
    private List<String> benefits = new ArrayList<>();
    public static final String EXTRAS_USER = "user";
    private User mUser;
    private SharedPreferencesManager manager;

    private String[] hairstyles = {"Tresses (Braids)","Vanilles (Twists)","Crochet Braids",
            "Tissages","Passion twists","Nattes collees","Fausses locks","Cornrows","Locks","Ponytail",
            "Tresses enfants","Lace","Perruque","Extension de cheveux","Maquillage","Soins","Coupes",
            "Lissages","Coiffures","Cauleur","Mariage/Chignon"};
    //String categoryList[] = {"32,000","54,000","18,000","20,000"};
    private DatabaseReference reference;
    private String date ="";
    private String time = "";
    private String place = "";
    private String style = "";
    private FirebaseAuth mAuth;
    private String uId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_screen);
        dateTxt = findViewById(R.id.date);
        timeTxt = findViewById(R.id.time);
        fnameTxt = findViewById(R.id.username);
        dresserPlace = findViewById(R.id.dresser_place);
        myPlace = findViewById(R.id.my_place);
        profileImg = findViewById(R.id.profile);
        continueBtb = findViewById(R.id.save);
        spinner = findViewById(R.id.items);
        mUser = getIntent().getParcelableExtra(EXTRAS_USER);
        manager = new SharedPreferencesManager(ReservationScreen.this);
        getLocale();
        getBenefitLists();
        reference = FirebaseDatabase.getInstance().getReference().child("Reversations");
        mAuth = FirebaseAuth.getInstance();
        uId = mAuth.getCurrentUser().getUid();
        fnameTxt.setText(mUser.getName());
        Picasso.with(ReservationScreen.this)
                .load(mUser.getImageUrl())
                .into(profileImg);
        dateTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH);
                int dd = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker = new DatePickerDialog(ReservationScreen.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        int mon= monthOfYear+1;
                        date = dayOfMonth+"/"+mon+"/"+year;
                        dateTxt.setText(date);
                    }
                }, yy, mm, dd);
                datePicker.show();
            }
        });
        timeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(ReservationScreen.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                time = hourOfDay + ":" + minute;
                                timeTxt.setText(time);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();

            }
        });
        dresserPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dresserPlace.setTextColor(Color.WHITE);
                dresserPlace.setBackgroundResource(R.drawable.button_selected_background);
                myPlace.setTextColor(Color.BLACK);
                myPlace.setBackgroundResource(R.drawable.button_unselected);
                place = "dresser place";
            }
        });
        myPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                myPlace.setTextColor(Color.WHITE);
                myPlace.setBackgroundResource(R.drawable.button_selected_background);
                dresserPlace.setTextColor(Color.BLACK);
                dresserPlace.setBackgroundResource(R.drawable.button_unselected);
                place = "my place";
            }
        });
        continueBtb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!date.isEmpty() && !time.isEmpty() &&
                        !style.isEmpty() && !place.isEmpty()){
                    saveAppointments();
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
    private void getBenefitLists() {
        DatabaseReference db2 = FirebaseDatabase.getInstance().getReference().child("Benefits");
        db2.child(mUser.getuId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Benefits model = ds.getValue(Benefits.class);
                        benefits.add(model.getPrice()+" - "+model.getTitle());
                    }
                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(ReservationScreen.this,
                            android.R.layout.simple_spinner_dropdown_item, benefits);
                    spinner.setAdapter(spinnerArrayAdapter);
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            style = adapterView.getItemAtPosition(i).toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void saveAppointments() {
        String key = reference.push().getKey();
        Appointments model = new Appointments(key,style,mUser.getuId(),time,date,uId,place);
        reference.child(key).setValue(model);
        Toast.makeText(ReservationScreen.this,"Reservation done!",Toast.LENGTH_LONG).show();
        startActivity(new Intent(ReservationScreen.this,MainScreenDashboard.class));
        finish();
    }
}