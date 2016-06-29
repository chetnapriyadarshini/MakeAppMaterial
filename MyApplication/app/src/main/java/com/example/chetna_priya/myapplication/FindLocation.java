package com.example.chetna_priya.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.CursorJoiner;
import android.location.Address;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;

/**
 * Created by chetna_priya on 8/21/2015.
 */
public class FindLocation implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, buildGoogleApiClient
{
    Context mContext;
    ArrayList mAddressOutput;
    private AddressResultReceiver mResultReceiver;
    Handler mAddressHandler;

    public FindLocation(Context context)
    {
        mContext = context;
        mAddressHandler = new Handler();
        if(checkLocationEnabled())
             buildGoogleApiClient();
    }

    public void resume()
    {
        if(!checkLocationEnabled())
            return;
        if(statVars.mGoogleApiClient == null)
            buildGoogleApiClient();
    }




    public void buildGoogleApiClient()
    {
        statVars.mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .build();

     //   if(!mGoogleApiClient.isConnected())
               statVars.mGoogleApiClient.connect();

    }
    @Override
    public void onConnected(Bundle bundle)
    {
        mResultReceiver = new AddressResultReceiver(mAddressHandler);

        statVars.pickupLocation = LocationServices.FusedLocationApi.getLastLocation(statVars.mGoogleApiClient);
        if(statVars.pickupLocation != null)
        {
          //  MainActivity.lat_view.append(String.valueOf(mLastLocation.getLatitude()));
          //  MainActivity.long_view.append(String.valueOf(mLastLocation.getLongitude()));
        /*    Intent intent = new Intent(mContext,FindAddressfromLocation.class);
            intent.putExtra(Constants.LOCATION_DATA_EXTRA,mLastLocation);
            mResultReceiver = new AddressResultReceiver(mAddressHandler);
            intent.putExtra(Constants.RECEIVER, mResultReceiver);
            mContext.startService(intent);
*/
            //Commented the above code as i now want to fetch the address from the places api of google instead of the geocoder api
            PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace(statVars.mGoogleApiClient,null);
            if(result == null)
                Log.e(statVars.TAG,"NO result received");
            try {
                result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                    @Override
                    public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                        Log.d(statVars.TAG, "In ON result likelyplaces = " + likelyPlaces);
                        ArrayList addressList;
                        try {
                            addressList = new ArrayList(likelyPlaces.getCount());
                            int i=0;
                            for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                                Log.d(statVars.TAG, "Name: " + placeLikelihood.getPlace().getName() + " Likelihood: " + placeLikelihood.getLikelihood());
                                addressList.add(i, placeLikelihood.getPlace().getName().toString());
                                i++;
                            }
                        } finally {
                            likelyPlaces.release();
                        }
                        if (addressList != null) {
                            Bundle bundle = new Bundle();
                            bundle.putStringArrayList(Constants.RESULT_DATA_KEY, addressList);
                            mResultReceiver.send(Constants.SUCCESS_RESULT, bundle);
                        }
                    }
                });
            }
            catch(Exception e)
            {
              Log.d(statVars.TAG, "Exception in fetching address");
            }
        }
    //    else
      //      Log.e(MainActivity.TAG, "mLast location is null");

        if(statVars.mGoogleApiClient == null)
            Log.e(statVars.TAG, "mGoogleApiClient is null");

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(statVars.TAG, "Connection suspendedddddddd");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(statVars.TAG, "Connection faileddddd");

    }

    public class AddressResultReceiver extends ResultReceiver
     {

        /**
         * Create a new ResultReceive to receive results.  Your
         * {@link #onReceiveResult} method will be called from the thread running
         * <var>handler</var> if given, or from an arbitrary thread if null.
         *
         * @param handler
         */
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

         @Override
         protected void onReceiveResult(int resultCode, Bundle resultData) {
             if(resultCode == Constants.SUCCESS_RESULT) {
                 mAddressOutput =  resultData.getStringArrayList(Constants.RESULT_DATA_KEY);
                 ArrayAdapter<Address> addressArrayAdapter = new ArrayAdapter<Address>(mContext,
                         android.R.layout.simple_list_item_1,mAddressOutput);
                 statVars.addressList.setAdapter(addressArrayAdapter);
                 statVars.addressList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                     @Override
                     public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                         statVars.user_add.setText(mAddressOutput.get(position).toString());
                     }
                 });
                 statVars.mButton_pickupLocation.setEnabled(true);
                 statVars.mButton_dropLocation.setEnabled(true);
                 statVars.user_add.setVisibility(View.VISIBLE);
                 statVars.user_add.setText(mAddressOutput.get(0).toString());
                 statVars.pickupAddress = mAddressOutput.get(0).toString();
             }
             else
               //  mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
                statVars.add_view.append(resultData.getString(Constants.RESULT_DATA_KEY));

         }
     }

    public boolean checkLocationEnabled()
    {
        if(!statVars.locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage("You need to enable GPS in your device for this app to work!");
            builder.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    mContext.startActivity(intent);
                }
            });

            builder.setNegativeButton("Exit App", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ((Activity) mContext).finish();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
            return false;
        }
        else
            return true;

       /* Criteria criteria = new Criteria();
        MainActivity.provider = MainActivity.locationManager.getBestProvider(criteria,false);
        Location location = MainActivity.locationManager.getLastKnownLocation(MainActivity.provider);

        if(location != null)
        {
            onLocationChanged(location);
        }
        else
            Log.d("Debuggg", "LOCATION IS NULLLL");
*/
    }

}
