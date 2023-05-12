package com.berkayguven.coffegooo;

import android.location.Location;

import com.google.android.gms.maps.internal.ILocationSourceDelegate;
import com.google.android.gms.maps.model.LatLng;

public class Place{
    String name ;
    LatLng location ;

    public Place(String name, LatLng location){
        this.name = name;
        this.location = location;
    }
}

