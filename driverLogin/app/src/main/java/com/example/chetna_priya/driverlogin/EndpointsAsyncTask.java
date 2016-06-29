/*
package com.example.chetna_priya.driverlogin;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Pair;
import android.widget.Toast;

import com.example.chetna_priya.driverlogin.form.DriverRegForm;
import com.example.chetna_priya.myapplication.backend.driverApi.DriverApi;
import java.io.IOException;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
*/
/**
 * Created by chetna_priya on 1/27/2016.
 *//*


public class EndpointsAsyncTask extends AsyncTask<DriverRegForm, Void, Object> {
    private static DriverApi myApiService = null;
    private Context context;

    @Override
    protected Object doInBackground(DriverRegForm... params) {
        if(myApiService == null) {  // Only do this once
            DriverApi.Builder builder = new DriverApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    // options for running against local devappserver
                    // - 10.0.2.2 is localhost's IP address in Android emulator
                    // - turn off compression when running against local devappserver
                    .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
            // end options for devappserver

            myApiService = builder.build();
        }

     //   context = params[0].first;
     //   String name = params[0].second;

        try {
            return myApiService.saveProfile(params[0]).execute();
          //  return myApiService.sayHi(name).execute().getData();
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    }
*/
