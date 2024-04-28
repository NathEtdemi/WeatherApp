package com.example.meteo;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import adapter.SignalementAdapter;
import api.ApiService;
import api.RetrofitClientInstance;
import model.Signalement;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import viewModel.LocationViewModel;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private SupportMapFragment mapFragment;
    private LocationViewModel locationViewModel;
    private SignalementAdapter signalementAdapter;
    private GoogleMap mMap;
    private LatLng userLocation = new LatLng(45.777222, 3.087025); // Default location

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize map fragment
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);

        // Check if map fragment is null to prevent duplicate instances
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction().replace(R.id.map_fragment, mapFragment).commit();
        }

        // Initialize signalementAdapter here
        signalementAdapter = new SignalementAdapter(requireContext(), new ArrayList<>());


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Load map asynchronously
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    // Callback method for when the map is ready to be used
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        locationViewModel = new ViewModelProvider(requireActivity()).get(LocationViewModel.class);
        try {
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            requireContext(), R.raw.map_style_json));

            if (!success) {
                Log.e("HomeFragment", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("HomeFragment", "Can't find style. Error: ", e);
        }

        mMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(false);

        locationViewModel.getUserLocation().observe(getViewLifecycleOwner(), newLocation -> {
            userLocation = new LatLng(newLocation.latitude, newLocation.longitude);
            mMap.clear(); // Clear previous markers
            mMap.addMarker(new MarkerOptions().position(userLocation).title("User's Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12));
            fetchAndDisplaySignalements(newLocation.latitude, newLocation.longitude);
        });
    }

    private void fetchAndDisplaySignalements(double latitude, double longitude) {
        Call<List<Signalement>> call = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            call = RetrofitClientInstance.getRetrofitInstance().create(ApiService.class).findReportsNearby(latitude, longitude, 10);
        }
        call.enqueue(new Callback<List<Signalement>>() {
            @Override
            public void onResponse(Call<List<Signalement>> call, Response<List<Signalement>> response) {
                if (response.isSuccessful()) {
                    List<Signalement> signalements = response.body();
                    if (signalements != null) {
                        for (Signalement signalement : signalements) {
                            LatLng location = new LatLng(signalement.getLatitude(), signalement.getLongitude());
                            BitmapDescriptor icon = signalementAdapter.getResizedMarkerIconForSignalementType(signalement.getWeatherType().getId().intValue(), 150);
                            mMap.addMarker(new MarkerOptions().position(location).icon(icon).title(signalement.getWeatherType().getName())).setTag(signalement);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Signalement>> call, Throwable t) {
                // Gérer l'échec de la récupération des signalements
            }
        });
    }
}
