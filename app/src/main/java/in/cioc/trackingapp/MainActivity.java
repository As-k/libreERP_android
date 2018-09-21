package in.cioc.trackingapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getName();
    private Button getLocBtn;
    LocationManager locationManager;
    double latitude, longitude;
    String str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isPermissionGranted();
        inti();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        getLoc();
//        startService(new Intent(this, BackgroundService.class));
        Intent intent = new Intent(BackgroundService.ACTION);
        sendBroadcast(intent);
    }

    void inti(){
        getLocBtn = findViewById(R.id.get_loc);
        getLocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLoc();
                Toast.makeText(MainActivity.this, "Latitude: "+latitude+"\nLongitude: "+longitude+"\n"+str, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public  boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.PROCESS_OUTGOING_CALLS) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {
                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.CALL_PHONE , Manifest.permission.READ_PHONE_STATE , Manifest.permission.PROCESS_OUTGOING_CALLS,
                        Manifest.permission.SEND_SMS}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int i = 1; i < 6; i++) {
            if (requestCode == i){
                if (grantResults.length > 0
                        && grantResults[i-1] == PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG, "Permission: " + permissions[i-1] + "was " + grantResults[i-1]);
                    //resume tasks needing this permission
                }
                return;
            }
        }
    }

    public void getLoc(){
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    //get Latitude
                    latitude = location.getLatitude();
                    //get Longitude
                    longitude = location.getLongitude();
//                    Toast.makeText(MainActivity.this, "Latitude: "+latitude+"\nLongitude: "+longitude, Toast.LENGTH_SHORT).show();
//                    LatLng latLng = new LatLng(latitude, longitude);
                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    try {
                        List<Address> addresses =  geocoder.getFromLocation(latitude, longitude, 1);
                        str = addresses.get(0).getLocality()+", ";
                        str += addresses.get(0).getCountryName();
//                        mMap.addMarker(new MarkerOptions().position(latLng).title(str));
//                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10.2f));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        }
        else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    //get Latitude
                    latitude = location.getLatitude();
                    //get Longitude
                    longitude = location.getLongitude();
//                    Toast.makeText(MainActivity.this, "Latitude: "+latitude+"\nLongitude: "+longitude, Toast.LENGTH_SHORT).show();
//                    LatLng latLng = new LatLng(latitude, longitude);
                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    try {
                        List<Address> addresses =  geocoder.getFromLocation(latitude, longitude, 1);
                        str = addresses.get(0).getLocality()+", ";
                        str += addresses.get(0).getCountryName();
//                        mMap.addMarker(new MarkerOptions().position(latLng).title(str));
//                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10.2f));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        startService(new Intent(this, BackgroundService.class));
    }
}
