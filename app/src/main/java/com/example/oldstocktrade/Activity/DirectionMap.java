package com.example.oldstocktrade.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.oldstocktrade.DirectionHelpers.DirectionFinder;
import com.example.oldstocktrade.DirectionHelpers.DirectionFinderListener;

import com.example.oldstocktrade.DirectionHelpers.Route;

import com.example.oldstocktrade.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DirectionMap extends FragmentActivity implements OnMapReadyCallback{
    GoogleMap map;
    MarkerOptions source, dest;
    String strOrigin,strDest;
    TextView tvTo,tvFrom;
    ImageView btnGMap;
    ImageButton btnBack;
    SupportMapFragment mapFragment;
    double lat1=10.762397,long1=106.682752,lat2=10.7704246,long2=106.6724038;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction_map);
        mapFragment =  (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.directionMap);
                // (MapFragment) getFragmentManager().findFragmentById(R.id.directionMap);
        mapFragment.getMapAsync(this);

        Intent receiver = getIntent();

        tvTo = findViewById(R.id.tvTo);
        //tvTo.setText("255 Ba tháng hai, P10, Q10, TP HCM");
        tvFrom = findViewById(R.id.tvFrom);
        //tvFrom.setText("255 Ba tháng hai, P10, Q10, TP HCM");
        String txtFrom="",txtTo="";
        if(receiver!=null){
            Bundle bundle = receiver.getExtras();
            if(bundle!=null){
                lat1 = bundle.getDouble("latitudeS");
                long1 = bundle.getDouble("longitudeS");
                lat2 = bundle.getDouble("latitudeD");
                long2 = bundle.getDouble("longitudeD");
                txtFrom = bundle.getString("addressS");
                txtTo =bundle.getString("addressD");

            }
            Log.d("bundle", "onCreate: "+bundle);
        }
        tvTo.setText(txtTo);
        tvFrom.setText(txtFrom);



        int height =150;
        int width = 150;
        BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.icon_marker3);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 130, 130, false);

        BitmapDrawable bitmapdraw1 = (BitmapDrawable)getResources().getDrawable(R.drawable.icon_marker);
        Bitmap b1 = bitmapdraw1.getBitmap();
        Bitmap smallMarker1 = Bitmap.createScaledBitmap(b1, width, height, false);
        //double lat1=10.762397,long1=106.682752,lat2=10.7704246,long2=106.6724038;
        source = new MarkerOptions().position(new LatLng(lat1,long1)).icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
        dest = new MarkerOptions().position(new LatLng(lat2,long2)).icon(BitmapDescriptorFactory.fromBitmap(smallMarker1));
        LatLng origin = source.getPosition();
        LatLng des = dest.getPosition();
        strOrigin = origin.latitude+","+origin.longitude;
        strDest = des.latitude+","+des.longitude;

        btnGMap = findViewById(R.id.btnGMap);
        btnBack = findViewById(R.id.btnBack2);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnGMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?saddr="+Double.toString(lat1)+","+Double.toString(long1)+"&daddr="+Double.toString(lat2)+","+Double.toString(long2)));
                startActivity(intent);
            }
        });
        DirectionFinder directionFinder = new DirectionFinder(new DirectionFinderListener() {
            @Override
            public void onDirectionFinderStart() {

            }

            @Override
            public void onDirectionFinderSuccess(List<Route> routes) {


                for (Route route : routes) {
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
                    ((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
                    ((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);


                    PolylineOptions polylineOptions = new PolylineOptions().
                            geodesic(true).
                            color(getApplicationContext().getResources().getColor(R.color.back_btn)).
                            width(10);

                    for (int i = 0; i < route.points.size(); i++)
                        polylineOptions.add(route.points.get(i));

                    map.addPolyline(polylineOptions);
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();

                    for (LatLng point : route.points) {
                        builder.include(point);
                    }

                    LatLngBounds bounds = builder.build();

                    map.setPadding(300, 100, 100, 650);
                    map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
                    map.setPadding(0,0,0,0);

                }
            }
        },strOrigin,strDest);
        try {
            directionFinder.execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        mapFragment.onResume();
        map.addMarker(source);
        map.addMarker(dest);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(source.getPosition(), 15));
        //map.addPolyline(new PolylineOptions().add(source.getPosition(),dest.getPosition()).width(10).color(Color.BLUE));
    }




}