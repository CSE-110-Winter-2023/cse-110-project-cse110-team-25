package com.example.demo5;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;

public class MainActivity extends AppCompatActivity {
    private TimeService timeService;
    private OrientationService orientationService;
    private LocationService locationService;
    private boolean requestingLocationUpdates = false;
    public static final int DEGREES_IN_A_CIRCLE = 360;
    private ScheduledExecutorService backgroundThreadExecutor = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> future;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        orientationService = OrientationService.singleton(this);

        ConstraintLayout compass = findViewById(R.id.compass);


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 200);
            requestingLocationUpdates = true;
        }

        locationService = LocationService.singleton(this);

        ImageView parentHouse = findViewById(R.id.parentHouse);
        TextView textView = findViewById(R.id.timeTextView);



        locationService.getLocation().observe(this, loc -> {
            Log.d("whatever", "after observe");
            runOnUiThread(() -> {
                textView.setText(Double.toString(loc.first) + " , " +
                        Double.toString(loc.second));
            });
        });

        this.future = backgroundThreadExecutor.scheduleAtFixedRate(() -> {
            Log.d("whatever", textView.getText().toString());

            SharedPreferences preferences = getPreferences(MODE_PRIVATE);
            Double pLat = Double.parseDouble(preferences.getString("parentLatitude", "123"));
            Double pLong = Double.parseDouble(preferences.getString("parentLongitude", "123"));


            //double adjacent = (pLong - loc.second);
            //double hypotenuse = Math.sqrt(((pLat - loc.first) * (pLat - loc.first)) + ((pLong - loc.second) * (pLong - loc.second)));

            //double ang = Math.acos(adjacent / hypotenuse);




        }, 0, 1000, TimeUnit.MILLISECONDS);

        runOnUiThread(() -> {

            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) parentHouse.getLayoutParams();
            //layoutParams.circleAngle = (float) Math.toDegrees(ang);
            layoutParams.circleAngle = (float) Math.toDegrees(0);

            orientationService.getOrientation().observe(this, orientation -> {
                Log.d("whatever", "after orientation");
                float deg = (float) Math.toDegrees(orientation);
                compass.setRotation(DEGREES_IN_A_CIRCLE - deg);


            });

        });

        loadProfile();


        //this.future = (Future<Void>) backgroundThreadExecutor.submit(() -> {
        //System.out.println("sleep");

        //});
    }

    protected void onPause(Bundle savedInstanceState) {
        super.onPause();
        orientationService.unregisterSensorListeners();
        //onStop(savedInstanceState);
    }

    /*
        protected void onStop(Bundle savedInstanceState) {
            super.onStop();
            onCreate(savedInstanceState);
        }

        @Override
        protected void onResume() {
            super.onResume();
            if (requestingLocationUpdates) {
                startLocationUpdates();
            }
        }

        private void startLocationUpdates() {
            //fusedLocationClient.requestLocationUpdates(locationRequest,
            //        locationCallback,
            //        Looper.getMainLooper());
        }
    */

    public void loadProfile() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        String s = preferences.getString("parentLatitude", "123");
        String t = preferences.getString("parentLongitude", "123");
        TextView parentLatitude = findViewById(R.id.parentLatitude);
        TextView parentLongitude = findViewById(R.id.parentLongitude);
        parentLatitude.setText(s);
        parentLongitude.setText(t);
    }

    public void saveProfile() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        TextView parentLatitude = findViewById(R.id.parentLatitude);
        editor.putString("parentLatitude", parentLatitude.getText().toString());
        TextView parentLongitude = findViewById(R.id.parentLongitude);
        editor.putString("parentLongitude", parentLongitude.getText().toString());
        editor.apply();
    }

    public void save(View view) {
        save();
    }

    public void save() {
        saveProfile();
        loadProfile();
        //onPause(savedInstanceState);

        Intent intent = new Intent(this, MainActivity.class);
        //this.future.cancel(true);
        finish();
        startActivity(intent);
        System.out.println("new activity");
    }
/*
    public double refresh(TextView textView, Pair<Double,Double> loc) {
        textView.setText(Double.toString(loc.first) + " , " +
                Double.toString(loc.second));

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        Double pLat = Double.parseDouble(preferences.getString("parentLatitude", "123"));
        Double pLong = Double.parseDouble(preferences.getString("parentLongitude", "123"));

        double adjacent = (pLong - loc.second);
        double hypotenuse = Math.sqrt(((pLat - loc.first) * (pLat - loc.first)) + ((pLong - loc.second) * (pLong - loc.second)));

        return Math.acos(adjacent / hypotenuse);
    }

    public void refresh(View view) {
        refresh(textView, loc);
    }

 */
}