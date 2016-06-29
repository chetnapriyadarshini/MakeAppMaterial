package com.example.chetna_priya.myapplication;

import android.app.Service;
import android.content.Intent;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    FindLocation findLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        statVars.locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
        findLocation = new FindLocation(this);
        statVars.mButton_dropLocation = ((Button)findViewById(R.id.drop_Location));
        statVars.mButton_dropLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(statVars.TAG, "Clickedddddd");
                Intent getDropLocation = new Intent(MainActivity.this,GetDropLocation.class);
                if(statVars.pickupLocation != null) {
                    getDropLocation.putExtra(Constants.LOCATION_DATA_EXTRA, statVars.pickupLocation);
                    getDropLocation.putExtra(Constants.FOR_PICKUP, false);
                    startActivity(getDropLocation);
                }
            }
        });
        statVars.mButton_pickupLocation = (Button) findViewById(R.id.pickup_location);
        statVars.mButton_pickupLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getPickupLocation = new Intent(MainActivity.this, GetPickupLocation.class);
                if (statVars.pickupLocation != null) {
                    getPickupLocation.putExtra(Constants.LOCATION_DATA_EXTRA, statVars.pickupLocation);
                    getPickupLocation.putExtra(Constants.FOR_PICKUP, true);
                    startActivity(getPickupLocation);
                }
            }
        });
        statVars.mButton_pickupLocation.setEnabled(false);
        statVars.mButton_dropLocation.setEnabled(false);
        statVars.add_view = (TextView) findViewById(R.id.address_view);
        statVars.addressList = (ListView) findViewById(R.id.address_view_list);
        statVars.user_add = (TextView) findViewById(R.id.useraddress);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    protected void onResume()
    {
        super.onResume();
        findLocation.resume();
        if(statVars.mGoogleApiClient!= null)
            statVars.mGoogleApiClient.connect();
        Log.d(statVars.TAG, "mGoogleApiClientConnected");

    }
}
