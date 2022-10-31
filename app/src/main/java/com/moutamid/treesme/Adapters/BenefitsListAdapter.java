package com.moutamid.treesme.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moutamid.treesme.MessagesActivity;
import com.moutamid.treesme.Model.Benefits;
import com.moutamid.treesme.Model.User;
import com.moutamid.treesme.R;

import java.util.List;

public class BenefitsListAdapter extends RecyclerView.Adapter<BenefitsListAdapter.BenefitsViewHolder>{

    private Context mContext;
    private List<Benefits> benefits;
    private User user;

    public BenefitsListAdapter(Context mContext, List<Benefits> benefits,User user) {
        this.mContext = mContext;
        this.benefits = benefits;
        this.user = user;
    }

    @NonNull
    @Override
    public BenefitsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(mContext).inflate(R.layout.benefits_custom_layout,parent,false);
        return new BenefitsViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull BenefitsViewHolder holder, int position) {

        Benefits model = benefits.get(position);
        holder.titleTxt.setText(model.getTitle());
        holder.priceTxt.setText(model.getPrice());
        if (user == null){
            holder.nextBtn.setVisibility(View.GONE);
        }else {
            holder.nextBtn.setVisibility(View.VISIBLE);
        }
        holder.nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MessagesActivity.class);
                intent.putExtra(MessagesActivity.EXTRAS_USER, user);
                intent.putExtra("userUid", user.getuId());
                intent.putExtra("message","Hello " + user.getName() + ",when would you be available for " + model.getTitle());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return benefits.size();
    }

    public class BenefitsViewHolder extends RecyclerView.ViewHolder{

        TextView titleTxt,priceTxt;
        ImageView nextBtn;

        public BenefitsViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.name);
            priceTxt = itemView.findViewById(R.id.price);
            nextBtn = itemView.findViewById(R.id.next);
        }
    }
}
