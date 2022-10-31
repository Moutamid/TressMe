package com.moutamid.treesme.Hairdresser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.moutamid.treesme.Adapters.MyPlanningListAdapter;
import com.moutamid.treesme.R;
import com.moutamid.treesme.SharedPreferencesManager;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MyPlanningActivity extends AppCompatActivity {

    private TextView pickdateTxt;
    private DatePickerDialog.OnDateSetListener mDatePickerListener;
    private ImageView backArrow, forwardArrow;
    private int MONTH_VALUE=0;
    private RecyclerView recyclerView;
    private ImageView bacImg;
    private Calendar calendar;
    private ArrayList<String> dateList;
    private int y,m;
    private SharedPreferencesManager pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_planning);
        bacImg = findViewById(R.id.back);
        pickdateTxt = findViewById(R.id.dateText);
        forwardArrow = findViewById(R.id.forwardDay);
        backArrow = findViewById(R.id.backwardDate);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(MyPlanningActivity.this);
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(manager);
        pref = new SharedPreferencesManager(MyPlanningActivity.this);
        getLocale();
        dateList = new ArrayList<>();
        calendar = Calendar.getInstance();
        pickdateTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH)-1;
                int dd = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker = new DatePickerDialog(MyPlanningActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String[] mons = new DateFormatSymbols(Locale.ENGLISH).getShortMonths();
                        pickdateTxt.setText(mons[monthOfYear] +" "+year);
                        getDates();
                    }
                }, yy, mm, dd);
                datePicker.show();

            }
        });

        bacImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyPlanningActivity.this,MainDashboard.class));
            }
        });
        initialize();
    }
    private void initialize() {
        getBackDate();
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MONTH_VALUE--;
                getBackDate();
            }
        });
        forwardArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MONTH_VALUE++;
                getBackDate();
            }
        });
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
        getBaseContext().getResources().updateConfiguration(configuration,getBaseContext().getResources().getDisplayMetrics());
        pref.storeString("lang",lng);
    }
    private void getBackDate(){
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, MONTH_VALUE);

        String[] mons = new DateFormatSymbols(Locale.ENGLISH).getShortMonths();
        // int month = calendar.get(Calendar.MONDAY);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd");
        //calendar.add(Calendar.MONTH,-1);
        //  last_date = sdf.format(calendar.getTime());
        pickdateTxt.setText(mons[month-1] + " " + year);
        getDates();
    }

    private void getDates() {
        dateList.clear();
        int days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i=1; i<=days; i++) {
            calendar.add(Calendar.DATE, 1);

            SimpleDateFormat curFormater = new SimpleDateFormat("EEEE, d MMMM");
            dateList.add(curFormater.format(calendar.getTime()));
        }

        MyPlanningListAdapter adapter = new MyPlanningListAdapter(MyPlanningActivity.this,dateList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

}