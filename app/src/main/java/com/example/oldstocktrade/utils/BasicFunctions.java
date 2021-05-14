package com.example.oldstocktrade.Utils;

import android.location.Location;
import android.util.Log;



public class BasicFunctions {
    public static double calDistance(double long1,double lat1,double long2, double lat2){
        Location dest = new Location("");
        dest.setLatitude(lat2);
        dest.setLongitude(long2);
        Location source = new Location("");
        source.setLatitude(lat1);
        source.setLongitude(long1);
        Log.d("Distance", "calDistance: "+source.distanceTo(dest)/1000+"jjj"+lat1+","+long1+" "+lat2+","+long2);
        return source.distanceTo(dest)/1000;
    }


}
