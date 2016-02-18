package com.nigelcardozo.screenscraper;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

public class LocationHelper {

    //This is an arbitrary number. There is no real reasoning behind the value other
    //than it seems a sensible amount of time.
    private static final long GPS_SEARCH_TIMEOUT_VAL = 20000;

    boolean listening=false;
    Timer tmrLocationTimer;
    LocationManager locationManager;
    LocationResult locationResultHandler;
    boolean gpsEnabled=false;
    boolean networkEnabled=false;

    public boolean getLocation(Context context, LocationResult result)
    {
        locationResultHandler=result;

        if(locationManager==null)
        {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }

        //Need a try/catch here as we may not have such a provider.
        try{
            listening = true;
            gpsEnabled=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            networkEnabled=locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }
        catch(Exception ex)
        {
            return false;
        }

        if (!gpsEnabled && !networkEnabled)
        {
            return false;
        }
        else
        {
            //Attempt to get location via network and gps.
            if (networkEnabled)
            {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, gpsNetworkListener);
            }

            if (gpsEnabled) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, gpsLocationListener);
            }

            //We use a timer to ensure timeout in the case of not finding a location within a fixed period
            tmrLocationTimer=new Timer();
            tmrLocationTimer.schedule(new GpsSearchTimeOutHandler(), GPS_SEARCH_TIMEOUT_VAL);
            return true;
        }
    }


    LocationListener gpsNetworkListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            //We've got a location, stop the timer, pass the result to the callback function
            //and stop subscribing to updates
            tmrLocationTimer.cancel();

            //This check is used since both the network and gps may respond separately despite
            //the cancel request being sent. It's a preventative measure to avoid updating twice
            if (listening == true)
            {
                locationResultHandler.locationResponseHandler(location);
                listening = false;
            }

            locationManager.removeUpdates(this);
        }

        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            //We've got a location, stop the timer, pass the result to the callback function
            //and stop subscribing to updates
            tmrLocationTimer.cancel();

            //This check is used since both the network and gps may respond separately despite
            //the cancel request being sent. It's a preventative measure to avoid updating twice
            if (listening == true)
            {
                locationResultHandler.locationResponseHandler(location);
                listening = false;
            }

            locationManager.removeUpdates(this);
        }

        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    class GpsSearchTimeOutHandler extends TimerTask {
        @Override
        public void run() {
            getLastKnownLocation();
        }

        private void getLastKnownLocation()
        {
            //We timed out, no longer subscribe to location updates
            locationManager.removeUpdates(gpsLocationListener);
            locationManager.removeUpdates(gpsNetworkListener);

            Location gpsLocation=null;
            Location networkLocation=null;
            if(gpsEnabled)
                gpsLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(networkEnabled)
                networkLocation=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if((gpsLocation!=null) && (networkLocation != null)){

                //We got matches from both, take whichever is more recent
                if (gpsLocation.getTime() > networkLocation.getTime())
                {
                    //Call our handler and pass it the last known location
                    locationResultHandler.locationResponseHandler(gpsLocation);
                }
                else
                {
                    //Call our handler and pass it the last known location
                    locationResultHandler.locationResponseHandler(networkLocation);
                }
                return;
            }

            //We failed to get a cached result
            locationResultHandler.locationResponseHandler(null);
        }
    }

    public static abstract class LocationResult{

        //This is implemented in MainActivity.java
        //It is used as a callback when we receive
        //location information.
        public abstract void locationResponseHandler(Location location);
    }

}
