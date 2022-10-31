package com.moutamid.treesme.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moutamid.treesme.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class OurTeamListAdapter extends RecyclerView.Adapter
        <OurTeamListAdapter.OurTeamsViewHolder> {

    Context context;
    private int[] teams;

    public OurTeamListAdapter(Context context, int[] teams) {
        this.context = context;
        this.teams = teams;
    }

    @NonNull
    @Override
    public OurTeamsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.our_teams_layout,parent,false);
        return new OurTeamsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OurTeamsViewHolder holder, int position) {
        Picasso.with(context)
                .load(teams[position])
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return teams.length;
    }

    public class OurTeamsViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView imageView;
        public OurTeamsViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.images);
        }
    }
}
