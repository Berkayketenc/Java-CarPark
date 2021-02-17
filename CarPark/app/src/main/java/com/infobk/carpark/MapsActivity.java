package com.infobk.carpark;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;


    LocationManager locationManager;
    LocationListener locationListener;
    public SQLiteDatabase database;

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Toast.makeText(getApplicationContext(),"Kaydetmek için uzun dokunun!",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);

        Intent intent = getIntent();
        String info = intent.getStringExtra("info");
        if(info.matches("name")){
            locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);

            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    System.out.println("location:" + location.toString());
                    System.out.println("calisti" );
                    SharedPreferences sharedPreferences = MapsActivity.this.getSharedPreferences("com.infobk.carpark",MODE_PRIVATE);
                    boolean trackBoolean = sharedPreferences.getBoolean("trackBoolean",false);

                    if(!trackBoolean) {
                        LatLng konum = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(konum, 15));
                        sharedPreferences.edit().putBoolean("trackBoolean",true).apply();
                    }


                }
            };


            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){

                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }else{

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

                Location sonkonum = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(sonkonum != null){

                    LatLng sonkullanicikonum = new LatLng(sonkonum.getLatitude(),sonkonum.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sonkullanicikonum,15));
                }
            }



        }else{


            Model model = (Model)intent.getSerializableExtra("model");
            LatLng latLng = new LatLng(model.latitude,model.longitude);
            String modelName = model.name;

            mMap.addMarker(new MarkerOptions().position(latLng).title(modelName));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));

        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length>0){

            if(requestCode==1){
                if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

                    Intent intent = getIntent();
                    String info = intent.getStringExtra("info");

                    if(info.matches("name")){
                        Location sonkonum = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if(sonkonum != null){

                            LatLng sonkullanicikonum = new LatLng(sonkonum.getLatitude(),sonkonum.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sonkullanicikonum,15));
                        }


                    }else{

                        mMap.clear();

                        Model model = (Model)intent.getSerializableExtra("model");
                        LatLng latLng = new LatLng(model.latitude,model.longitude);
                        String modelName = model.name;

                        mMap.addMarker(new MarkerOptions().position(latLng).title(modelName));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));



                    }
                }
            }
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        String adres="";

        try {
            List<Address> addressList=geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);

            if (addressList !=null && addressList.size()>0) {
                if (addressList.get(0).getThoroughfare() != null) {

                    adres += addressList.get(0).getThoroughfare();

                    if (addressList.get(0).getSubThoroughfare() != null) {
                        adres += "";
                        adres += addressList.get(0).getSubThoroughfare();
                    }


                }

               }else{
                adres = "New Place";
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        mMap.clear();
        mMap.addMarker(new MarkerOptions().title(adres).position(latLng));

        Double latitude = latLng.latitude;
        Double longitude = latLng.longitude;

        final Model model = new Model(adres,latitude,longitude);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapsActivity.this);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Kaydetmek İstediğine Emin misin ? ");
        alertDialog.setMessage(model.name);
        alertDialog.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                try {
                    database = MapsActivity.this.openOrCreateDatabase("Konumlar",MODE_PRIVATE,null);
                    database.execSQL("CREATE TABLE IF NOT EXISTS konumlar(id INTEGER PRIMARY KEY ,name VARCHAR , latitude VARCHAR,longitude VARCHAR)");

                    String toCompile = "INSERT INTO konumlar (name,latitude,longitude)VALUES(?,?,?)";

                    SQLiteStatement sqLiteStatement= database.compileStatement(toCompile);
                    sqLiteStatement.bindString(1, model.name);
                    sqLiteStatement.bindString(2,String.valueOf(model.latitude));
                    sqLiteStatement.bindString(3,String.valueOf(model.longitude));

                    sqLiteStatement.execute();

                    Toast.makeText(getApplicationContext(),"Kaydedildi",Toast.LENGTH_LONG).show();

                }catch (Exception e)
                {
                    e.printStackTrace();

                }




            }
        });
        alertDialog.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Toast.makeText(getApplicationContext(),"İptal Edildi",Toast.LENGTH_LONG).show();

            }


        });
        alertDialog.show();



    }
}