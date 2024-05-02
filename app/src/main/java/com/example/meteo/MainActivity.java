package com.example.meteo;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;
import android.widget.RelativeLayout;
import android.Manifest;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import model.Signalement;
import viewModel.LocationViewModel;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    MeowBottomNavigation bottomNavigation;

    RelativeLayout main_layout;

    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        main_layout = findViewById(R.id.main_layout);

        bottomNavigation = findViewById(R.id.bottomNavigation);

        replace(new HomeFragment());

        bottomNavigation.show(2, true);

        bottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.baseline_add_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.baseline_home_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.baseline_view_list_24));

        meownavigation();
        main_layout.setBackgroundColor(Color.parseColor("#ADD8E6"));

        checkAndRequestPermissions();
        fetchLocation();
    }

    private void fetchLocation() {
        FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            LocationViewModel locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
                            locationViewModel.setUserLocation(location.getLatitude(), location.getLongitude());
                        } else {
                            Log.e("Location", "Location is null");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Location", "Failed to get location", e);
                    });
        } else {
            Log.e("Location", "Permission not granted");
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        checkAndRequestPermissions();
    }

    private void checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, "Cette permission est nécessaire pour localiser votre position sur la carte.", Toast.LENGTH_LONG).show();
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_ACCESS_FINE_LOCATION) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Toast.makeText(this, "Vous devez activer les permissions dans les paramètres de l'app.", Toast.LENGTH_LONG).show();
                    openAppSettings();
                }
            }
        }
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", getPackageName(), null));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void meownavigation(){

        bottomNavigation.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {

                switch (model.getId()){

                    case 1:

                        replace(new AddFragment());
                        main_layout.setBackgroundColor(Color.parseColor("#ADD8E6"));

                        break;

                    case 2:

                        replace(new HomeFragment());
                        main_layout.setBackgroundColor(Color.parseColor("#ADD8E6"));

                        break;

                    case 3:

                        replace(new ListeFragment());

                        break;

                }

                return null;
            }
        });
    }

    private void replace(Fragment fragment){

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.commit();

    }
}
