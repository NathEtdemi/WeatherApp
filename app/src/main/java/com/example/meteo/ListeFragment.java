package com.example.meteo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.slider.Slider;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import adapter.SignalementAdapter;
import api.ApiService;
import api.RetrofitClientInstance;
import model.Signalement;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import viewModel.LocationViewModel;

public class ListeFragment extends Fragment {

    private LocationViewModel locationViewModel;
    double latitude;
    double longitude;
    double radius = 10;
    private RecyclerView recyclerView;
    private SignalementAdapter signalementAdapter;

    private Slider sliderDistance;
    private Button buttonSort;
    private boolean isAscendingOrder = true;

    public ListeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationViewModel = new ViewModelProvider(requireActivity()).get(LocationViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_liste, container, false);

        Log.d("tutu", "onCreateView called");

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        signalementAdapter = new SignalementAdapter(requireContext(), new ArrayList<>());
        recyclerView.setAdapter(signalementAdapter);

        sliderDistance = view.findViewById(R.id.sliderDistance);
        buttonSort = view.findViewById(R.id.buttonSort);

        buttonSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAscendingOrder = !isAscendingOrder;
                fetchSignalements(latitude, longitude);
            }
        });
        sliderDistance.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {
                // Ne rien faire ici
            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                radius = slider.getValue();
                Log.d("testradius", "radius" + radius);
                fetchSignalements(latitude, longitude);
            }
        });

        locationViewModel.getUserLocation().observe(getViewLifecycleOwner(), newLocation -> {
            latitude = newLocation.latitude;
            longitude = newLocation.longitude;
            Log.d("tutu", "Nouvelle localisation reçue: " + latitude + " longitude : " + longitude);
            signalementAdapter.setUserLocation(latitude, longitude);
            fetchSignalements(latitude, longitude);
        });

        return view;
    }

    private void fetchSignalements(double latitude, double longitude) {
        // Fetching signalements...
        Call<List<Signalement>> call = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            call = RetrofitClientInstance.getRetrofitInstance().create(ApiService.class).findReportsNearby(latitude, longitude, radius);
        }
        call.enqueue(new Callback<List<Signalement>>() {
            @Override
            public void onResponse(Call<List<Signalement>> call, Response<List<Signalement>> response) {
                if (response.isSuccessful()) {
                    List<Signalement> signalements = response.body();
                    if (signalements != null) {
                        for (Signalement signalement : signalements) {
                            double distance = signalement.distanceFromUser(latitude, longitude);
                            signalement.setDistance(distance);
                        }
                        // Tri des signalements selon l'ordre de tri actuel
                        if (isAscendingOrder) {
                            signalements.sort(new Comparator<Signalement>() {
                                @Override
                                public int compare(Signalement o1, Signalement o2) {
                                    return Double.compare(o1.getDistance(), o2.getDistance());
                                }
                            });
                        } else {
                            signalements.sort(new Comparator<Signalement>() {
                                @Override
                                public int compare(Signalement o1, Signalement o2) {
                                    return Double.compare(o2.getDistance(), o1.getDistance());
                                }
                            });
                        }
                        signalementAdapter.setSignalements(signalements);
                        signalementAdapter.notifyDataSetChanged();
                    } else {
                        // Traitement de l'erreur
                    }
                } else {
                    Toast.makeText(getContext(), "Échec de la récupération des données", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(Call<List<Signalement>> call, Throwable t) {
                Toast.makeText(getContext(), "Erreur de connexion", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
