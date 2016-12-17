package homeseek.app.android.capstonehomeseek;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import android.location.Geocoder;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.config.GoogleDirectionConfiguration;
import com.akexorcist.googledirection.constant.RequestResult;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.constant.Unit;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PropertyLocationMap extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener{

    private GoogleMap mMap;
    String address;
    Map<LatLng, Marker> map = null;
    EditText etSearch;
    Button btnSearch;
    Switch mapType;
    String myLatitude, myLongitude;
    String property_id;
    double currentLat, currentLng;

    ArrayList<LatLng> MarkerPoints;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_location_map);

//        buildGoogleApiClient();

        GoogleDirectionConfiguration.getInstance().setLogEnabled(true);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //unfocus on edittexts when starting
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        etSearch = (EditText) findViewById(R.id.et_search);
        btnSearch = (Button) findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    onSearch();
                }catch (Exception ex){
                    ex.printStackTrace();
                }

            }
        });

        mapType = (Switch) findViewById(R.id.mapType);
        mapType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changeMapType(isChecked);
            }
        });

        try{
            //retrieved on the property details.php
            Intent intent = getIntent();
            property_id = intent.getStringExtra("property_id");
            address = intent.getStringExtra("address");

            buildGoogleApiClient();

            // Create the LocationRequest object
            mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                    .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        }catch (NullPointerException ex){
            ex.printStackTrace();
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("Device: ", "Location serviced connected.");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            runtime_permissions();
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        else {
            handleNewLocation(location);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("Device: ", "Location serviced suspended. Please Reconnect.");
    }

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("Device: ", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    private void handleNewLocation(Location location) {
        try{
            Log.d("Device: ", location.toString());
            double currentLatitude = location.getLatitude();
            double currentLongitude = location.getLongitude();
            final LatLng currentlatLng = new LatLng(currentLatitude, currentLongitude);

            Intent intent = getIntent();
            address = intent.getStringExtra("address");
            LatLng propertyLocation = getLocationFromAddress(this, address);
            final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
                    .findViewById(android.R.id.content)).getChildAt(0);

            property_id = intent.getStringExtra("property_id");
            address = intent.getStringExtra("address");

            //noinspection ResourceType
            mMap.setMyLocationEnabled(true);

            if(property_id != null){
//            final LatLng origin = new LatLng(Double.parseDouble(myLatitude), Double.parseDouble(myLongitude));
                final LatLng destination = getLocationFromAddress(this, address);
                Location locOrigin = new Location("Point A");
                locOrigin.setLatitude(currentLatitude);
                locOrigin.setLongitude(currentLongitude);
                Location locDestination = new Location("Point B");
                locDestination.setLatitude(destination.latitude);
                locDestination.setLongitude(destination.longitude);
                final double distance = Math.round(locOrigin.distanceTo(locDestination) / 1000);

                String serverKey = getResources().getString(R.string.google_maps_key);

                GoogleDirection.withServerKey(serverKey)
                        .from(currentlatLng)
                        .to(destination)
                        .transportMode(TransportMode.DRIVING)
                        .unit(Unit.METRIC)
                        .alternativeRoute(true)
                        .execute(new DirectionCallback() {
                            @Override
                            public void onDirectionSuccess(Direction direction, String rawBody) {
                                //Place current location marker

                                MarkerOptions markerOptionsOrigin = new MarkerOptions();
                                markerOptionsOrigin.position(currentlatLng);
                                markerOptionsOrigin.title("Current Position");
                                markerOptionsOrigin.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                                mMap.addMarker(markerOptionsOrigin);

                                //move map camera
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(currentlatLng));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(13));

                                //address
                                MarkerOptions markerOptionsDestination = new MarkerOptions();
                                markerOptionsDestination.position(destination);
                                markerOptionsDestination.title(address);
                                markerOptionsDestination.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                mMap.addMarker(markerOptionsDestination);


                                ArrayList<LatLng> directionPositionList = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
                                PolylineOptions polylineOptions = DirectionConverter.createPolyline(getApplicationContext(), directionPositionList, 5,
                                        Color.RED);
                                mMap.addPolyline(polylineOptions);

                                Snackbar bar = Snackbar.make(viewGroup, "Distance: " + String.valueOf(distance) + " kilometers", Snackbar.LENGTH_INDEFINITE)
                                        .setAction("Dismiss", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                // Handle user action
                                            }
                                        });
                                bar.show();
                            }

                            @Override
                            public void onDirectionFailure(Throwable t) {

                            }
                        });
            }
            else{
                try{
                    if(propertyLocation != null) {
                        Snackbar snackbar = Snackbar.make(viewGroup, propertyLocation.toString(), Snackbar.LENGTH_LONG);
                        snackbar.show();

                        mMap.addMarker(new MarkerOptions().position(propertyLocation).title(propertyLocation.toString()));
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(propertyLocation));
                        moveToCurrentLocation(propertyLocation);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Snackbar snackbar = Snackbar.make(viewGroup, address, Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        }catch (NullPointerException ex){
            ex.printStackTrace();
            Log.d("Error: ", ex.toString());
            Toast.makeText(this, "Server error, please try again later", Toast.LENGTH_SHORT).show();
        }

    }

//    private LatLng origin = new LatLng(7.0853758, 125.4864085);
//    private LatLng destination = new LatLng(7.10646953, 125.623257828);
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    public void changeMapType(boolean isChecked){
        if(isChecked){
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        }else{
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }

    public void onSearch(){
        String location = etSearch.getText().toString();
        List<Address> addressList = null;

        if(location != null ||  !location.trim().equals("")){
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }

            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

            try{
                mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                moveToCurrentLocation(latLng);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void moveToCurrentLocation(LatLng currentLocation)
    {
        if(currentLocation != null){
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
            // Zoom in, animating the camera.
            mMap.animateCamera(CameraUpdateFactory.zoomIn());
            // Zoom out to zoom level 10, animating with a duration of 2 seconds.
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
        }
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

            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return p1;
    }

    private boolean runtime_permissions() {
        if(Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=  PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    100);
            return true;
        }

        //else -  the api is lower 23
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
}

