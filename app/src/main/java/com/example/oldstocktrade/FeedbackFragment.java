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

import com.example.oldstocktrade.Adapter.FeedbackAdapter;
import com.example.oldstocktrade.Model.Feedback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FeedbackFragment extends Fragment {
    private RecyclerView recyclerView;
    private FirebaseUser firebaseUser;
    private ArrayList<Feedback> mItems;
    private FeedbackAdapter feedbackAdapter;
    private RelativeLayout no_feedback;

    public FeedbackFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mItems = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);

        no_feedback = view.findViewById(R.id.no_feedback);
        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref_feedback = FirebaseDatabase.getInstance().getReference("Feedback").child(firebaseUser.getUid());
        ref_feedback.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mItems.clear();
                for(DataSnapshot data : snapshot.getChildren()){
                    Feedback item = data.getValue(Feedback.class);
                    mItems.add(item);
                }
                if (mItems.size() == 0){
                    no_feedback.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }else {
                    no_feedback.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    feedbackAdapter = new FeedbackAdapter(getContext(), mItems);
                    recyclerView.setAdapter(feedbackAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }
}