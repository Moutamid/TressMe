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

import com.example.treesme.Model.Hairstyles;
import com.example.treesme.Model.Post;
import com.example.treesme.R;
import com.example.treesme.User.PostDetail;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CommunityPostsListAdaper extends RecyclerView.Adapter<CommunityPostsListAdaper.CommunityPostsViewHolder> {

    private Context mContext;
    private List<Post> postList;

    public CommunityPostsListAdaper(Context mContext, List<Post> postList) {
        this.mContext = mContext;
        this.postList = postList;
    }

    @NonNull
    @Override
    public CommunityPostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.community_post_layout,parent,false);
        return new CommunityPostsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommunityPostsViewHolder holder, int position) {
        Post model = postList.get(position);
        Picasso.with(mContext)
                .load(model.getPostUrl())
                .into(holder.imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, PostDetail.class);
                intent.putExtra("pId",model.getId());
                intent.putExtra("dresserId",model.getDresserId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class CommunityPostsViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;

        public CommunityPostsViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
        }
    }
}
