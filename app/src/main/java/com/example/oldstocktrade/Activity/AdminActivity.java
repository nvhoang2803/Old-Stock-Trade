package com.example.oldstocktrade.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.oldstocktrade.Fragment.HistoryFragment;
import com.example.oldstocktrade.Fragment.ManageProductFragment;
import com.example.oldstocktrade.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminActivity extends AppCompatActivity {
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    FusedLocationProviderClient client;
    public double longitude, latitude;
    public String address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin2);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance("https://old-stock-trade-default-rtdb.firebaseio.com/").
                getReference("Users").child(firebaseUser.getUid());
        BottomNavigationView bottomNav = findViewById(R.id.bottom_adminnavi);
        bottomNav.inflateMenu(R.menu.bottom_adminnav);
        bottomNav.setOnNavigationItemSelectedListener(navListenerAdmin);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListenerAdmin = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.nav_product:
                    selectedFragment = new ManageProductFragment(AdminActivity.this);
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
            //getFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            return true;
        }
    };
}

