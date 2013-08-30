package com.brudan.bodapay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MainActivity extends FragmentActivity implements LocationListener {
	
	GoogleMap googleMap;
	public static Location mLocation;
	public static Location dLocation;
	public static String message;
	private TextView mAddress;
	public static String dAddress;
    private ProgressBar mActivityIndicator;
    private Marker marker;
    JSONParser jParser = new JSONParser();
    private int distance;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mAddress = (TextView) findViewById(R.id.tv_location);
	    mActivityIndicator = (ProgressBar) findViewById(R.id.address_progress);

		
		// Getting Google Play availability status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

        // Showing status
        if(status!=ConnectionResult.SUCCESS){ // Google Play Services are not available
        	
        	int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();
            
        }else {	// Google Play Services are available	
		
			// Getting reference to the SupportMapFragment of activity_main.xml
			SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
			
			// Getting GoogleMap object from the fragment
			googleMap = fm.getMap();
			
			// Enabling MyLocation Layer of Google Map
			googleMap.setMyLocationEnabled(true);				
					
			
			 // Getting LocationManager object from System Service LOCATION_SERVICE
	        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
	
	        // Creating a criteria object to retrieve provider
	        Criteria criteria = new Criteria();
	
	        // Getting the name of the best provider
	        String provider = locationManager.getBestProvider(criteria, true);
	
	        // Getting Current Location
	        mLocation = locationManager.getLastKnownLocation(provider);
	
	        if(mLocation!=null){
                getAddress();
	        }
	
	        locationManager.requestLocationUpdates(provider, 20000, 0, this);
        }
		
	}
	
	@Override
    protected void onResume() {
        super.onResume();
        if(dAddress == null){
        	dAddress = "";
        } else {
        	Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        	List<Address> addresses;
			try {
				addresses = geocoder.getFromLocationName(dAddress, 1);
				Address address = addresses.get(0);
	        	double longitude = address.getLongitude();
	        	double latitude = address.getLatitude();
	        	LatLng latLng = new LatLng(latitude, longitude);
	        	googleMap.clear();
	        	marker = googleMap.addMarker(new MarkerOptions().position(latLng));
//	        	String json = jParser.getJSONFromUrl(makeURL(mLocation.getLatitude(), mLocation.getLongitude(), latitude, longitude));
	        	(new connectAsyncTask(makeURL(mLocation.getLatitude(), mLocation.getLongitude(), latitude, longitude))).execute();
//	        	drawPath(json);
//	        	googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//	    		googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
	        	String str = "Your journey should cost approx. Ugx " + (int)(distance*700*0.000621371);
	        	mAddress.setText(str);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        }
//        Toast.makeText(this, dAddress, Toast.LENGTH_SHORT).show();
    }
	
	public void moveTheCamera(Location location){
		double latitude = location.getLatitude();
		double longitude = location.getLongitude();
		LatLng latLng = new LatLng(latitude, longitude);
		googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
		googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
	}
	
	public String makeURL (double sourcelat, double sourcelog, double destlat, double destlog ){
        StringBuilder urlString = new StringBuilder();
        urlString.append("http://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString.append(Double.toString( sourcelog));
        urlString.append("&destination=");// to
        urlString.append(Double.toString( destlat));
        urlString.append(",");
        urlString.append(Double.toString( destlog));
        urlString.append("&sensor=false&mode=walking&alternatives=true");
        return urlString.toString();
	}
	
	public void drawPath(String result) {

	    try {
	            //Tranform the string into a json object
	           final JSONObject json = new JSONObject(result);
	           JSONArray routeArray = json.getJSONArray("routes");
	           JSONObject routes = routeArray.getJSONObject(0);
	           JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
	           JSONObject leg = routes.getJSONArray("legs").getJSONObject(0);
	           JSONArray steps = leg.getJSONArray("steps");
	           int numSteps = steps.length();
	           distance = 0;
	           for (int i = 0; i < numSteps; i++) {
                   //Get the individual step
                   final JSONObject step = steps.getJSONObject(i);
                   final int length = step.getJSONObject("distance").getInt("value");
                   distance += length;
	           }
	           
	           String encodedString = overviewPolylines.getString("points");
	           List<LatLng> list = decodePoly(encodedString);

	           for(int z = 0; z<list.size()-1;z++){
	                LatLng src= list.get(z);
	                LatLng dest= list.get(z+1);
	                Polyline line = googleMap.addPolyline(new PolylineOptions()
	                .add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude, dest.longitude))
	                .width(2)
	                .color(Color.BLUE).geodesic(true));
	            }

	    } 
	    catch (JSONException e) {

	    }
	}
	
	private List<LatLng> decodePoly(String encoded) {

	    List<LatLng> poly = new ArrayList<LatLng>();
	    int index = 0, len = encoded.length();
	    int lat = 0, lng = 0;

	    while (index < len) {
	        int b, shift = 0, result = 0;
	        do {
	            b = encoded.charAt(index++) - 63;
	            result |= (b & 0x1f) << shift;
	            shift += 5;
	        } while (b >= 0x20);
	        int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	        lat += dlat;

	        shift = 0;
	        result = 0;
	        do {
	            b = encoded.charAt(index++) - 63;
	            result |= (b & 0x1f) << shift;
	            shift += 5;
	        } while (b >= 0x20);
	        int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	        lng += dlng;

	        LatLng p = new LatLng( (((double) lat / 1E5)),
	                 (((double) lng / 1E5) ));
	        poly.add(p);
	    }

	    return poly;
	}

	@Override
	public void onLocationChanged(Location location) {
		
//		TextView tvLocation = (TextView) findViewById(R.id.tv_location);
//		
//		// Getting latitude of the current location
//		double latitude = location.getLatitude();
//		
//		// Getting longitude of the current location
//		double longitude = location.getLongitude();		
//		
//		// Creating a LatLng object for the current location
//		LatLng latLng = new LatLng(latitude, longitude);
//		
//		// Showing the current location in Google Map
//		googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//		
//		// Zoom in the Google Map
//		googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
//		
//		// Setting latitude and longitude in the TextView tv_location
//		tvLocation.setText("Latitude:" +  latitude  + ", Longitude:"+ longitude );
				
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub		
	}
	
	/**
	    * A subclass of AsyncTask that calls getFromLocation() in the
	    * background. The class definition has these generic types:
	    * Location - A Location object containing
	    * the current location.
	    * Void     - indicates that progress units are not used
	    * String   - An address passed to onPostExecute()
	    */
    private class GetAddressTask extends AsyncTask<Location, Void, String> {
        Context mContext;
        public GetAddressTask(Context context) {
            super();
            mContext = context;
        }
	        
        /**
         * Get a Geocoder instance, get the latitude and longitude
         * look up the address, and return it
         *
         * @params params One or more Location objects
         * @return A string containing the address of the current
         * location, or an empty string if no address can be found,
         * or an error message
         */
        @Override
        protected String doInBackground(Location... params) {
            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
            // Get the current location from the input parameter list
            Location loc = params[0];
            // Create a list to contain the result address
            List<Address> addresses = null;
            try {
                /*
                 * Return 1 address.
                 */
                addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            } catch (IOException e1) {
	            Log.e("LocationSampleActivity",
	                    "IO Exception in getFromLocation()");
	            e1.printStackTrace();
	            return ("IO Exception trying to get address");
            } catch (IllegalArgumentException e2) {
	            // Error message to post in the log
	            String errorString = "Illegal arguments " +
	                    Double.toString(loc.getLatitude()) +
	                    " , " +
	                    Double.toString(loc.getLongitude()) +
	                    " passed to address service";
	            Log.e("LocationSampleActivity", errorString);
	            e2.printStackTrace();
	            return errorString;
            }
            // If the reverse geocode returned an address
            if (addresses != null && addresses.size() > 0) {
                // Get the first address
                Address address = addresses.get(0);
                /*
                 * Format the first line of address (if available),
                 * city, and country name.
                 */
                message = String.format(
                        "%s, %s, %s",
                        // If there's a street address, add it
                        address.getMaxAddressLineIndex() > 0 ?
                                address.getAddressLine(0) : "",
                        // Locality is usually a city
                        address.getLocality(),
                        // The country of the address
                        address.getCountryName());
                // Return the text
                return message;
            } else {
                return "No address found";
            }
        }
        
        @Override
        protected void onPostExecute(String address) {
            // Set activity indicator visibility to "gone"
            mActivityIndicator.setVisibility(View.GONE);
            // Display the results of the lookup.
            mAddress.setText(address);
            moveTheCamera(mLocation);
        }

	}
    
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public void getAddress() {
        // Ensure that a Geocoder services is available
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD && Geocoder.isPresent()) {
            // Show the activity indicator
            mActivityIndicator.setVisibility(View.VISIBLE);
            /*
             * Reverse geocoding is long-running and synchronous.
             * Run it on a background thread.
             * Pass the current location to the background task.
             * When the task finishes,
             * onPostExecute() displays the address.
             */
            (new GetAddressTask(this)).execute(mLocation);
        }
        
    }

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
	    case R.id.menu_about:
	        intent = new Intent(this, AboutActivity.class);
	        startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
	        return true;
	    case R.id.menu_map:
	    	Toast.makeText(this, item.getTitle(), Toast.LENGTH_LONG).show();
	        return true;
	    case R.id.menu_home:
	    	intent = new Intent(this, HomeActivity.class);
	    	startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	private class connectAsyncTask extends AsyncTask<Void, Void, String>{
	    private ProgressDialog progressDialog;
	    String url;
	    connectAsyncTask(String urlPass){
	        url = urlPass;
	    }
	    @Override
	    protected void onPreExecute() {
	        // TODO Auto-generated method stub
	        super.onPreExecute();
	        progressDialog = new ProgressDialog(MainActivity.this);
	        progressDialog.setMessage("Fetching route, Please wait...");
	        progressDialog.setIndeterminate(true);
	        progressDialog.show();
	    }
	    @Override
	    protected String doInBackground(Void... params) {
	        JSONParser jParser = new JSONParser();
	        String json = jParser.getJSONFromUrl(url);
	        return json;
	    }
	    @Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);   
	        progressDialog.hide();        
	        if(result!=null){
	            drawPath(result);
	        }
	    }
	}
}
