package com.moutamid.treesme.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.moutamid.treesme.Model.Planning;
import com.moutamid.treesme.R;
import com.moutamid.treesme.SharedPreferencesManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class TimeListAdapter extends BaseAdapter {

    private Context ctx;
    private String[] time;
    private LayoutInflater inflter;
    private String date;
    private boolean isEnable;
    private SharedPreferencesManager manager;

    public TimeListAdapter(Context ctx, String[] time, String date) {
        this.ctx = ctx;
        this.time = time;
        inflter = (LayoutInflater.from(ctx));
        this.date = date;
        manager = new SharedPreferencesManager(ctx);
    }

    @Override
    public int getCount() {
        return time.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.parent_layout, null); // inflate the layout
        TextView timeTxt = view.findViewById(R.id.time);
        Switch enable = view.findViewById(R.id.enable);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String uId = mAuth.getCurrentUser().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Planning");
        String key = reference.push().getKey();
        timeTxt.setText(time[i]);
        isEnable = manager.retrieveBoolean("isEnable",false);
        //if (isEnable){
            checkEnabledTime(time[i],date,enable);
        //}
        enable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Planning model = new Planning(key,time[i],date,uId);
                reference.child(key).setValue(model);
            }
        });
        return view;
    }

    private void savePlanning(String time, String date, Switch enable) {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String uId = mAuth.getCurrentUser().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Planning");
        String key = reference.push().getKey();
        Query query = FirebaseDatabase.getInstance().getReference().child("Planning")
                .orderByChild("dresserId").equalTo(uId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Planning model = ds.getValue(Planning.class);
                        if (model.getTime().equals(time) && model.getDate().equals(date)){
                            //enable.setEnabled(true);
                            enable.setChecked(true);
                        }else {
                            Planning models = new Planning(key,time,date,uId);
                            reference.child(key).setValue(models);
                            //manager.storeBoolean("isEnable",true);
                        }
                    }
                }else {

                    Planning model = new Planning(key,time,date,uId);
                    reference.child(key).setValue(model);
                    //manager.storeBoolean("isEnable",true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkEnabledTime(String time,String date, Switch enable) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String uId = mAuth.getCurrentUser().getUid();

        Query query = FirebaseDatabase.getInstance().getReference().child("Planning")
                .orderByChild("dresserId").equalTo(uId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Planning model = ds.getValue(Planning.class);
                        if (model.getTime().equals(time) && model.getDate().equals(date)){
                            //enable.setEnabled(true);
                            enable.setChecked(true);
                        }
                    }
                }else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
