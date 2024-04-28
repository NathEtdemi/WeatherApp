package adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;

import com.example.meteo.R;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.List;

import model.Signalement;

public class SignalementAdapter extends RecyclerView.Adapter<SignalementAdapter.ViewHolder> {

    private List<Signalement> signalementList;
    private Context context;
    private double userLatitude;
    private double userLongitude;

    public SignalementAdapter(Context context, List<Signalement> signalementList) {
        this.context = context;
        this.signalementList = signalementList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_signalement, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Signalement signalement = signalementList.get(position);
        holder.bind(signalement);
    }

    @Override
    public int getItemCount() {
        return signalementList.size();
    }

    public void setSignalements(List<Signalement> signalements) {
        this.signalementList = signalements;
        notifyDataSetChanged();
    }

    public void setUserLocation(double latitude, double longitude) {
        this.userLatitude = latitude;
        this.userLongitude = longitude;
        notifyDataSetChanged();
    }

    public List<Signalement> getSignalements() {
        return signalementList;
    }

    public BitmapDescriptor getResizedMarkerIconForSignalementType(int idWeatherType, int width) {
        Bitmap originalBitmap;
        switch (idWeatherType) {
            case 1:
                originalBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ciel_bleu);
                break;
            case 2:
                originalBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.nuages);
                break;
            case 3:
                originalBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.eclaircies);
                break;
            case 4:
                originalBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.couvert);
                break;
            case 5:
                originalBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.brouillard);
                break;
            case 6:
                originalBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.averse);
                break;
            case 7:
                originalBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pluie);
                break;
            case 8:
                originalBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.neige);
                break;
            default:
                originalBitmap = null; // Aucune icône par défaut
                break;
        }

        if (originalBitmap != null) {
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, width, width, false);
            return BitmapDescriptorFactory.fromBitmap(resizedBitmap);
        } else {
            return null; // Retourne null si aucune icône appropriée n'est trouvée
        }
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textWeatherType;
        private ImageView imageWeatherIcon;
        private TextView textTemperature;
        private TextView textDistance;
        private TextView textCreatedAt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageWeatherIcon = itemView.findViewById(R.id.image_weather_icon);
            textWeatherType = itemView.findViewById(R.id.text_weather_type);
            textWeatherType = itemView.findViewById(R.id.text_weather_type);
            textTemperature = itemView.findViewById(R.id.text_temperature);
            textDistance = itemView.findViewById(R.id.text_distance);
            textCreatedAt = itemView.findViewById(R.id.text_created_at);
        }

        public void bind(Signalement signalement) {
            int weatherIconResId = getWeatherIconResourceId(signalement.getWeatherType().getId().intValue());
            textWeatherType.setText(signalement.getWeatherType().getName());
            imageWeatherIcon.setImageResource(weatherIconResId);

            textTemperature.setText(signalement.getTemperature() + " °C|°F");
            double distance = signalement.getDistance();
            String distanceString;
            if (distance < 1.0) {
                distanceString = String.format("%.0f m", distance * 1000);
            } else {
                distanceString = String.format("%.2f km", distance);
            }
            textDistance.setText("Distance: " + distanceString);

            String timeAgo = getTimeAgo(signalement.getCreatedAt());
            textCreatedAt.setText(timeAgo);
        }

        private int getWeatherIconResourceId(int idWeatherType) {
            switch (idWeatherType) {
                case 1:
                    return R.drawable.ciel_bleu;
                case 2:
                    return R.drawable.nuages;
                case 3:
                    return R.drawable.eclaircies;
                case 4:
                    return R.drawable.couvert;
                case 5:
                    return R.drawable.brouillard;
                case 6:
                    return R.drawable.averse;
                case 7:
                    return R.drawable.pluie;
                case 8:
                    return R.drawable.neige;
                default:
                    return R.drawable.ic_launcher_foreground;
            }
        }

        private String getTimeAgo(Date createdAt) {
            long now = System.currentTimeMillis();
            long createdAtMillis = createdAt.getTime();
            long diff = now - createdAtMillis;
            int minutes = (int) (diff / (1000 * 60));
            int hours = minutes / 60;
            int remainingMinutes = minutes % 60;
            int days = hours / 24;

            if (minutes < 60) {
                return "il y a " + minutes + " minutes";
            } else if (hours < 24) {
                return "il y a " + hours + " heures et " + remainingMinutes + " minutes";
            } else {
                return "il y a " + days + " jours";
            }
        }
    }
}
