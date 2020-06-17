package com.example.dzhohar.googlemaps;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Maps2Activity extends FragmentActivity implements OnMapReadyCallback , GoogleMap.OnMapLongClickListener{

    LocationManager locationManager;
    LocationListener locationListener;
    private GoogleMap mMap;
    SharedPreferences sharedPreferences = null;


    public void centerMapOnLocation(Location location, String title) {
        if (location != null) {
            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(userLocation).title(title));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                centerMapOnLocation(lastKnownLocation, "Your Location");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener(this);

        Intent intent = getIntent();
        if (intent.getIntExtra("placeNumber",0) == 0) {

            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),location.getLongitude())).title("Y"));
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            };

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                centerMapOnLocation(lastKnownLocation, "Your Location");
            } else {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
        } else {
            Location placeLocation = new Location(LocationManager.GPS_PROVIDER);
            placeLocation.setLatitude(OblibenaMistaActivity.locations.get(intent.getIntExtra("placeNumber",0)).latitude);
            placeLocation.setLongitude(OblibenaMistaActivity.locations.get(intent.getIntExtra("placeNumber",0)).longitude);

            centerMapOnLocation(placeLocation, OblibenaMistaActivity.places.get(intent.getIntExtra("placeNumber",0)));
        }
        mMap.setMyLocationEnabled(true);
    }


        @RequiresApi(api = Build.VERSION_CODES.N)
        public void onMapLongClick(LatLng latLng) {
            System.out.println("commited");
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

            String address = "";

            try {

                List<Address> listAddresses = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);

                if (listAddresses != null && listAddresses.size() > 0) {
                    if (listAddresses.get(0).getThoroughfare() != null) {
                        if (listAddresses.get(0).getSubThoroughfare() != null) {
                            address += listAddresses.get(0).getThoroughfare() + " ";
                        }

                        if (listAddresses.get(0).getLocality() != null) {
                            address += listAddresses.get(0).getLocality() + " ";
                        }

                        if (listAddresses.get(0).getPostalCode() != null) {
                            address += listAddresses.get(0).getPostalCode() + " ";
                        }

                        if (listAddresses.get(0).getAdminArea() != null) {
                            address += listAddresses.get(0).getAdminArea();
                        }


                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (address.equals("")) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm yyyy-MM-dd");
                address += sdf.format(new java.util.Date());
            }

            mMap.addMarker(new MarkerOptions().position(latLng).title(address));

            OblibenaMistaActivity.places.add(address);
            OblibenaMistaActivity.locations.add(latLng);

            OblibenaMistaActivity.arrayAdapter.notifyDataSetChanged();

            sharedPreferences = this.getSharedPreferences("com.example.dzhohar.googlemaps", Context.MODE_PRIVATE);

            try {

                ArrayList<String> latitudes = new ArrayList<>();
                ArrayList<String> longitudes = new ArrayList<>();

                for (LatLng coord : OblibenaMistaActivity.locations) {
                    latitudes.add(Double.toString(coord.latitude));
                    longitudes.add(Double.toString(coord.longitude));
                }

                sharedPreferences.edit().putString("places", ListSerializace.serialize(OblibenaMistaActivity.places)).apply();
                sharedPreferences.edit().putString("lats", ListSerializace.serialize(latitudes)).apply();
                sharedPreferences.edit().putString("lons", ListSerializace.serialize(longitudes)).apply();


            } catch (Exception e) {
                e.printStackTrace();
            }

            Toast.makeText(this,"Location Saved!",Toast.LENGTH_SHORT).show();
        }

    }


