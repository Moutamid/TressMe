package com.example.treesme.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.treesme.HairDresserProfile;
import com.example.treesme.Model.Planning;
import com.example.treesme.Model.Review;
import com.example.treesme.Model.User;
import com.example.treesme.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AvailableHairDresserListAdaper extends RecyclerView.Adapter<AvailableHairDresserListAdaper.AvailableHairDresserViewHolder>{

    private Context mContext;
    private List<Planning> planningList;
    private User user;

    public AvailableHairDresserListAdaper(Context mContext, List<Planning> planningList) {
        this.mContext = mContext;
        this.planningList = planningList;
    }

    @NonNull
    @Override
    public AvailableHairDresserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.hair_dresser_custom_layout,parent,false);
        return new AvailableHairDresserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AvailableHairDresserViewHolder holder, int position) {
        Planning model = planningList.get(position);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(model.getDresserId());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    user = snapshot.getValue(User.class);
                    holder.name.setText(user.getName());
                    Picasso.with(mContext)
                            .load(user.getImageUrl())
                            .into(holder.imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.type.setText("Home and on move");
        holder.rate.setText(String.valueOf(5));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, HairDresserProfile.class);
                intent.putExtra("user",user);
                intent.putExtra("type","Home and on move");
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return planningList.size();
    }

    public class AvailableHairDresserViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView name,rate,type,view,available;

        public AvailableHairDresserViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.profile);
            name = itemView.findViewById(R.id.name);
            rate = itemView.findViewById(R.id.rate);
            type = itemView.findViewById(R.id.type);
            view = itemView.findViewById(R.id.view);
            available = itemView.findViewById(R.id.available);
        }
    }
}
