package com.moutamid.treesme.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moutamid.treesme.Model.Favorite;
import com.moutamid.treesme.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FavoriteListAdapter extends RecyclerView.Adapter<FavoriteListAdapter.FavoriteViewHolder>{

    private Context mContext;
    private ArrayList<Favorite> favoriteArrayList;

    public FavoriteListAdapter(Context mContext, ArrayList<Favorite> favoriteArrayList) {
        this.mContext = mContext;
        this.favoriteArrayList = favoriteArrayList;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.custom_layout,parent,false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {

        Favorite model = favoriteArrayList.get(position);
        holder.name.setText(model.getProductName());
        Picasso.with(mContext)
                .load(model.getProductImg())
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return favoriteArrayList.size();
    }

    public class FavoriteViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView name;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.images);
            name = itemView.findViewById(R.id.name);
        }
    }
}
