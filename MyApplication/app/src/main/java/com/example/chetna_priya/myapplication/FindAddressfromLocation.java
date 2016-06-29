package com.example.chetna_priya.myapplication;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by chetna_priya on 8/25/2015.
 */
public class FindAddressfromLocation extends IntentService{
    private ResultReceiver mResultReceiver;
    private ArrayList mAddressList;
    private int numAddresses = 5;

    public FindAddressfromLocation(String name) {
        super(name);
    }

    public FindAddressfromLocation() {
        super("Address fetching service");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        String errorMessage = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        Location location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);
        mResultReceiver = intent.getParcelableExtra(Constants.RECEIVER);

        List<Address> addresses = null;

        try
        {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(),5);
        }
        catch (IOException ioException)
        {
            errorMessage = "Geocoder service is not available";
        }

        catch (IllegalArgumentException illegalarguementexception)
        {
            errorMessage = "Latitude and Longitude values are not valid";
        }
        if(addresses == null || addresses.size() == 0) {
            mAddressList = new ArrayList<String>(1);
            mAddressList.add(0,errorMessage);
            errorMessage = "Sorry no matching addresses were found for given location";
            deliverResultsToReceiver(Constants.FAILURE_RESULT,mAddressList);
            return;
        }

        mAddressList = new ArrayList<String>(numAddresses);

        for(int i=0;i<numAddresses;i++)
        {
            ArrayList<String> addressFragments = new ArrayList<String>();
            for(int j=0;j<addresses.get(i).getMaxAddressLineIndex();j++)
            {
                addressFragments.add(j, addresses.get(i).getAddressLine(j));
            }
            mAddressList.add(i, TextUtils.join(System.getProperty("line.separator"), addressFragments));
        }

        deliverResultsToReceiver(Constants.SUCCESS_RESULT,mAddressList);

    }

    public void deliverResultsToReceiver(int resultCode, ArrayList<String> resultOrmessage)
    {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(Constants.RESULT_DATA_KEY, resultOrmessage);
        mResultReceiver.send(resultCode,bundle);
    }
}
