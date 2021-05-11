package com.example.oldstocktrade;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.oldstocktrade.Adapter.ViewPageAdapter;
import com.google.android.material.tabs.TabLayout;

public class StorageFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPageAdapter adapter;
    //-------------phan de setting chon toi
    private int typetab = -1;
    public StorageFragment(int type) {
        typetab = type;
    }
    public StorageFragment() {

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

        adapter.addFragment(new SellingFragment(), getString(R.string.title_storage_selling));
        adapter.addFragment(new WishListFragment(), getString(R.string.title_storage_wishlist));
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
        //--------phan de chon item setting dan toi tab tuong thich
        if(typetab!=-1){
            TabLayout.Tab tab = tabLayout.getTabAt(typetab);
            tab.select();
        }
        //----------------------
    }
}
