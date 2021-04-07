package com.example.oldstocktrade;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.example.oldstocktrade.Adapter.ViewPageAdapter;
import com.google.android.material.tabs.TabLayout;

public class ContactActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPageAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        tabLayout = findViewById(R.id.tab_contact);
        viewPager = findViewById(R.id.viewpager_id);
        adapter = new ViewPageAdapter(getSupportFragmentManager());

        adapter.addFragment(new AllUsersFragment(), "All users");
        adapter.addFragment(new ContactFragment(),"Recent");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}