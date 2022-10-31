package com.moutamid.treesme.Hairdresser.ui.chart;


import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.moutamid.treesme.Hairdresser.fragments.MonthRevenueFragment;
import com.moutamid.treesme.Hairdresser.fragments.TodayRevenueFragment;
import com.moutamid.treesme.Hairdresser.fragments.YearRevenueFragment;
import com.moutamid.treesme.R;
import com.moutamid.treesme.SharedPreferencesManager;
import com.moutamid.treesme.databinding.PieChartFragmentBinding;

import java.util.Locale;


public class ChartFragment extends Fragment {

    private PieChartFragmentBinding binding;
    private TextView todayBtn,monthBtn,yearBtn;
    private SharedPreferencesManager pref;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = PieChartFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        todayBtn = root.findViewById(R.id.today);
        monthBtn = root.findViewById(R.id.month);
        yearBtn = root.findViewById(R.id.year);
        getFragmentManager().beginTransaction().replace(R.id.fragment,new TodayRevenueFragment()).commit();
        pref = new SharedPreferencesManager(getActivity());
        getLocale();
        todayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                todayBtn.setTextColor(Color.WHITE);
                todayBtn.setBackgroundResource(R.drawable.button_selected_background);
                monthBtn.setTextColor(Color.BLACK);
                monthBtn.setBackgroundResource(R.drawable.button_unselected);
                yearBtn.setTextColor(Color.BLACK);
                yearBtn.setBackgroundResource(R.drawable.button_unselected);
                getFragmentManager().beginTransaction().replace(R.id.fragment,new TodayRevenueFragment()).commit();
            }
        });
        monthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                monthBtn.setTextColor(Color.WHITE);
                monthBtn.setBackgroundResource(R.drawable.button_selected_background);
                todayBtn.setTextColor(Color.BLACK);
                todayBtn.setBackgroundResource(R.drawable.button_unselected);
                yearBtn.setTextColor(Color.BLACK);
                yearBtn.setBackgroundResource(R.drawable.button_unselected);
                getFragmentManager().beginTransaction().replace(R.id.fragment,new MonthRevenueFragment()).commit();
            }
        });
        yearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                yearBtn.setTextColor(Color.WHITE);
                yearBtn.setBackgroundResource(R.drawable.button_selected_background);
                monthBtn.setTextColor(Color.BLACK);
                monthBtn.setBackgroundResource(R.drawable.button_unselected);
                todayBtn.setTextColor(Color.BLACK);
                todayBtn.setBackgroundResource(R.drawable.button_unselected);
                getFragmentManager().beginTransaction().replace(R.id.fragment,new YearRevenueFragment()).commit();
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}