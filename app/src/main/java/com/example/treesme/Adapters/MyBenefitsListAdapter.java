package com.example.treesme.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.treesme.Model.Benefits;
import com.example.treesme.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MyBenefitsListAdapter extends RecyclerView.Adapter<MyBenefitsListAdapter.MyBenefitsViewHolder>{

    private Context mContext;
    private List<Benefits> benefits;

    public MyBenefitsListAdapter(Context mContext, List<Benefits> benefits) {
        this.mContext = mContext;
        this.benefits = benefits;
    }

    @NonNull
    @Override
    public MyBenefitsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(mContext).inflate(R.layout.my_benefits_custom_layout,parent,false);
        return new MyBenefitsViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull MyBenefitsViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Benefits model = benefits.get(position);
        holder.titleTxt.setText(model.getTitle());
        holder.priceTxt.setText(model.getPrice());
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Benefits").child(mAuth.getCurrentUser().getUid());
                db.child(model.getId()).removeValue();
                notifyItemRemoved(position);
                notifyItemRangeRemoved(position,benefits.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return benefits.size();
    }

    public class MyBenefitsViewHolder extends RecyclerView.ViewHolder{

        TextView titleTxt,priceTxt;
        ImageView deleteBtn;

        public MyBenefitsViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.name);
            priceTxt = itemView.findViewById(R.id.price);
            deleteBtn = itemView.findViewById(R.id.delete);
        }
    }
}
