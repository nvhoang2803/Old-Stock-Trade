package com.example.oldstocktrade;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.oldstocktrade.Adapter.ViewPageAdapter;
import com.google.android.material.tabs.TabLayout;

public class DeliveryFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPageAdapter adapter;
    private ImageButton btn_contact;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_delivery, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tabLayout = (TabLayout) view.findViewById(R.id.tab_delivery);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager_id);
        adapter = new ViewPageAdapter(getChildFragmentManager());
        btn_contact = view.findViewById(R.id.btn_contact);
        adapter.addFragment(new PendingFragment(), "PENDING");
        //adapter.addFragment(new ContactFragment(), "Recent contact");
        adapter.addFragment(new BuyingFragment(),"BUYING");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        btn_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ContactActivity.class));
            }
        });
    }
}
