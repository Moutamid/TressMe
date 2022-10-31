package com.moutamid.treesme.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.moutamid.treesme.Model.Hairstyles;
import com.moutamid.treesme.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HairStylesGridListAdapter extends RecyclerView.Adapter
        <HairStylesGridListAdapter.HairaStylesGridViewHolder> {

    Context context;
    List<Hairstyles> hairstyles;

    public HairStylesGridListAdapter(Context context, List<Hairstyles> hairstyles) {
        this.context = context;
        this.hairstyles = hairstyles;
    }

    @NonNull
    @Override
    public HairaStylesGridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.community_hair_styles_layout,parent,false);
        return new HairaStylesGridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HairaStylesGridViewHolder holder, int position) {
        Hairstyles model = hairstyles.get(position);
        holder.name.setText(model.getName());
        Picasso.with(context)
                .load(model.getImageUrl())
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return hairstyles.size();
    }

    public class HairaStylesGridViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView imageView;
        private TextView name;
        private CardView cardView;

        public HairaStylesGridViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            imageView = itemView.findViewById(R.id.images);
            name = itemView.findViewById(R.id.name);

        }
    }
}
