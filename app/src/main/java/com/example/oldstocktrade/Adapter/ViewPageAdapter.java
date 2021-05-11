package com.example.oldstocktrade.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.oldstocktrade.SellingFragment;
import com.example.oldstocktrade.WishListFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class ViewPageAdapter extends FragmentPagerAdapter {
    private final List<Fragment> lstFragment = new ArrayList<>();
    private final List<String> lstTitles = new ArrayList<>();

    private final FragmentManager mFragmentManager;
    private Fragment mFragmentAtPos0;

    public ViewPageAdapter(FragmentManager fm) {
        super(fm);
        mFragmentManager = fm;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        return lstFragment.get(position);
    }

    @Override
    public int getCount() {
        return lstTitles.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return lstTitles.get(position);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public void addFragment (Fragment fragment, String title){
        lstFragment.add(fragment);
        lstTitles.add(title);
    }
}