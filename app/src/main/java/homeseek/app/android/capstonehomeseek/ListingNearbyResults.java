package homeseek.app.android.capstonehomeseek;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import homeseek.app.android.capstonehomeseek.HttpRequests.GetAddressRequest;
import homeseek.app.android.capstonehomeseek.HttpRequests.SearchListingNearbyRequest;

public class ListingNearbyResults extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, SwipeRefreshLayout.OnRefreshListener{

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    String[] property_id, property_name, type, address, rate;
    double[] distance;
    boolean hasData;
    GPSTracker gpsTracker;
    double myLatitude, myLongitude;
    ListView data_listing;
    boolean justInstalled = false;
    private static final String TAG = ListingNearbyResults.class.getSimpleName();
    private SwipeRefreshLayout swipeRefreshLayout;
    ProgressDialog progressDialog;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_nearby_results);
        getSupportActionBar().setTitle("Listings Nearby");

        data_listing = (ListView) findViewById(R.id.ln_data_listing);

        //initialize swipe refresh layout
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLists);
        swipeRefreshLayout.setOnRefreshListener(this);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();

        //request permission on location
        runtime_permissions();
        CheckLocation();
    }

    @Override
    protected void onStart() {
        // connect googleapiclient
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        // disconnect googleapiclient
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //noinspection MissingPermission,ResourceType
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            myLatitude = mLastLocation.getLatitude();
            myLongitude = mLastLocation.getLongitude();
            // here we go you can see current lat long.
            if(!runtime_permissions()){
                ProcessLocation();
            }
            Log.e(TAG, "onConnected: " + String.valueOf(mLastLocation.getLatitude()) + ":" + String.valueOf(mLastLocation.getLongitude()));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void ProcessLocation(){
        gpsTracker = new GPSTracker(this);

        if(gpsTracker.getIsGPSTrackingEnabled()){
            if(gpsTracker.getLatitude() == 0 && gpsTracker.getLongitude() == 0){
                retrieveData(myLatitude, myLongitude);
            }else{
                //pag dili 0 ang gpstracker which means ni gana ang GPSTracker class
                retrieveData(gpsTracker.getLatitude(), gpsTracker.getLongitude());
            }

        }else{
            retrieveData(myLatitude, myLongitude);
        }
    }


    private boolean runtime_permissions() {
        if(Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=  PackageManager.PERMISSION_GRANTED
             && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    100);
            return true;
        }

        //else -  the api is lower 23
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                //granted
                justInstalled = true;
                ProcessLocation();
            }else{
                //ask permission again
                runtime_permissions();
            }
        }
    }

    private void retrieveData(final double mLat, final double mLng){
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                jsonDetails(response, mLat, mLng);
            }
        };

        SearchListingNearbyRequest searchListingNearbyRequest = new SearchListingNearbyRequest(responseListener);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(searchListingNearbyRequest);
    }


    private void jsonDetails(String response, final double mLat, final double mLng){
        final String JSONArray_name = "listing";

        JSONObject jsonResponse = null;
        try {
            jsonResponse = new JSONObject(response);
            final JSONArray arrayResponse = jsonResponse.getJSONArray(JSONArray_name);
            hasData = jsonResponse.getBoolean("hasData");

            property_id = new String[arrayResponse.length()];
            property_name = new String[arrayResponse.length()];
            type = new String[arrayResponse.length()];
            rate = new String[arrayResponse.length()];
            address = new String[arrayResponse.length()];
            distance = new double[arrayResponse.length()];

            if(hasData){
                Toast.makeText(getApplicationContext(), arrayResponse.length() + " properties found.", Toast.LENGTH_SHORT).show();
                for(int x = 0; x < arrayResponse.length(); x++) {
                    JSONObject jsonObject = arrayResponse.getJSONObject(x);
                    property_id[x] = jsonObject.getString("property_id");
                    property_name[x] = jsonObject.getString("property_name");
                    type[x] = jsonObject.getString("property_type");
                    rate[x] = jsonObject.getString("rate");
                    address[x] = jsonObject.getString("address");
                }

                ListingNearbyCustomAdapter listingNearbyCustomAdapter = new ListingNearbyCustomAdapter(this, property_id, property_name,
                        type,rate,address);
                data_listing.setAdapter(listingNearbyCustomAdapter);

                data_listing.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        view.setSelected(true);
                        String property_id = parent.getItemAtPosition(position).toString();
                        Intent intent = new Intent(view.getContext(), PropertyDetails.class);
                        intent.putExtra("property_id", property_id);
                        startActivity(intent);
                    }
                });

                data_listing.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {
                        view.setSelected(true);
                        view.setSelected(true);
                        final String property_id = parent.getItemAtPosition(position).toString();
                        final String myLat = String.valueOf(mLat);
                        final String myLng = String.valueOf(mLng);

                        final String[] items = new String[]{"Show Directions on Map", "View property"};
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.select_dialog_item, items);
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                        builder.setTitle("Choose Action:");
                        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    GetAddress(view, property_id, myLat, myLng);
                                } else if (which == 1) {
                                    //change image
                                    Intent intent = new Intent(view.getContext(), PropertyDetails.class);
                                    intent.putExtra("property_id", property_id);
                                    startActivity(intent);
                                }
                            }
                        });
                        final AlertDialog dialog = builder.create();
                        dialog.show();
                        return true;
                    }
                });

            }else{
                Toast.makeText(getApplicationContext(), "No data retrieved", Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            Log.d("Error: Response = ", response);
            e.printStackTrace();

        }catch (NullPointerException e){
            Log.d("Error: Response = ", response);
        }
        finally {
            //swipe refresh layout
//            progressDialog.dismiss();
            if(swipeRefreshLayout.isRefreshing()){
                swipeRefreshLayout.setRefreshing(false);
            }
        }


    }

    String property_address;

    private void GetAddress(final View view, final String property_id, final String myLat, final String myLng){
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    property_address = jsonResponse.getString("address");
                    Intent intent = new Intent(view.getContext(), PropertyLocationMap.class);
                    intent.putExtra("property_id", property_id);
                    intent.putExtra("address", property_address);
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        GetAddressRequest getAddressRequest = new GetAddressRequest(Integer.parseInt(property_id), responseListener);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(getAddressRequest);
    }

    private void CheckLocation(){
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_NEGATIVE:

                            break;

                        case DialogInterface.BUTTON_NEUTRAL:
                            Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(i);
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Location is not enabled, Go to settings?").setNeutralButton("Yes", dialogClickListener).setNegativeButton("Cancel", dialogClickListener).show();
        }
    }

    @Override
    public void onRefresh() {
        Toast.makeText(getApplicationContext(), "Listings reloaded.", Toast.LENGTH_LONG).show();
        //gets data from home fragment
        ProcessLocation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.listings_nearby_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.location_settings:
//                CheckLocation();
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    public LatLng getLocationFromAddress(Context context,String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return p1;
    }

}
