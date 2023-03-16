package com.example.demo5;

import android.os.Bundle;
import android.util.Log;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import androidx.appcompat.app.AppCompatActivity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);
        Log.i("BEGAN", "Program start");

        locationService = LocationService.singleton(this);
        CompassViewModel viewModel = new ViewModelProvider(this).get(CompassViewModel.class);

        //friends = viewModel.getFriends();
        /*Friend austin = new Friend();
        austin.setUid("austin");
        viewModel.save(austin);*/
        friend = viewModel.getFriend("nos");

        //friends.observe(this, this::onFriendLocationChanged);
        /*var flist = friends.getValue();
        for (Friend f : flist) {
            Log.i(f.getLatitude() + ", " + f.getLongitude(), f.getLabel());
        }*/
    }

    private void onFriendLocationChanged(List<Friend> friends) {
        for (Friend f : friends) {
            Log.i(f.getLatitude() + ", " + f.getLongitude(), f.getLabel());
        }
    }
}