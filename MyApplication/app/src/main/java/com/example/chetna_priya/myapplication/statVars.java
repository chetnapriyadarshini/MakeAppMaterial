package com.example.chetna_priya.myapplication;

import android.location.Location;
import android.location.LocationManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by chetna_priya on 9/23/2015.
 */
public class statVars
{

    static TextView add_view, user_add;
    static ListView addressList;
    static LocationManager locationManager;
    static String provider;
    static String TAG = "Debug";
    static Button mButton_pickupLocation, mButton_dropLocation;
    static GoogleApiClient mGoogleApiClient;
    static Location pickupLocation;
    static Location dropLocation;
    static String pickupAddress, dropAddress;

}
