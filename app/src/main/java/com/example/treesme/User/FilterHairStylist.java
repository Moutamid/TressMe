package com.example.treesme.User;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.treesme.Model.Appointments;
import com.example.treesme.R;
import com.example.treesme.SharedPreferencesManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FilterHairStylist extends AppCompatActivity {

    EditText addressTxt;
    TextView todayTxt,tomorrowTxt,dateTxt,timeTxt1,timeTxt2,timeTxt3;
    AppCompatButton saveBtn;
    String title;
    //DatabaseReference db;
    ImageView backImg;
    FirebaseAuth mAuth;
    FirebaseUser user;
    String time="";
    String date="";
    String address = "";
    private SharedPreferencesManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_hair_stylist);
        addressTxt=findViewById(R.id.address);
        todayTxt=findViewById(R.id.today);
        tomorrowTxt=findViewById(R.id.tomorrow);
        dateTxt=findViewById(R.id.select);
        timeTxt1=findViewById(R.id.time1);
        timeTxt2=findViewById(R.id.time2);
        timeTxt3=findViewById(R.id.time3);
        saveBtn=findViewById(R.id.save);
        backImg = findViewById(R.id.back);
        manager = new SharedPreferencesManager(FilterHairStylist.this);
        getLocale();
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FilterHairStylist.this, MainScreenDashboard.class);
                startActivity(intent);
            }
        });
        title = getIntent().getStringExtra("name");
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
       // db = FirebaseDatabase.getInstance().getReference().child("Appointments").child(user.getUid());
        dateTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH);
                int dd = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker = new DatePickerDialog(FilterHairStylist.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String[] mons = new DateFormatSymbols(Locale.ENGLISH).getMonths();
                        SimpleDateFormat curFormater = new SimpleDateFormat("EEEE");
                        Date selected_date = new Date(year,monthOfYear,dayOfMonth-1);
                        date = curFormater.format(selected_date) + ", " + dayOfMonth + " " + mons[monthOfYear];
                        dateTxt.setText(date);
                    }
                }, yy, mm, dd);
                datePicker.show();
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                address = addressTxt.getText().toString();
                if (!address.isEmpty() && !time.isEmpty() && !date.isEmpty()){
                    saveAppointments();
                }
            }
        });
        todayTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH);
                int dd = calendar.get(Calendar.DAY_OF_MONTH);
            //    date = dd+"/"+mm+"/"+yy;
                String[] mons = new DateFormatSymbols(Locale.ENGLISH).getMonths();
                SimpleDateFormat curFormater = new SimpleDateFormat("EEEE");
                Date selected_date = new Date(yy,mm,dd-1);
                date = curFormater.format(selected_date) + ", " + dd + " " + mons[mm];
                todayTxt.setBackgroundResource(R.drawable.selected_input);
                tomorrowTxt.setBackgroundResource(R.drawable.input);
            }
        });
        tomorrowTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH);
                int dd = calendar.get(Calendar.DAY_OF_MONTH)+1;
                //date = dd+"/"+mm+"/"+yy;
                String[] mons = new DateFormatSymbols(Locale.ENGLISH).getMonths();
                SimpleDateFormat curFormater = new SimpleDateFormat("EEEE");
                Date selected_date = new Date(yy,mm,dd-1);
                date = curFormater.format(selected_date) + ", " + dd + " " + mons[mm];
                todayTxt.setBackgroundResource(R.drawable.input);
                tomorrowTxt.setBackgroundResource(R.drawable.selected_input);
            }
        });
        timeTxt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time = timeTxt1.getText().toString();
                timeTxt1.setBackgroundResource(R.drawable.selected_input);
                timeTxt2.setBackgroundResource(R.drawable.input);
                timeTxt3.setBackgroundResource(R.drawable.input);
            }
        });
        timeTxt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time = timeTxt2.getText().toString();
                timeTxt1.setBackgroundResource(R.drawable.input);
                timeTxt2.setBackgroundResource(R.drawable.selected_input);
                timeTxt3.setBackgroundResource(R.drawable.input);
            }
        });
        timeTxt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time = timeTxt3.getText().toString();
                timeTxt1.setBackgroundResource(R.drawable.input);
                timeTxt2.setBackgroundResource(R.drawable.input);
                timeTxt3.setBackgroundResource(R.drawable.selected_input);
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
    private void saveAppointments() {
        Intent intent = new Intent(FilterHairStylist.this, HairDresserInfo.class);
        intent.putExtra("style",title);
        intent.putExtra("time",time);
        intent.putExtra("date",date);
        startActivity(intent);
    }

    public String getDayName(int day) {
        switch (day) {
            case Calendar.SATURDAY:
                return "Saturday";
            case Calendar.SUNDAY:
                return "Sunday";
            case Calendar.MONDAY:
                return "Monday";
            case Calendar.TUESDAY:
                return "Tuesday";
            case Calendar.WEDNESDAY:
                return "Wednesday";
            case Calendar.THURSDAY:
                return "Thursday";
            case Calendar.FRIDAY:
                return "Friday";
            default:
                return "Sunday";
        }
    }

}