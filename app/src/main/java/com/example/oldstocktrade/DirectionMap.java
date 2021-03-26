package com.example.oldstocktrade;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.oldstocktrade.DirectionHelpers.DirectionFinder;
import com.example.oldstocktrade.DirectionHelpers.DirectionFinderListener;

import com.example.oldstocktrade.DirectionHelpers.Route;

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

public class DirectionMap extends FragmentActivity implements OnMapReadyCallback{
    GoogleMap map;
    MarkerOptions source, dest;
    Polyline currentPolyline;
    String strOrigin,strDest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction_map);
        SupportMapFragment mapFragment =  (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.directionMap);
                // (MapFragment) getFragmentManager().findFragmentById(R.id.directionMap);
        mapFragment.getMapAsync(this);
        source = new MarkerOptions().position(new LatLng(10.762397,106.682752));
        dest = new MarkerOptions().position(new LatLng(10.7704246,106.6724038));
        LatLng origin = source.getPosition();
        LatLng des = dest.getPosition();
        strOrigin = origin.latitude+","+origin.longitude;
        strDest = des.latitude+","+des.longitude;

        DirectionFinder directionFinder = new DirectionFinder(new DirectionFinderListener() {
            @Override
            public void onDirectionFinderStart() {

            }

            @Override
            public void onDirectionFinderSuccess(List<Route> routes) {
//            polylinePaths = new ArrayList<>();
//            originMarkers = new ArrayList<>();
//            destinationMarkers = new ArrayList<>();

                for (Route route : routes) {
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
                    ((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
                    ((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);

//                originMarkers.add(mMap.addMarker(new MarkerOptions()
//                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
//                        .title(route.startAddress)
//                        .position(route.startLocation)));
//                destinationMarkers.add(mMap.addMarker(new MarkerOptions()
//                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
//                        .title(route.endAddress)
//                        .position(route.endLocation)));

                    PolylineOptions polylineOptions = new PolylineOptions().
                            geodesic(true).
                            color(Color.BLUE).
                            width(10);

                    for (int i = 0; i < route.points.size(); i++)
                        polylineOptions.add(route.points.get(i));

                    map.addPolyline(polylineOptions);
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();

                    for (LatLng point : route.points) {
                        builder.include(point);
                    }

                    LatLngBounds bounds = builder.build();
                    int padding = 20; //
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,
                            padding);
                    map.moveCamera(cu);
                    map.animateCamera(cu, 1800, null);
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
        map.addMarker(source);
        map.addMarker(dest);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(source.getPosition(), 15));
        //map.addPolyline(new PolylineOptions().add(source.getPosition(),dest.getPosition()).width(10).color(Color.BLUE));
    }




}