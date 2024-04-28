package api;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.LocalDateTime;

import adapter.LocalDateTimeAdapter;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientInstance {

    private static Retrofit retrofit;
    private static final String BASE_URL = "http://10.0.2.2:8080/";

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Retrofit getRetrofitInstance(){

        Log.d("tutu", "method getRetrofitInstance");

        Gson gson = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .create();
        }

        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }

        return retrofit;
    }

}