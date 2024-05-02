package com.example.meteo;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import adapter.WeatherTypeAdapter;
import api.ApiService;
import api.RetrofitClientInstance;
import model.Signalement;
import model.WeatherType;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import viewModel.LocationViewModel;

public class AddFragment extends Fragment {

    private ApiService apiService;
    private View view;
    private Spinner spinnerWeatherTypes;
    private EditText editTextTemperature;
    private LocationViewModel locationViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            apiService = RetrofitClientInstance.getRetrofitInstance().create(ApiService.class);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflating the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add, container, false);

        // Initialize LocationViewModel
        locationViewModel = new ViewModelProvider(requireActivity()).get(LocationViewModel.class);

        // Fetch weather types from API
        fetchWeatherTypes();

        // Setup submit button click listener
        Button submitButton = view.findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createSignalement();
            }
        });

        // Observe user location updates
        locationViewModel.getUserLocation().observe(getViewLifecycleOwner(), newLocation -> {
            // Once the location is updated, use it to populate latitude and longitude fields
            updateLocation(newLocation.latitude, newLocation.longitude);
        });

        return view;
    }

    // Method to fetch weather types from API
    private void fetchWeatherTypes() {
        Call<List<WeatherType>> call = apiService.getAllWeatherTypes();
        call.enqueue(new Callback<List<WeatherType>>() {
            @Override
            public void onResponse(Call<List<WeatherType>> call, Response<List<WeatherType>> response) {
                if (response.isSuccessful()) {
                    List<WeatherType> weatherTypes = response.body();
                    // Update UI with weather types
                    updateUI(weatherTypes);
                } else {
                    // Handle response error
                }
            }

            @Override
            public void onFailure(Call<List<WeatherType>> call, Throwable t) {
                // Handle API call failure
            }
        });
    }

    // Method to update UI with weather types
    private void updateUI(List<WeatherType> weatherTypes) {
        spinnerWeatherTypes = view.findViewById(R.id.spinnerWeatherType);
        WeatherTypeAdapter adapter = new WeatherTypeAdapter(requireContext(), weatherTypes);
        spinnerWeatherTypes.setAdapter(adapter);

        editTextTemperature = view.findViewById(R.id.editTextTemperature);
    }

    // Method to create a signalement
    private void createSignalement() {
        // Retrieve selected weather type
        WeatherType selectedWeatherType = (WeatherType) spinnerWeatherTypes.getSelectedItem();
        // Retrieve temperature
        double temperature = Double.parseDouble(editTextTemperature.getText().toString());
        // Retrieve latitude and longitude from the LocationViewModel
        double latitude = locationViewModel.getUserLocation().getValue().latitude;
        double longitude = locationViewModel.getUserLocation().getValue().longitude;

        // Log the signalement details
        Log.d("Signalement", "Weather Type: " + selectedWeatherType.getName());
        Log.d("Signalement", "Temperature: " + temperature);
        Log.d("Signalement", "Latitude: " + latitude);
        Log.d("Signalement", "Longitude: " + longitude);

        // Create Signalement object with retrieved data
        Signalement signalement = new Signalement();
        signalement.setWeatherType(selectedWeatherType);
        signalement.setLatitude(latitude);
        signalement.setLongitude(longitude);
        signalement.setTemperature(temperature);

        // Send a POST request to create the signalement
        Call<Signalement> call = apiService.createSignalement(signalement);
        call.enqueue(new Callback<Signalement>() {
            @Override
            public void onResponse(Call<Signalement> call, Response<Signalement> response) {
                if (response.isSuccessful()) {
                    showSuccessSnackbar();
                } else {
                    // Handle response error
                }
            }

            @Override
            public void onFailure(Call<Signalement> call, Throwable t) {
                // Handle API call failure
            }
        });
    }

    private void showSuccessSnackbar() {
        Snackbar.make(view, "Signalement créé avec succès", Snackbar.LENGTH_SHORT).show();
    }

    // Method to update location fields
    private void updateLocation(double latitude, double longitude) {
        // Update UI or perform any action with the received latitude and longitude
    }
}
