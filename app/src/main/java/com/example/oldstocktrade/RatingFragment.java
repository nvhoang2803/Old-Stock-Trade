package com.example.oldstocktrade;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.oldstocktrade.Adapter.RatingAdapter;
import com.example.oldstocktrade.Model.Rating;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RatingFragment extends Fragment {
    // MODE ratings : All, 5 stars...
    public static final int MODE_ALL = 0;
    public static final int MODE_5 = 5;
    public static final int MODE_4 = 4;
    public static final int MODE_3 = 3;
    public static final int MODE_2 = 2;
    public static final int MODE_1 = 1;

    private RecyclerView recyclerView;
    private List<Rating> mRatings;
    private RatingAdapter ratingAdapter;
    private RelativeLayout no_feedback;
    private int mode = 0;
    private String userid;
    public RatingFragment() {

    }

    public static RatingFragment newInstance(String userid, int mode) {
        
        Bundle args = new Bundle();
        
        RatingFragment fragment = new RatingFragment();
        args.putString("userid",userid);
        args.putInt("mode",mode);
        fragment.setArguments(args);
        return fragment;

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userid = getArguments().getString("userid");
        mode = getArguments().getInt("mode");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRatings = new ArrayList<>();

        View view = inflater.inflate(R.layout.fragment_rating, container, false);
        no_feedback = view.findViewById(R.id.no_feedback);
        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseDatabase.getInstance().getReference("Users").child(userid).child("Ratings").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()){
                    Rating rating = data.getValue(Rating.class);
                    int rate = rating.getRating();
                    if (mode == MODE_ALL || rate == mode){
                        mRatings.add(rating);
                    }
                }
                if (mRatings.size() == 0){
                    no_feedback.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
                else {
                    no_feedback.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    ratingAdapter = new RatingAdapter(getActivity(), mRatings);
                    recyclerView.setAdapter(ratingAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }
}