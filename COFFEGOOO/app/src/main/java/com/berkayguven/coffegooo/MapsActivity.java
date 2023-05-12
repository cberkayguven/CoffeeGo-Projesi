package com.berkayguven.coffegooo;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.berkayguven.coffegooo.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    ActivityResultLauncher<String> permissionLauncher;
    LocationManager locationManager;
    LocationListener locationListener;
    SharedPreferences sharedPreferences;
    LatLng userLocation;
    boolean info;
    private final double DISTANCE = 10.0;

    ArrayList<Place> places = new ArrayList<Place>();
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        places.add(new Place("EspressoLab", new LatLng(39.70705, 37.02732)));
        places.add(new Place("Noche", new LatLng(39.70696, 37.02680)));
        places.add(new Place("Çerkez'in Cafe & Restaurant",new LatLng(39.70820,37.02824)));
        places.add(new Place("Akropol Cafe & Bistro",new LatLng(39.70764,37.02797)));
        places.add(new Place("FERİŞTAH FASTFOOD",new LatLng(39.70502,37.02425)));
        places.add(new Place("Carmelo Coffee",new LatLng(39.70679,37.02375)));
        places.add(new Place("NEVA KAFE",new LatLng(39.70532,37.03014)));
        places.add(new Place("Albatros Restaurant",new LatLng(39.71113,37.03011)));
        places.add(new Place("Shapkamix Coffee Company",new LatLng(39.71198,37.02965)));
        places.add(new Place("Sedir Kafe",new LatLng(39.71125,37.02904)));
        places.add(new Place("Alaaddin Cafe",new LatLng(39.70979,37.03323)));
        places.add(new Place("Coffee and study",new LatLng(39.71446,37.04216)));
        places.add(new Place("Simit Sarayı",new LatLng(39.70565,37.02755)));
        places.add(new Place("Mariposa Station",new LatLng(39.70716,37.02705)));
        places.add(new Place("Pi love58",new LatLng(39.70811,37.03202)));
        places.add(new Place("Kepenek Pide Fastfood",new LatLng(39.71181,37.03069)));
        places.add(new Place("Neşve",new LatLng(39.71156,37.02987)));














        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        registerLauncher();

        sharedPreferences = this.getSharedPreferences("com.berkayguven.coffegooo",MODE_PRIVATE);
        info=false;
    }

    /**
     * Kullanılabilir olduğunda haritayı değiştirir.
     * Cihazda Google Play hizmetleri yüklü değilse ,kullanıcıdan yüklemesi istenecektir.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //döküm
        locationManager =(LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

                info = sharedPreferences.getBoolean("info", false);

                for (Place place : places){
                    addMarker(place);
                }


                if (!info) {
                    userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 16));
                    sharedPreferences.edit().putBoolean("info", true).apply();
                }
            }
            @Override public void onProviderEnabled(@NonNull String provider) {

            }

            @Override public void onProviderDisabled(@NonNull String provider) {

            }

            @Override public void onStatusChanged(String provider, int status, Bundle extras) {

            }


        };

        mMap.setOnMarkerClickListener(this);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                Snackbar.make(binding.getRoot(),"Haritalar için izin gerekli", Snackbar.LENGTH_INDEFINITE).setAction("izin ver", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Kullanıcı izni gerekli
                        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                    }
                }).show();
            } else {
                //Kullanıcıdan izin isteme
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);

            }
        } else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

            Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastLocation != null){
                userLocation = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,16));
            }

            mMap.setMyLocationEnabled(true);
        }
        //Sivas Cumhuriyet Üniversitesinin Enlem ve Boylam Bilgileri
        //39.705358, 37.022978
    }

    private void addMarker(Place place){
        mMap.addMarker(new MarkerOptions().title(place.name).position(place.location));
    }


    private void registerLauncher(){
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){
                    //İzin verilirse
                    if (ContextCompat.checkSelfPermission(MapsActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ){
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

                        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        if (location != null){
                            userLocation = new LatLng(location.getLatitude(),location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,16));
                        }
                    }


                } else{
                    //İzin reddedilirse
                    Toast.makeText(MapsActivity.this,"İzin Gerekli!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {

        double distance = SphericalUtil.computeDistanceBetween(userLocation,marker.getPosition());

        if (distance < DISTANCE){
            Toast.makeText(this, "10 Puan Kazandınız", Toast.LENGTH_LONG).show();
            puanEkle();
        }else{
            Toast.makeText(this, "Puan kazanmak için geçerli kafeye yakınlaşınız.", Toast.LENGTH_LONG).show();
        }


        return false;
    }

    private void puanEkle() {
        FirebaseDatabase.getInstance()
                .getReference().child("Users").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            User user = snapshot.getValue(User.class);


                            HashMap<String, Object> map = new HashMap<>();
                            if (user != null) {
                                map.put("ticket", user.getPuan() + 10);
                            }
                            FirebaseDatabase.getInstance()
                                    .getReference().child("Users").child(firebaseUser.getUid()).
                                    updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });


                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }

                });


    }}

