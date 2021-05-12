package com.example.oldstocktrade;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.oldstocktrade.Adapter.ViewPageAdapter;
import com.google.android.material.tabs.TabLayout;

public class RatingActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPageAdapter adapter;
    private ImageButton btn_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        Intent i = getIntent();
        String userid = i.getStringExtra("userid");

        tabLayout = findViewById(R.id.tab_rating);
        viewPager = findViewById(R.id.viewpager_id);
        btn_back = findViewById(R.id.btn_back);
        adapter = new ViewPageAdapter(getSupportFragmentManager());

        btn_back.setOnClickListener(new View.OnClickListener() {@Override public void onClick(View v) {finish();}});

        adapter.addFragment(RatingFragment.newInstance(userid, RatingFragment.MODE_ALL),"All");
        adapter.addFragment(RatingFragment.newInstance(userid, RatingFragment.MODE_5),"5 Stars");
        adapter.addFragment(RatingFragment.newInstance(userid, RatingFragment.MODE_4),"4 Stars");
        adapter.addFragment(RatingFragment.newInstance(userid, RatingFragment.MODE_3),"3 Stars");
        adapter.addFragment(RatingFragment.newInstance(userid, RatingFragment.MODE_2),"2 Stars");
        adapter.addFragment(RatingFragment.newInstance(userid, RatingFragment.MODE_1),"1 Stars");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

}