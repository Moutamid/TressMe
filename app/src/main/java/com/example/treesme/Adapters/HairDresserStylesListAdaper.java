package com.example.treesme.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.treesme.Model.Hairstyles;
import com.example.treesme.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HairDresserStylesListAdaper extends BaseAdapter {
    Context context;
    List<Hairstyles> hairstyles;
    LayoutInflater inflter;
    public HairDresserStylesListAdaper(Context applicationContext,List<Hairstyles> hairstyles) {
        this.context = applicationContext;
        this.hairstyles = hairstyles;
        inflter = (LayoutInflater.from(applicationContext));
    }
    @Override
    public int getCount() {
        return hairstyles.size();
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
        view = inflter.inflate(R.layout.style_custom_layout, null); // inflate the layout


        ImageView imageView = view.findViewById(R.id.images);
        TextView name = view.findViewById(R.id.name);

        Hairstyles model = hairstyles.get(i);
        name.setText(model.getName());
        Picasso.with(context)
                .load(model.getImageUrl())
                .into(imageView);

         return view;
    }
}