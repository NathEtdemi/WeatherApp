package api;

import model.Signalement;
import model.WeatherType;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.util.List;

public interface ApiService {
    @GET("/api/signalement/a-proximite")
    Call<List<Signalement>> findReportsNearby(@Query("userLat") double userLat,
                                              @Query("userLon") double userLon,
                                              @Query("radius") double radius);

    @GET("/api/weather-types")
    Call<List<WeatherType>> getAllWeatherTypes();

    @POST("/api/signalement")
    Call<Signalement> createSignalement(@Body Signalement signalement);
}