package com.example.chetna_priya.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.provider.Settings;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.google.android.gms.maps.MapFragment;

public class GetDropLocation extends Activity implements ResultObserver {

    private ResultSubject addressResult;
    private MapFragment mapFragment;
    private AutoCompleteTextView possibleaddresses;
    private Button nextButton;
    private Location location;
    private ViewOnMap viewOnMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_drop_location);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.viewonMapFragment);
        possibleaddresses = (AutoCompleteTextView) findViewById(R.id.enter_add);
        nextButton = (Button) findViewById(R.id.button_nextButton);

        Intent intent = getIntent();
        location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);

        viewOnMap = new ViewOnMap(this,possibleaddresses,location,mapFragment);
        viewOnMap.registerObserver(this);
        this.setSubject(viewOnMap);

        possibleaddresses.setHint("Enter Drop Address");
        nextButton.setText("Book A Cab");

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder =  new AlertDialog.Builder(GetDropLocation.this);
                if(statVars.dropLocation == null)
                {
                    builder.setMessage("Please Enter a valid drop location!!");
                }
                else
                   builder.setMessage("PICKUP ADDRESS: " + statVars.pickupAddress + "\n\nDROP ADDRESS: " + statVars.dropAddress);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(statVars.dropLocation != null) {
                            Intent bookCab = new Intent(GetDropLocation.this, BookCabActivity.class);
                            bookCab.putExtra(Constants.LOCATION_DATA_EXTRA, statVars.pickupLocation);
                            bookCab.putExtra(Constants.FOR_PICKUP, false);
                            startActivity(bookCab);
                        }
                    }
                });

                builder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });


                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_get_drop_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(statVars.mGoogleApiClient!= null)
            statVars.mGoogleApiClient.disconnect();

        Log.d(statVars.TAG, "mGoogleApiClientDisconnected");
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewOnMap.resume();
        if(!statVars.mGoogleApiClient.isConnected())
            statVars.mGoogleApiClient.connect();
        Log.d(statVars.TAG, "mGoogleApiClientConnected");
    }


    @Override
    public void update() {
        Location location = (Location) addressResult.getUpdate(this);
        if(location == null)
            Log.d(statVars.TAG, "No address received");
        statVars.dropLocation = location;
        statVars.dropAddress = possibleaddresses.getText().toString();
        Log.d(statVars.TAG, "RECEIVED DROP LOCATION:  "+statVars.dropLocation);

    }

    @Override
    public void setSubject(ResultSubject resultSubject) {
        this.addressResult = resultSubject;
    }
}
