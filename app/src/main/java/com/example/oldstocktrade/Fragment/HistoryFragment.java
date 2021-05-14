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

public class HistoryFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPageAdapter adapter;
    private MainActivity curActivity;
    //-------------phan de setting chon toi
    private int typetab = -1;

    public HistoryFragment(MainActivity activity, int typetab) {
        curActivity = activity;
        this.typetab = typetab;
    }
    public HistoryFragment() {

    }
    public static final HistoryFragment newInstance( int typetab)
    {
        HistoryFragment fragment = new HistoryFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("typetab", typetab);
        fragment.setArguments(bundle);
        return fragment ;
    }
    public HistoryFragment(MainActivity activity) {
        curActivity = activity;
    }
    //------------------
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_history, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        curActivity = (MainActivity) getActivity();
        typetab = getArguments().getInt("typetab");

        tabLayout = (TabLayout) view.findViewById(R.id.tab_history);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager_id);
        adapter = new ViewPageAdapter(getChildFragmentManager());

        adapter.addFragment(new SoldFragment(curActivity), getString(R.string.title_history_sold));
        adapter.addFragment(new BoughtFragment(curActivity), getString(R.string.title_history_bought));
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        //--------phan de chon item setting dan toi tab tuong thich
        if(typetab!=-1){
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
