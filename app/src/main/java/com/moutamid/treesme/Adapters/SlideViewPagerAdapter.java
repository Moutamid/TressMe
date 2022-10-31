package com.moutamid.treesme.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.moutamid.treesme.AccountsLogin.AccountsRegistrationScreen;
import com.moutamid.treesme.R;
import com.moutamid.treesme.SharedPreferencesManager;

import java.util.Locale;

public class SlideViewPagerAdapter extends PagerAdapter {

    private Context ctx;
    private SharedPreferencesManager manager;

    public SlideViewPagerAdapter(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater= (LayoutInflater) ctx.getSystemService(ctx.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.slide_screen,container,false);
        ImageView logo1 =view.findViewById(R.id.logo1);
        ImageView sel1 =view.findViewById(R.id.sel1);
        ImageView sel2 =view.findViewById(R.id.sel2);
        ImageView sel3 =view.findViewById(R.id.sel3);

        TextView title =view.findViewById(R.id.titles);
        TextView desc=view.findViewById(R.id.description);
        Button btnGetStarted =view.findViewById(R.id.started);
        btnGetStarted.setVisibility(View.GONE);
        manager = new SharedPreferencesManager(ctx);
        getLocale();
        if (position == 2){
            btnGetStarted.setVisibility(View.VISIBLE);
        }
        btnGetStarted.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ///if (position == 2){
                    Intent intent=new Intent(ctx, AccountsRegistrationScreen.class);
                    ctx.startActivity(intent);
                //}
            }
        });

        switch (position)
        {
            case 0:
                logo1.setImageResource(R.drawable.walkthough1);
                sel1.setImageResource(R.drawable.selected);
                sel2.setImageResource(R.drawable.unselected);
                sel3.setImageResource(R.drawable.unselected);

                title.setText(R.string.walkthrough1_title_txt);
                desc.setText(R.string.walkthrough1_content_txt);
                //back.setVisibility(View.GONE);
                break;

            case 1:
                logo1.setImageResource(R.drawable.walkthrough2);
                sel1.setImageResource(R.drawable.unselected);
                sel2.setImageResource(R.drawable.selected);
                sel3.setImageResource(R.drawable.unselected);

                title.setText(R.string.walkthrough2_title_txt);
                desc.setText(R.string.walkthrough2_content_txt);
                //back.setVisibility(View.INVISIBLE);
              //  next.setVisibility(View.INVISIBLE);
                break;

            case 2:
                logo1.setImageResource(R.drawable.walkthrough3);
                sel1.setImageResource(R.drawable.unselected);
                sel2.setImageResource(R.drawable.unselected);
                sel3.setImageResource(R.drawable.selected);

                title.setText(R.string.walkthrough3_title_txt);
                desc.setText(R.string.walkthrough3_content_txt);
               // back.setVisibility(View.INVISIBLE);
                //next.setVisibility(View.GONE);
                break;
        }



        container.addView(view);
        return view;
    }


    private void getLocale(){

        String lang = manager.retrieveString("lang","");
        setLocale(lang);
    }

    private void setLocale(String lng) {

        Locale locale = new Locale(lng);
        Locale.setDefault(locale);

        Configuration configuration = new Configuration();
        configuration.locale = locale;
        ctx.getResources().updateConfiguration(configuration,ctx.getResources().getDisplayMetrics());
        manager.storeString("lang",lng);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
