package com.example.oldstocktrade;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.oldstocktrade.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingsFragment extends Fragment {
    Button btnMap,btnDirection;
    private FirebaseUser fuser;
    RelativeLayout btnSignout,btnPost,btn_profile;
    private DatabaseReference ref;
    ImageView avatar;
    private User user;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view =  inflater.inflate(R.layout.fragment_settings, container, false);
            btnSignout = view.findViewById(R.id.btnSignout);
            btnPost = view.findViewById(R.id.btnPost);
            btnMap = view.findViewById(R.id.btnMap);
            avatar = view.findViewById(R.id.avatar);
            fuser = FirebaseAuth.getInstance().getCurrentUser();
            ref = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    user = snapshot.getValue(User.class);
                    Log.d("Userauth", "onDataChange: "+user.getImageURL());
                    if (user.getImageURL().equals("default")) ;
//                    profile_image.setImageResource(R.mipmap.ic_launcher);
                    else
                        Glide.with(getActivity()).load(user.getImageURL()).apply(RequestOptions.circleCropTransform()).into(avatar);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
        btnDirection = view.findViewById(R.id.btnDirection);
            btnSignout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getActivity(),LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    getActivity().finish();
                }
            });
            btnPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(),PostActivity.class));

                }
            });
            btnDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),DirectionMap.class));

            }
            });
            btnMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(),MapsActivity.class));

                }
            });
            btn_profile = view.findViewById(R.id.btn_profile);
            btn_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(),ProfileActivity.class).addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT));
                }
            });
            return view;
    }


            }
