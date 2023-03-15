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

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        locationService = LocationService.singleton(this);

        CompassViewModel viewModel = new ViewModelProvider(this).get(CompassViewModel.class);

        Friend friend1 = new Friend();
        Friend friend2 = new Friend();
        friend1.setUid("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454");
        friend2.setUid("c81d4e2e-bcf2-11e6-869b-7df92533d2db");

        viewModel.getDao().upsert(friend1);
        viewModel.getDao().upsert(friend2);

        FriendAdapter adapter = new FriendAdapter();
        var friends = viewModel.getFriends();
        friends.observe(this, adapter::setFriends);

        var locationData = locationService.getLocation();
        locationData.observe(this, latLong -> {
            TextView locationText = findViewById(R.id.locationText);
            locationText.setText(Utilities.formatLocation(latLong.first, latLong.second));
            userLocation = latLong;
            viewModel.getFriends().observe(this, adapter::setFriends);
        });

        Log.i("CHECK", "Make sure friends locations 0,0");
        for (Friend f : adapter.getFriends()) {
            String uid = f.getUidString();
            Log.i(f.loc.toString(), uid);
        }

        adapter.getFriends().get(0).setLocation();
        adapter.getFriends().get(1).setLocation();

        Log.i("RECHECK", "Make sure friends locations changed");
        for (Friend f : adapter.getFriends()) {
            String uid = f.getUidString();
            Log.d(f.loc.toString(), uid);
        }

        new Handler().postDelayed(() -> {
            // display the locations of the friends in the database to show that they have been updated
            Log.i("RECHECK", "Make sure friends are in database");
            for (Friend f : viewModel.getDao().getAll()) {
                String uid = f.getUidString();
                Log.i(f.loc.toString(), uid);
            }
        }, 1000);

    }*/


}