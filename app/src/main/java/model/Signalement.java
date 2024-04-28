package model;

import java.util.Date;

public class Signalement {
    private Long id;
    private WeatherType weatherType;
    private double latitude;
    private double longitude;
    private double temperature;
    private Date createdAt;
    private double distance; // Ajoutez une variable de distance

    public Signalement(Long id, WeatherType weatherType, double latitude, double longitude, double temperature, Date createdAt) {
        this.id = id;
        this.weatherType = weatherType;
        this.latitude = latitude;
        this.longitude = longitude;
        this.temperature = temperature;
        this.createdAt = createdAt;
    }

    public double distanceFromUser(double userLatitude, double userLongitude) {
        double earthRadius = 6371;
        double dLat = Math.toRadians(userLatitude - latitude);
        double dLon = Math.toRadians(userLongitude - longitude);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(userLatitude)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        distance = earthRadius * c; // Mettez à jour la distance
        return distance;
    }

    public Signalement() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WeatherType getWeatherType() {
        return weatherType;
    }

    public void setWeatherType(WeatherType weatherType) {
        this.weatherType = weatherType;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    // Ajoutez une méthode pour obtenir la distance
    public double getDistance() {
        return distance;
    }

    // Ajoutez une méthode pour définir la distance
    public void setDistance(double distance) {
        this.distance = distance;
    }
}
