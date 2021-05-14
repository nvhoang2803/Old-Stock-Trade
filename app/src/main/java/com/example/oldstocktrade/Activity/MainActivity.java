package com.example.oldstocktrade.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.oldstocktrade.Model.Contact;
import com.example.oldstocktrade.Model.User;
import com.example.oldstocktrade.Fragment.HistoryFragment;
import com.example.oldstocktrade.Fragment.HomeFragment;
import com.example.oldstocktrade.Fragment.SettingsFragment;
import com.example.oldstocktrade.Fragment.StorageFragment;
import com.example.oldstocktrade.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;



public class MainActivity extends AppCompatActivity {
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    FusedLocationProviderClient client;
    public double longitude, latitude;
    public String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance("https://old-stock-trade-default-rtdb.firebaseio.com/").
                getReference("Users").child(firebaseUser.getUid());



//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid());


        //----------------------------Get current Location

        client = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment(MainActivity.this)).commit();

        } else {
            getLocation();
        }
        //----------------------------End
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user.getType() == 1){
                    bottomNav.inflateMenu(R.menu.bottom_adminnav);
                    bottomNav.setOnNavigationItemSelectedListener(navListenerAdmin);
                }else{
                    bottomNav.inflateMenu(R.menu.bottom_navigation);
                    bottomNav.setOnNavigationItemSelectedListener(navListener);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.nav_home:
                    selectedFragment = new HomeFragment(MainActivity.this);
                    break;
                case R.id.nav_history:
                    selectedFragment = new HistoryFragment(MainActivity.this);
                    break;
                case R.id.nav_storage:
                    selectedFragment = new StorageFragment(MainActivity.this);
                    break;
                case R.id.nav_settings:
                    selectedFragment = new SettingsFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
            //getFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            return true;
        }
    };
    private BottomNavigationView.OnNavigationItemSelectedListener navListenerAdmin = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.nav_product:
                    selectedFragment = new ManageProductFragment(MainActivity.this);
                    break;
                case R.id.nav_user:
                    selectedFragment = new HistoryFragment(MainActivity.this);
                    break;
                case R.id.nav_static:
                    selectedFragment = new HistoryFragment(MainActivity.this);
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
            //getFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            return true;
        }
    };
    public void handleClickFragment(int itemID, int type) {
        Fragment selectedFragment = null;
        switch (itemID) {
            case R.id.nav_home:
                selectedFragment = new HomeFragment(MainActivity.this);
                break;
            case R.id.nav_history:
                selectedFragment = new HistoryFragment(MainActivity.this, type);
                break;
            case R.id.nav_storage:
                selectedFragment = new StorageFragment(MainActivity.this, type);
                break;
            case R.id.nav_settings:
                selectedFragment = new SettingsFragment();
                break;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
    }
    void status(String s) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", s);

        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }


        client.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            longitude = location.getLongitude();
                            latitude = location.getLatitude();

                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    new HomeFragment(MainActivity.this)).commit();
                            Geocoder geocoder = new Geocoder(MainActivity.this);
                            ArrayList<Address> addresses = null;
                            try {
                                addresses = (ArrayList<Address>) geocoder.getFromLocation(latitude,longitude,1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (addresses != null && addresses.size() != 0) {
                                Address add = addresses.get(0);
                                address = add.getAddressLine(0);
                            }
                            Log.d("location", "onSuccess: "+location.toString());//vi tri hien tai
                            //test khoang cach tai vi tri hien tai den mot vi tri bat ki
                            Log.d("distance", "onSuccess: ");
                        }
                    }
                });
    }
}