package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.meteo.R;

import java.util.List;

import model.WeatherType;

public class WeatherTypeAdapter extends ArrayAdapter<WeatherType> {

    private List<WeatherType> weatherTypes;
    private LayoutInflater inflater;

    public WeatherTypeAdapter(Context context, List<WeatherType> weatherTypes) {
        super(context, 0, weatherTypes);
        this.weatherTypes = weatherTypes;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.item_weather_type, parent, false);
        }

        WeatherType weatherType = weatherTypes.get(position);

        ImageView imageView = view.findViewById(R.id.imageViewWeatherIcon);
        TextView textView = view.findViewById(R.id.textViewWeatherType);

        imageView.setImageResource(getWeatherIconResourceId(weatherType.getId()));
        textView.setText(weatherType.getName());

        return view;
    }

    private int getWeatherIconResourceId(Long idWeatherType) {
        int id = idWeatherType.intValue();
        switch (id) {
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

}