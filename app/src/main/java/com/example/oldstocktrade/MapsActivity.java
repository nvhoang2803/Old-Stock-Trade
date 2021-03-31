package com.example.oldstocktrade;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.net.ssl.SSLEngineResult;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    EditText searchView;
    FusedLocationProviderClient client;
    Button btnConfirm;
    TextView txtAddress,txtTude;
    Geocoder geocoder;
    ImageButton btnBack;
//    List<String> listResult = null;
//    ListView listLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        geocoder = new Geocoder(MapsActivity.this);
        searchView = findViewById(R.id.svLocation);
        btnConfirm = findViewById(R.id.btnConfirm);
        txtAddress = findViewById(R.id.address);
        txtTude = findViewById(R.id.tude);
        btnBack = findViewById(R.id.btnBack1);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //listLocation = findViewById(R.id.listLocation);
//        listResult = new LinkedList<>();
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MapsActivity.this, android.R.layout.simple_list_item_1,listResult);
//        listLocation.setAdapter(adapter);
        Places.initialize(getApplicationContext(),getString(R.string.google_maps_key));

        View locationButton = ((View) findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
// position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        rlp.setMargins(0, 180, 180, 0);
        client = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;
                    mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                        @Override
                        public boolean onMyLocationButtonClick() {
                            getCurrentLocation();
                            return false;
                        }
                    });
                }});
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("address", String.valueOf(txtAddress.getText()));
                intent.putExtra("location", String.valueOf(txtTude.getText()));
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        searchView.setFocusable(false);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Onclick Search View", "onClick: ");
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS
                        ,Place.Field.LAT_LNG,Place.Field.NAME);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN,fieldList).setCountry("VN").build(MapsActivity.this);
                startActivityForResult(intent, 100);
            }
        });

////        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
////            @Override
////            public boolean onQueryTextSubmit(String query) {
////
//////                String location = searchView.getQuery().toString();
//////                List<Address> listAddress = null;
//////                listResult.clear();
//////                listResult = new ArrayList<>();
//////                if (location != null || !location.equals("")) {
//////
//////                    try {
//////                        listAddress = geocoder.getFromLocationName(location, 5);
//////                        for(Address x:listAddress){
//////                            listResult.add(x.getAddressLine(0).toString());
//////                        }
//////                        Log.d("Result", "onQueryTextSubmit: "+listResult);
//////                        adapter.notifyDataSetChanged();
//////
//////                    } catch (IOException e) {
//////                        e.printStackTrace();
//////                    }
//////
////////                    if (addressList.size() != 0) {
////////
//////////                        Address address = addressList.get(0);
//////////                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
//////////                        mMap.addMarker(new MarkerOptions().position(latLng).title(address.getAddressLine(0)));
//////////                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
//////////                        txtAddress.setText(address.getAddressLine(0));
//////////                        txtTude.setText(Double.toString(address.getLatitude())+"-"+Double.toString(address.getLongitude()));
////////
////////                    }
//////
//////
//////                }
//////                return false;
////            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return false;
//            }
//        });
        //mapFragment.getMapAsync(this);
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
//                    mapFragment.getMapAsync(new OnMapReadyCallback() {
//                        @Override
//                        public void onMapReady(GoogleMap googleMap) {
//                            mMap = googleMap;

                            setMyLocation();

                            LatLng pos = new LatLng(location.getLatitude(),location.getLongitude());
                            ArrayList<Address> addresses = null;
                            try {
                                addresses = (ArrayList<Address>) geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos,15));
                            mMap.addMarker(new MarkerOptions().position(pos).title("You're here"));
                            if (addresses.size() != 0) {
                                Address address = addresses.get(0);
                                txtAddress.setText(address.getAddressLine(0));
                                txtTude.setText(Double.toString(location.getLatitude()) + "-" + Double.toString(location.getLongitude()));


                            }

                }
            }
        });

    }


    private void setMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==44){
            if(grantResults.length>0 &&grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCurrentLocation();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == RESULT_OK){
            Place place = Autocomplete.getPlaceFromIntent(data);

            LatLng latLng = place.getLatLng();
            mMap.addMarker(new MarkerOptions().position(latLng).title(place.getName()));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            txtAddress.setText(place.getAddress());
            txtTude.setText(Double.toString(latLng.latitude)+"-"+Double.toString(latLng.longitude));

        }else if(resultCode == AutocompleteActivity.RESULT_ERROR){
            Status status = Autocomplete.getStatusFromIntent(data);
            Log.d("Error Status", "onActivityResult: "+status.getStatusMessage());
        }
    }
}