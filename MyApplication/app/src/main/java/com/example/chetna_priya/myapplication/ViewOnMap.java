package com.example.chetna_priya.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.service.carrier.CarrierMessagingService;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class ViewOnMap implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener, buildGoogleApiClient, ResultSubject
{

    private MapFragment mapFragment;
    private Location location;
    private float zoomInAmt = 14;
    protected AutoCompleteTextView possibleaddresses;
    private getAddressSuggestions getaddressSuggestions;
    private TimerTask autoCompleteTask;
    private ArrayList addressList;
    private PlaceAutoCompleteAdapter addressAdapter;
    private final int threshold = 2;
    private Timer timer;
    private boolean isTimerTaskCompleted = true;
    protected Location userSelectedLocation;
    private final double HEADING_NORTH_EAST = 45;
    private final double HEADING_SOUTH_WEST = 215;
    private final double diagonalBoundsSize = 10000; // 10km
    private GoogleMap gMap;
    protected Button nextButton;
    private List<ResultObserver> observerList;
    private final Object MUTEX = new Object();
    private boolean locationAvailable = false;
    private Context mContext;

    public ViewOnMap(Context context, final AutoCompleteTextView possibleaddresses, Location location,
                     MapFragment mapFragment)
    {
        this.mContext = context;
        this.possibleaddresses = possibleaddresses;
        this.location = location;
        this.mapFragment = mapFragment;
        statVars.mGoogleApiClient = null;
        if(checkLocationEnabled())
          buildGoogleApiClient();
        timer = new Timer();

        LatLng center = new LatLng(location.getLatitude(),location.getLongitude());
        LatLng northEast = SphericalUtil.computeOffset(center,diagonalBoundsSize/2,HEADING_NORTH_EAST);
        LatLng southWest = SphericalUtil.computeOffset(center,diagonalBoundsSize/2,HEADING_SOUTH_WEST);
        LatLngBounds mbounds = new LatLngBounds(southWest, northEast);



        addressAdapter = new PlaceAutoCompleteAdapter(mContext,android.R.layout.simple_list_item_1,
                statVars.mGoogleApiClient,mbounds, null);
        possibleaddresses.setAdapter(addressAdapter);
        possibleaddresses.setThreshold(threshold);
        this.observerList = new ArrayList<>();
        possibleaddresses.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (possibleaddresses.getText().toString().length() >= threshold) {
                    startTimer();
                    Log.d(statVars.TAG, "Start the timerrrrrrrr");
                } else
                    Log.d(statVars.TAG, "Length:  222222222222222  " + possibleaddresses.getText().length());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        possibleaddresses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                possibleaddresses.performCompletion();
                InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(possibleaddresses.getWindowToken(), 0);
                final PlaceAutoCompleteAdapter.PlaceAutoComplete item_place = addressAdapter.getItem(position);
                final String PlaceId = String.valueOf(item_place.placeId);
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(statVars.mGoogleApiClient,PlaceId);
                placeResult.setResultCallback(mUpdateDetailsCallback);

            }
        });
        mapFragment.getMapAsync(this);

    }


    protected ResultCallback<PlaceBuffer> mUpdateDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(statVars.TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            LatLng userLocation = place.getLatLng();
            if(gMap != null) {
                gMap.addMarker(new MarkerOptions().position(userLocation).title(possibleaddresses.getText().toString())).showInfoWindow();
                gMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
                gMap.animateCamera(CameraUpdateFactory.zoomTo(zoomInAmt));
            }

            userSelectedLocation = new Location("");
            userSelectedLocation.setLatitude(userLocation.latitude);
            userSelectedLocation.setLongitude(userLocation.longitude);
            Log.d(statVars.TAG, "Calling notify result observers");
            locationAvailable = true;
            notifyResultObservers();

        }
    };


    private void startTimer()
    {
        if(isTimerTaskCompleted && statVars.mGoogleApiClient.isConnected()) {
            isTimerTaskCompleted = false;
            initializeTimerTask();
            timer.schedule(autoCompleteTask, 2000);
        }
        else
            Log.d(statVars.TAG, "could not execute:: isTimerTaskCompleted: "+isTimerTaskCompleted
            +" statVars.mGoogleApiClient.isConnected:  "+statVars.mGoogleApiClient.isConnected());
    }

    private void initializeTimerTask() {

        autoCompleteTask = new TimerTask() {
            @Override
            public void run() {
                new getAddressSuggestions().execute(possibleaddresses.getText().toString());

            }
        };
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(statVars.TAG, "googleApiClient connected in viewOnMap");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(statVars.TAG, "googleApiClient connection suspended in viewOnMap");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(statVars.TAG, "googleApiClient connection failed in viewOnMap");
    }


    @Override
    public void buildGoogleApiClient() {
        statVars.mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Places.GEO_DATA_API)
                .build();

        statVars.mGoogleApiClient.connect();

    }

    @Override
    public void resume() {
        if(!checkLocationEnabled())
            return;
        if(statVars.mGoogleApiClient == null) {
            buildGoogleApiClient();
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
    }



    @Override
    public void registerObserver(ResultObserver resultObserver) {
        if(resultObserver == null)
            throw new NullPointerException("Null Pointer Exception!!");
        synchronized (MUTEX)
        {
            if(!observerList.contains(resultObserver))
            {
                observerList.add(resultObserver);
                Log.d(statVars.TAG, "ADDED OBSERVER!!!!!!!!!");
            }
        }
    }

    @Override
    public void unregisterObserver(ResultObserver resultObserver) {
        synchronized (MUTEX) {
            observerList.remove(resultObserver);
            Log.d(statVars.TAG, "REMOVED OBSERVER!!!!!!!!!");
        }
    }

    @Override
    public void notifyResultObservers() {
        List<ResultObserver> observersLocal;
        Log.d(statVars.TAG, "In notify result observers");
        synchronized (MUTEX)
        {
            if(!locationAvailable)
                return;
            observersLocal =  new ArrayList<>(this.observerList);
            locationAvailable = false;
            for(ResultObserver resultObserver: observersLocal) {
                Log.d(statVars.TAG, "Notify observer");
                resultObserver.update();
            }
        }
    }

    @Override
    public Object getUpdate(ResultObserver resultObserver) {
        return userSelectedLocation;
    }



    private class getAddressSuggestions extends AsyncTask<String,Void,Void>
    {

        @Override
        protected Void doInBackground(String... params) {
            Log.d(statVars.TAG, "IN do in backgr of viewonmap");
           final ArrayList resultList = addressAdapter.getAutoComplete(params.toString());
            if(resultList == null)
            {
                Log.e(statVars.TAG, "No results received");
                return null;
            }

            ((Activity)mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addressAdapter.addAll(resultList);
                    addressAdapter.notifyDataSetChanged();

//stuff that updates ui

                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            isTimerTaskCompleted = true;
        }
    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng userLocation;
        this.gMap = googleMap;
        if(location != null)
            userLocation  = new LatLng(location.getLatitude(), location.getLongitude());
        else
            userLocation = new LatLng(0,0);

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(zoomInAmt));
        googleMap.addMarker(new MarkerOptions().position(userLocation));


    }
}
