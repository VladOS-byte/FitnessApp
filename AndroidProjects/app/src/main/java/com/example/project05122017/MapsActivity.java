package com.example.project05122017;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;
import java.util.Date;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener{
    final static int PERMISSION_ALL = 1;
    final static String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    Marker marker;
    MarkerOptions mo;
    private GoogleMap mMap;
    LocationManager lm;
    Boolean onMap=true;
    Boolean start=false;
    PolylineOptions polylineOptions = new PolylineOptions();
    ImageButton btn;
    ImageButton btn0;
    Location dis0;
    double dis=0.00;
    TextView tv;
    Date date=new Date();
    Chronometer chronometer;
    long timeX;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        btn=(ImageButton)findViewById(R.id.btnstart);
        btn0=(ImageButton)findViewById(R.id.btnstop);
        tv=(TextView)findViewById(R.id.tv0);
        btn.setImageResource(R.drawable.start);
        btn0.setImageResource(R.drawable.stop);
        btn0.setClickable(false);
        btn0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chronometer.stop();
                timeX=date.getTime()-timeX;
                showAlert();
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn0.setClickable(true);
                chronometer=(Chronometer)findViewById(R.id.chron);
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
                timeX=date.getTime();
                start=true;
            }
        });

        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        mo=new MarkerOptions().position(new LatLng(0,0)).title("Я");
        if (Build.VERSION.SDK_INT>=23&&!isPermissionGranted()){
            requestPermissions(PERMISSIONS,PERMISSION_ALL);
        }
        else requestLocation();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        marker=mMap.addMarker(mo);
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng ll=new LatLng(location.getLatitude(), location.getLongitude());
        if (ll.latitude!=0||ll.longitude!=0){
            if (onMap){
                onMap=false;
                dis0=location;
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ignored) {}
            }
            marker.setPosition(ll);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll,15.0f));
            if (start)
                init(location);
        }
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
    private boolean isLocationEnabled(){
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER)|lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    private boolean isPermissionGranted(){
        return checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED |
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
    private void showAlert(){
        String message,title;
            message="Вы преодолели "+new DecimalFormat("#0.00").format(dis/1000)+"км";
            title="Пробежка окончена";


        final AlertDialog.Builder dialog =new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        dialog.setTitle(title).setMessage(message)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        dialog.show();
    }
    @SuppressLint("MissingPermission")
    private void requestLocation() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy((Criteria.ACCURACY_FINE));
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        String provider = lm.getBestProvider(criteria, true);
        lm.requestLocationUpdates(provider, 500, 5, this);
    }
    @SuppressLint("SetTextI18n")
    private void init(Location location){
        dis+=location.distanceTo(dis0);
        if(dis>=1000)
            tv.setText(new DecimalFormat("#0.00").format(dis/1000)+"км");
        else
            tv.setText(new DecimalFormat("#0.00").format(dis)+"м");
        polylineOptions.add(new LatLng(location.getLatitude(),location.getLongitude())).color(Color.MAGENTA).width(5);
        polylineOptions.geodesic(true);
        mMap.addPolyline(polylineOptions);
        dis0=location;
    }
}
