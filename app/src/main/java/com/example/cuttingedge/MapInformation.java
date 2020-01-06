package com.example.cuttingedge;

public class MapInformation {
    String name;
    Double longitude;
    Double latitude;
    String details;
    int icon;

    public MapInformation(String name, Double longitude, Double latitude, String details, int icon){
        this.name=name;
        this.details=details;
        this.icon=icon;
        this.latitude=latitude;
        this.longitude=longitude;
    }
}
