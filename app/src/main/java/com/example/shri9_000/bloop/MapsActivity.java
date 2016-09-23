package com.example.shri9_000.bloop;

import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.shri9_000.bloop.PermissionCheckUtil.BaseActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;


public class MapsActivity extends BaseActivity implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    Location user = null;
    Location distuser = null;
    float totaldistance = 0;
    ArrayList<Location> locset = new ArrayList<Location>();

    private static final PlaceLocation[] ALLPLACESLOCATION = new PlaceLocation[] {
            
            new PlaceLocation(new LatLng(12.934795, 77.612136), new String("Forum Mall")),
            
            new PlaceLocation(new LatLng(12.936437, 77.606021), new String("Christ College")),
            
            new PlaceLocation(new LatLng(12.895028, 77.599582), new String("IIM Bangalore")),

            new PlaceLocation(new LatLng(12.882462, 77.614297), new String("Random Location"))
            
    };

    static class PlaceLocation {

        public LatLng mLatLng;

        public String mId;

        PlaceLocation(LatLng latlng, String id) {

            mLatLng = latlng;

            mId = id;

        }

    }



    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 5000;
    private static int FATEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FATEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(DISPLACEMENT);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button b = (Button)findViewById(R.id.dist);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RelativeLayout r = (RelativeLayout) findViewById(R.id.rel);
                Snackbar snackbar = Snackbar
                        .make(r, "Distance Travelled : "+totaldistance+ " Meters", Snackbar.LENGTH_SHORT);

                snackbar.show();
            }
        });

    }

    @Override
    protected String[] getRequiredPermision() {

        return new String[]{
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
        };
    }


    @Override
    protected void onAllRequiredPermissionGranted() {
        super.onAllRequiredPermissionGranted();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FATEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    @Override
    public void onLocationChanged(Location location) {


        mMap.clear();
        mMap.addMarker(new MarkerOptions().title("My Location").position(new LatLng(location.getLatitude(),location.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.myloc)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()), 15));
        user = new Location("My Location");
        user.setLongitude(location.getLongitude());
        user.setLatitude(location.getLatitude());
        locset.add(user);
        distance(user);
        calcdist();


    }

    public void calcdist()
    {

        if(distuser == null) {
            distuser = user;
        }
        else if(locset.size() >= 2){

            int size = locset.size();
            size = size-2;
            distuser = locset.get(size);
        }

        if(distuser != user)
        {
            totaldistance+=distuser.distanceTo(user);

        }
    }

    public void distance(Location l)
    {
       for(int i=0; i<ALLPLACESLOCATION.length; i++) {

           mMap.addMarker(new MarkerOptions().title(ALLPLACESLOCATION[i].mId).position(ALLPLACESLOCATION[i].mLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.places)));
           mMap.setOnMarkerClickListener(this);
           Location rloc = new Location(ALLPLACESLOCATION[i].mId);
           rloc.setLatitude(ALLPLACESLOCATION[i].mLatLng.latitude);
           rloc.setLongitude(ALLPLACESLOCATION[i].mLatLng.longitude);
           if (l.distanceTo(rloc) < 100) {
               Toast.makeText(getApplicationContext(), "Location Nearby: "+ALLPLACESLOCATION[i].mId,
                       Toast.LENGTH_SHORT).show();


           }
       }
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (googleApiClient.isConnected())
            requestLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (googleApiClient.isConnected())
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
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


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        requestLocationUpdates();

    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        LatLng markloc = marker.getPosition();
        Location mark = new Location("M");
        mark.setLongitude(markloc.longitude);
        mark.setLatitude(markloc.latitude);

        float dist = mark.distanceTo(user);

                Toast.makeText(getApplicationContext(),"Distance is "+dist+" Meters",
                        Toast.LENGTH_SHORT).show();
        return true;
    }
}
