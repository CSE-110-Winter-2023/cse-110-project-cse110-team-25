package com.example.demo5;

import com.google.gson.annotations.SerializedName;

import java.util.Random;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "friends")
public class Friend {
    @SerializedName("name")
    public String name;

    @SerializedName("loc")
    Pair<Double, Double> loc;

    private double friendRad;
    @PrimaryKey
    @NonNull
    @SerializedName("uid")
    UUID uid;

    Friend() {
        loc = new Pair<Double,Double>(0.0,0.0);
        uid = UUID.randomUUID();
    }

    public void setFriendRad(double val) {
        this.friendRad = val;
    }

    public double getFriendRad() {
        return this.friendRad;
    }

    public UUID getUid() {
        return uid;
    }

    public void setUid(String uuid) {
        uid = UUID.fromString(uuid);
    }

    public String getUidString() {
        return getUid().toString();
    }

    public Pair<Double, Double> getLocation() {
        return loc;
    }

    public void setLocation() {
        Double newLat = (new Random()).nextDouble() * 200 - 100;
        Double newLong = (new Random()).nextDouble() * 200 - 100;
        loc = new Pair<>(newLat, newLong);
    }
}

