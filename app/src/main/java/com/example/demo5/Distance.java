package com.example.demo5;

//import android.util.Pair;

import android.app.Activity;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.util.Pair;


public class Distance extends AppCompatActivity {

    public Activity activity;


    /**
     * Declaring the longitude and lattitude
     * TODO: Implement their distances based on the friend's location
     */
    double latitude;
    double longitude;
    private LocationService locationService;
    private CompassViewModel viewModel;
    private boolean skip1;
    private String friendLongText;
    private String friendLatText;
    private Double friendLat;
    private Double friendLong;

    public Distance(Activity activity, CompassViewModel viewModel) {
        this.activity = activity;
        this.viewModel = viewModel;
        skip1 = false;
        this.friendLongText = "0.0";
        this.friendLatText = "0.0";
    }

    public void settingCircleAngle(int ang) {
        TextView friendtext = this.activity.findViewById(R.id.best_friend);
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) friendtext.getLayoutParams();

        layoutParams.circleRadius = ang;
        friendtext.setLayoutParams(layoutParams);


    }

    public double distanceCalculation(Double longitude, Double latitude) {
        Pair<String, String> friendLocation = retrieveFriendLocation();
        String parentLongText = friendLocation.first;
        String parentLatText = friendLocation.second;

        double friend_lat;
        double friend_long;

        double lat1;
        double lon1;
        double lat2;
        double lon2;

        try {
            friend_lat = Double.parseDouble(parentLatText);
            friend_long = Double.parseDouble(parentLongText);
        } catch (Exception e) {
            friend_lat = 0;
            friend_long = 0;
        }

        //Calculating the distance
        lat1 = Math.toRadians(friend_lat);
        lon1 = Math.toRadians(friend_long);
        lat2 = Math.toRadians(latitude);
        lon2 = Math.toRadians(longitude);

        double distance = Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1)) * 6371 * 0.621371;

        //Will return the distance in miles
        //System.out.println("Mile distance = " + distance);
        return distance;
    }


    /**
     * Retrieving the location of the friend
     *
     * @return : long and lat as a pair of strings
     */
    public Pair<String, String> retrieveFriendLocation() {
        //TODO: Step 1: Retrieving their location based on their UID

        friendLongText = String.valueOf(friendLong);
        friendLatText = String.valueOf(friendLat);

        return new Pair<>(friendLongText, friendLatText);
    }

    public void setLocation(Double latitude, Double longitude) {
        friendLat = latitude;
        friendLong = longitude;
    }

}

