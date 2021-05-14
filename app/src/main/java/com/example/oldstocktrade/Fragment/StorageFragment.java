package com.example.oldstocktrade.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.oldstocktrade.Activity.ContactActivity;
import com.example.oldstocktrade.Adapter.ViewPageAdapter;
import com.example.oldstocktrade.Activity.MainActivity;
import com.example.oldstocktrade.R;
import com.google.android.material.tabs.TabLayout;

public class StorageFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPageAdapter adapter;
    //-------------phan de setting chon toi
    private int typetab = -1;
    private MainActivity curActivity;

    public StorageFragment(MainActivity activity, int typetab) {
        curActivity = activity;
        this.typetab = typetab;
    }
    public StorageFragment(MainActivity activity) {
        curActivity = activity;
    }
    //-----------------
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_storage, container, false);
            return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tabLayout = (TabLayout) view.findViewById(R.id.tab_storage);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager_id);
        adapter = new ViewPageAdapter(getChildFragmentManager());

        adapter.addFragment(new SellingFragment(curActivity), getString(R.string.title_storage_selling));
        adapter.addFragment(new WishListFragment(curActivity), getString(R.string.title_storage_wishlist));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        //--------phan de chon item setting dan toi tab tuong thich
        if(typetab!=-1){
            adapter.notifyDataSetChanged();
            TabLayout.Tab tab = tabLayout.getTabAt(typetab);
            tab.select();
        }
        //----------------------
        view.findViewById(R.id.btn_contact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ContactActivity.class);
                startActivity(i);
            }
        });
    }
}
