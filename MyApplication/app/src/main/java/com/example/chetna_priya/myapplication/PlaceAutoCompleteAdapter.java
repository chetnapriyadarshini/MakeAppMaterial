package com.example.chetna_priya.myapplication;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * Created by chetna_priya on 9/17/2015.
 */
public class PlaceAutoCompleteAdapter extends ArrayAdapter<PlaceAutoCompleteAdapter.PlaceAutoComplete> {

    private ArrayList<PlaceAutoComplete> mResultList;
    private GoogleApiClient mGoogleApiClient;
    private LatLngBounds mBounds;
    private AutocompleteFilter mFilter;

    public PlaceAutoCompleteAdapter(Context context, int resource, GoogleApiClient mGoogleApiClient, LatLngBounds mBounds,
                                    AutocompleteFilter mFilter)
    {
        super(context, resource);
        this.mGoogleApiClient = mGoogleApiClient;
        this.mBounds = mBounds;
        this.mFilter = mFilter;
    }

    /**
     * Sets the bounds for all subsequent queries.
     */
    public void setBounds(LatLngBounds bounds) {
        mBounds = bounds;
    }

    /**
     * Returns the number of results received in the last autocomplete query.
     */
    @Override
    public int getCount() {
        return mResultList.size();
    }

    /**
     * Returns an item from the last autocomplete query.
     */
    @Override
    public PlaceAutoComplete getItem(int position) {
        return mResultList.get(position);
    }


    public ArrayList<PlaceAutoComplete> getAutoComplete(String constraint)
    {
        Log.d(statVars.TAG, "IN get autocomplete");

        if(!mGoogleApiClient.isConnected()) {
            Log.e(statVars.TAG, "Google Api Client not connected will not complete autocomplete req");
            return null;
        }

        PendingResult<AutocompletePredictionBuffer> result = Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClient,
                constraint,
                mBounds, null);
        AutocompletePredictionBuffer autocompletePredictions = result.await(60, TimeUnit.SECONDS);
        final Status status = autocompletePredictions.getStatus();

        if(!status.isSuccess())
        {
            Log.e(statVars.TAG, "Error getting auto complete predictions api:: " + status.toString());
            autocompletePredictions.release();
            return null;
        }

        Log.i(statVars.TAG, "Successfully completed autocompleteprediction buffer. " +
                "Received "+autocompletePredictions.getCount()+" predictions");

        Iterator<AutocompletePrediction> prediction_iterator = autocompletePredictions.iterator();

        ArrayList resultList = new ArrayList<>(autocompletePredictions.getCount());
        while (prediction_iterator.hasNext())
        {
            AutocompletePrediction prediction = prediction_iterator.next();
            resultList.add(new PlaceAutoComplete(prediction.getPlaceId(), prediction.getDescription()));
            Log.d(statVars.TAG, "Place ID: "+prediction.getPlaceId()+" Description: "+prediction.getDescription());
        }


        autocompletePredictions.release();

        return resultList;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                // Skip the autocomplete query if no constraints are given.
                if (constraint != null) {
                    // Query the autocomplete API for the (constraint) search string.
                    mResultList = getAutoComplete(constraint.toString());
                    if (mResultList != null) {
                        // The API successfully returned results.
                        results.values = mResultList;
                        results.count = mResultList.size();
                    }
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
                if (results != null && results.count > 0) {
                    // The API returned at least one result, update the data.
                    notifyDataSetChanged();
                } else {
                    // The API did not return any results, invalidate the data set.
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }


    public class PlaceAutoComplete
    {
        public CharSequence placeId;
        public CharSequence description;

        PlaceAutoComplete(CharSequence placeId, CharSequence description) {
            this.placeId = placeId;
            this.description = description;
        }

        @Override
        public String toString() {
            return description.toString();
        }
    }


}
