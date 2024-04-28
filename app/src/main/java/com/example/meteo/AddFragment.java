package com.example.meteo;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

import api.ApiService;
import api.RetrofitClientInstance;
import model.WeatherType;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddFragment extends Fragment {

    private ApiService apiService;
    private View view;

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

        // Fetch weather types from API
        fetchWeatherTypes();

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
        Spinner spinnerWeatherTypes = view.findViewById(R.id.spinnerWeatherType);
        ArrayAdapter<WeatherType> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, weatherTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWeatherTypes.setAdapter(adapter);
    }
}