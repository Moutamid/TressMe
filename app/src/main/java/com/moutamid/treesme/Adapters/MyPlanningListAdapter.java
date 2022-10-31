package com.moutamid.treesme.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moutamid.treesme.R;

import java.util.ArrayList;

public class MyPlanningListAdapter extends RecyclerView.Adapter<MyPlanningListAdapter.MyPlanningViewHolder>{

    private Context mContext;
    private ArrayList<String> headerList;


    public MyPlanningListAdapter(Context mContext, ArrayList<String> headerList) {
        this.mContext = mContext;
        this.headerList = headerList;
    }

    @NonNull
    @Override
    public MyPlanningViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.inside_layout,parent,false);
        return new MyPlanningViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyPlanningViewHolder holder, int position) {
        holder.headerTxt.setText(headerList.get(position));
        String[] items = {"06h 12h","12h 18h","18h 00h"};
        TimeListAdapter adapter= new TimeListAdapter(mContext,items,headerList.get(position));
        holder.listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    @Override
    public int getItemCount() {
        return headerList.size();
    }

    public class MyPlanningViewHolder extends RecyclerView.ViewHolder{

        private TextView headerTxt;
        private ListView listView;

        public MyPlanningViewHolder(@NonNull View itemView) {
            super(itemView);
            headerTxt = itemView.findViewById(R.id.header);
            listView = itemView.findViewById(R.id.listView);
        }
    }
}
