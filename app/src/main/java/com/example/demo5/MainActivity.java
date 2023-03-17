package com.example.demo5;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.util.Pair;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

public class MainActivity extends AppCompatActivity {

    private LocationService locationService;
    private OrientationService orientationService;

    private Future<?> future;
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    public Pair<Double, Double> userLocation;
    private LiveData<List<Friend>> friends;
    private LiveData<Friend> friend;
    private CompassViewModel viewModel;
    private List<Friend> friendsList = Collections.EMPTY_LIST;
    private Distance distance;
    private GpsSignal gpsSignal;
    private ScheduledFuture<?> poller;
    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private LocationManager locationManager;

    public int zoomCounter;

    public ZoomFeature zoomFeature;
    private Friend bestFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationService = LocationService.singleton(this);

        CompassViewModel viewModel = new ViewModelProvider(this).get(CompassViewModel.class);

        friends = viewModel.getFriends();
        friends.observe(this, this::setFriends);

        userLocation = new Pair<Double,Double>(0.0,0.0);
        friend = viewModel.getFriend("nos");
        for (Friend curr : friendsList) {
            ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.compass);

            TextView friend = new TextView(this);

            String name = curr.getName();
            friend.setText(name);
            curr.spot = friend;

            ConstraintLayout.LayoutParams lay = new ConstraintLayout.LayoutParams(findViewById(R.id.friend).getLayoutParams());

            lay.circleConstraint = R.id.compass;
            lay.circleRadius = 400;
            lay.circleAngle = (float) angleCalculation(curr.getLocation());

            layout.addView(friend, lay);
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        System.out.println("LOCATION MANAGER: " + locationManager.toString());


        distance = new Distance(this);
        gpsSignal = new GpsSignal(this);



        this.reobserveLocation();

        zoomCounter = 2;
        zoomFeature = new ZoomFeature(zoomCounter, this);
    }

    private void setFriends(List<Friend> friends) {
        this.friendsList = friends;
    }

    public void reobserveLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        TextView gpsStatus = findViewById(R.id.gpsStatus);
        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        long lastUpdateTime = lastLocation.getTime();
        long currTime = System.currentTimeMillis();
        if (lastLocation != null) {
            long locationTimestamp = lastLocation.getTime();
            long currentTimestamp = System.currentTimeMillis();
            long timeDifference = currentTimestamp - locationTimestamp;
            if (timeDifference > 10000) {
                //gps Signal is initially off
                gpsStatus.setText("GPS Status: Offline");
            }
        }
        var locationData = locationService.getLocation();
        locationData.observe(this, this::onLocationChanged);
    }


    /**
     * If the location changes, will notify the Compass in order to place the friend at the correct
     * distance of the user
     *
     * @param latLong : The longitude and latitude of the User
     */

    public void onLocationChanged(Pair<Double, Double> latLong) {
        //this.updateGPSLabel();

        userLocation = latLong;
        whenFriendLocationChanges();
        distance.updateCompassWhenLocationChanges(latLong.first, latLong.second);

        gpsSignal.updateGPSLabel(locationManager);
    }

    private double angleCalculation(Pair<Double, Double> friendLocation) {
        return Math.atan2(friendLocation.second - userLocation.second, friendLocation.first - userLocation.first);
    }

    public void whenFriendLocationChanges() {
        //rad = angleCalculation(location);


        friend.observe(this, this::angleCalculation);

        for (Friend friend : this.friends.getValue()) {
            var bestFriendLocationData1 = friend.getLocation();

            friend.setFriendRad(angleCalculation(bestFriendLocationData1));

            ConstraintLayout.LayoutParams lay = new ConstraintLayout.LayoutParams(findViewById(R.id.friend).getLayoutParams());

            lay.circleAngle = (float) angleCalculation(friend.getLocation());
            friend.spot.setLayoutParams(lay);
            //System.out.println(friend.getName());
        }

    }

    private void angleCalculation(Friend friend) {
        if (friend == null)
            return;

        friend.updateAngle(userLocation);
        TextView bestFriend = findViewById(R.id.best_friend);
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams)
                bestFriend.getLayoutParams();
        layoutParams.circleAngle = (float) Math.toDegrees(friend.getFriendRad());
        bestFriend.setLayoutParams(layoutParams);
    }


    public void submit(View view) {
        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.compass);

        TextView friend = new TextView(this);

        EditText inp = (EditText) findViewById(R.id.enter_uid);
        String name = inp.getText().toString();
        friend.setText(name);

        Friend newfriend = new Friend();
        newfriend.setName(name);
        this.friends.getValue().add(newfriend);
        newfriend.spot = friend;
        viewModel.getDao().upsert(newfriend);

        //friend.setId(5);
        ConstraintLayout.LayoutParams lay = new ConstraintLayout.LayoutParams(findViewById(R.id.friend).getLayoutParams());

        lay.circleConstraint = R.id.compass;
        lay.circleRadius = 400;
        lay.circleAngle = (float) angleCalculation(newfriend.getLocation());
        //friend.setLayoutParams(linearLayout.getLayoutParams());
        layout.addView(friend, lay);

        //friend.setText("Jay");

    }

    public void onZoomInClick(View view) {
        assert view instanceof  Button;
        Button btn  = (Button) view;

        //can be zoomed in
        if(zoomCounter != 1) {
            zoomCounter--;
            zoomFeature= new ZoomFeature(zoomCounter, this);
        }
        //cannot be zoomed in anymore
        else {
            zoomFeature = new ZoomFeature(1, this);
        }
    }

    public void onZoomOutClick(View view) {
        assert view instanceof  Button;
        Button btn = (Button) view;

        //can be zoomed out
        if(zoomCounter != 4) {
            zoomCounter++;
            zoomFeature = new ZoomFeature(zoomCounter, this);
        }
        //cannot be zoomed out anymore
        else {
            zoomFeature = new ZoomFeature(4, this);
        }
    }
}

