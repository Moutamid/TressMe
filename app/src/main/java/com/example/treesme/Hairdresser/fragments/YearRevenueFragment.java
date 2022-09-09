package com.example.treesme.Hairdresser.fragments;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.example.treesme.Model.Appointments;
import com.example.treesme.Model.Revenue;
import com.example.treesme.R;
import com.example.treesme.SharedPreferencesManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.intrusoft.scatter.ChartData;
import com.intrusoft.scatter.PieChart;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class YearRevenueFragment extends Fragment {

    private PieChart pieChart;
    private TextView modifyObj,priceTxt,myObjTxt;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private int total = 0;
    private Calendar calendar;
    int y, m, d;
    String date;
    String last_date;
    private int objective = 0;
    private SharedPreferencesManager pref;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.chart_date_custom_layout,container,false);
        pieChart = root.findViewById(R.id.piechart);
        modifyObj = root.findViewById(R.id.modify);
        priceTxt = root.findViewById(R.id.price);
        myObjTxt = root.findViewById(R.id.objective);
        pref = new SharedPreferencesManager(getActivity());
        getLocale();
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child("Revenue")
                .child(firebaseUser.getUid());
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,1);
        y=calendar.get(Calendar.YEAR);
        m=calendar.get(Calendar.MONTH)+1;
        d=calendar.get(Calendar.DAY_OF_MONTH)-1;
        date = y+" "+m+" "+d;
        getMyObjective();
        getTotalRevenue();
        modifyObj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showObjectiveDialogBox();
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

    private void getTotalRevenue() {
        Query query= FirebaseDatabase.getInstance().getReference().child("Reversations")
                .orderByChild("dresserId").equalTo(firebaseUser.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Appointments model = ds.getValue(Appointments.class);
                        //   String strTest = model.getStyle();
                        String strSub = model.getStyle().substring(0,model.getStyle().indexOf("-")-1);
                        total += Integer.parseInt(strSub);
                        priceTxt.setText(String.valueOf(total));
                        setupPieChart(total);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getMyObjective() {
        calendar.add(Calendar.YEAR,1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd");
        last_date = sdf.format(calendar.getTime());
        Query query = reference.orderByChild("date").equalTo(last_date);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Revenue model = ds.getValue(Revenue.class);
                        objective = Integer.parseInt(model.getTotal());
                        myObjTxt.setText(model.getTotal());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    List<ChartData> data1;
    int percentage = 0;
    private void setupPieChart(int total) {
        //if (objective != 0){
            percentage = (total*100)/objective;
        //}

        data1 = new ArrayList<>();
        data1.add(new ChartData("", percentage, Color.WHITE, Color.parseColor("#ff0000")));
        data1.add(new ChartData("", 100-percentage, Color.WHITE, Color.parseColor("#cdcdcd")));


        pieChart.setChartData(data1);
        pieChart.partitionWithPercent(true);
    }


    private void showObjectiveDialogBox() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View add_view = inflater.inflate(R.layout.add_revenue_dialog_box,null);
        EditText priceEdTxt = add_view.findViewById(R.id.price);
        AppCompatButton cancelBtn = add_view.findViewById(R.id.cancel);
        AppCompatButton addBtn = add_view.findViewById(R.id.save);
        builder.setView(add_view);
        AlertDialog alertDialog = builder.create();
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String price = priceEdTxt.getText().toString();
                if (!price.isEmpty()){
                    saveRevenue(price);
                    alertDialog.dismiss();
                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void saveRevenue(String price) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR,1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd");
        last_date = sdf.format(c.getTime());
        String key = reference.push().getKey();
        Revenue revenue = new Revenue(key,price,last_date);
        reference.child(key).setValue(revenue);
        myObjTxt.setText(price);
    }

}
