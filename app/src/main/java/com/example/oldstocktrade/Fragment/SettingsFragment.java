package com.example.oldstocktrade.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.oldstocktrade.Activity.ContactActivity;
import com.example.oldstocktrade.Activity.LoginActivity;
import com.example.oldstocktrade.Activity.MapsActivity;
import com.example.oldstocktrade.Activity.PostActivity;
import com.example.oldstocktrade.Activity.ProfileActivity;
import com.example.oldstocktrade.Activity.MainActivity;
import com.example.oldstocktrade.Model.User;
import com.example.oldstocktrade.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class SettingsFragment extends Fragment {
    private final int REQUEST_CODE_LOCATION = 100;
    TextView txtUsername,txtLocation;
    private FirebaseUser fuser;
    RelativeLayout btnSignout,btnPost,btn_profile,btnYourLocation,btnWishlist,btnSelling,btnSold,btnBought,btnContact;
    private DatabaseReference ref;
    CircleImageView avatar;
    private User user;
    MainActivity main;
    ValueEventListener valueEventListener = null;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view =  inflater.inflate(R.layout.fragment_settings, container, false);
            main = (MainActivity) getActivity();
            btnSignout = view.findViewById(R.id.btnSignout);
            btnYourLocation = view.findViewById(R.id.btnYourLocation);
            btnWishlist = view.findViewById(R.id.btnWishlist);
            btnSelling = view.findViewById(R.id.btnSelling);
            btnSold = view.findViewById(R.id.btnSold);
            btnBought = view.findViewById(R.id.btnBought);
            btnContact = view.findViewById(R.id.btnContact);
            btnPost = view.findViewById(R.id.btnPost);

            avatar = view.findViewById(R.id.avatar);
            txtUsername = view.findViewById(R.id.UserName);
            txtLocation = view.findViewById(R.id.txtMyLocation);

            btn_profile = view.findViewById(R.id.btn_profile);
            fuser = FirebaseAuth.getInstance().getCurrentUser();
            ref = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
            txtLocation.setText(main.address);

            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    user = snapshot.getValue(User.class);
                    txtUsername.setText(user.getUsername());
                    Log.d("Userauth", "onDataChange: "+user.getImageURL());
                    if (user.getImageURL().equals("default"))
                        avatar.setImageResource(R.mipmap.ic_launcher);
                    else
                        Glide.with(main.getApplicationContext()).load(user.getImageURL()).apply(RequestOptions.circleCropTransform()).into(avatar);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            ref.addValueEventListener(valueEventListener);
            btnSignout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getActivity(), LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    //getActivity().finish();
                    Log.d("signout", "onClick: Sign out");
                }
            });
            btnWishlist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    main.handleClickFragment(R.id.nav_storage,1);
                }
            });
            btnContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), ContactActivity.class);
                    startActivity(i);
                }
            });
            btnSelling.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    main.handleClickFragment(R.id.nav_storage,0);
                }
            });
            btnSold.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    main.handleClickFragment(R.id.nav_history,0);
                }
            });
            btnBought.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    main.handleClickFragment(R.id.nav_history,1);
                }
            });
            btnYourLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), MapsActivity.class);
                    Bundle myBundle = new Bundle();
                    myBundle.putDouble ("lat",main.latitude);
                    myBundle.putDouble ("long",main.longitude);
                    intent.putExtras(myBundle);

                    startActivityForResult(intent,REQUEST_CODE_LOCATION);
                }
            });
            btnPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), PostActivity.class);
                    Bundle myBundle = new Bundle();
                    myBundle.putDouble ("lat",main.latitude);
                    myBundle.putDouble ("long",main.longitude);
                    intent.putExtras(myBundle);
                    startActivity(intent);

                }
            });

            btn_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), ProfileActivity.class).addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT));
                }
            });
            return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LOCATION) {
            if (resultCode == RESULT_OK) {

                // Get String data from Intent
                String location = data.getStringExtra("location");
                String straddress = data.getStringExtra("address");
                String[] loc = location.split("#");
                main.latitude = Double.valueOf(loc[0]);
                main.longitude = Double.valueOf(loc[1]);
                txtLocation.setText(straddress);
                main.address = straddress;
                // Set text view with string
                Log.d("Location", "onActivityResult: "+location);;//split location bang # se ra longitude va latitude roi luu vao db

            }
        }
    }
}
