package com.example.oldstocktrade.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.oldstocktrade.Activity.LoginActivity;
import com.example.oldstocktrade.Adapter.AdminProductApapter;
import com.example.oldstocktrade.Adapter.AdminUserAdapter;
import com.example.oldstocktrade.Model.Product;
import com.example.oldstocktrade.Model.User;
import com.example.oldstocktrade.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;

public class ManageUserFragment extends Fragment {
    Activity curActivity;
    ArrayList<User> arr;
    DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();
    User curUser;
    RecyclerView adminViewProduct;
    public ManageUserFragment(Activity act){
        curActivity = act;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_admin3, container, false);

        ImageView cr = view.findViewById(R.id.circleImageView1);

        ((ImageView) view.findViewById(R.id.btnAdminClose1)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                //getActivity().finish();
                Log.d("signout", "onClick: Sign out");
            }
        });
        arr = new ArrayList<User>();

        mReference.child("Users").orderByChild("id")
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    curUser = ds.getValue(User.class);
                }
                Glide.with(cr).load(curUser.getImageURL()).into(cr);
                ((TextView) view.findViewById(R.id.adminName1)).setText(curUser.getUsername());
                mReference.child("Users").
                        addListenerForSingleValueEvent(new ValueEventListener() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User tmp;
                                int i=0;
                                for (DataSnapshot ds: snapshot.getChildren()){
                                    tmp = ds.getValue(User.class);
                                    arr.add(tmp);
                                    if (i==50) break;
                                }
//                                arr.sort(Comparator.comparing(User::getId).reversed());
                                AdminUserAdapter adminViewUserAdapter = new AdminUserAdapter(getActivity(),arr);
                                adminViewProduct = view.findViewById(R.id.admin_userView);
                                adminViewProduct.setAdapter(adminViewUserAdapter);
                                adminViewProduct.setLayoutManager(new LinearLayoutManager(curActivity));
//                                        listViewProduct.setAdapter(listViewProductAdapter);
//                                        listViewProduct.setLayoutManager(new LinearLayoutManager(curActivity));
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }
}
