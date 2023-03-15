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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);
        Log.i("BEGAN", "Program start");

        locationService = LocationService.singleton(this);
        CompassViewModel viewModel = new ViewModelProvider(this).get(CompassViewModel.class);

        friends = viewModel.getFriends();
        viewModel.putFriend("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454");

        future = backgroundThreadExecutor.submit(() -> {
            friends.observe(this, this::onFriendLocationChanged);
        });

        //viewModel.getFriend("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454").setLocation();

    }

    private void onFriendLocationChanged(List<Friend> friends) {
        for (Friend f : friends) {
            Log.i(f.getLocation().toString(), f.getUidString());
        }
    }
}