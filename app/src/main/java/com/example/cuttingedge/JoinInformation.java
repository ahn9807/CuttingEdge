package com.example.cuttingedge;

import android.widget.Button;

import org.json.JSONArray;

public class JoinInformation {
//    String day;
    String id;
    String startTime;
    String endTime;
    JSONArray people;
//    Button
    public JoinInformation(String startTime, String endTime, JSONArray people, String id){
        this.startTime=startTime;
        this.endTime=endTime;
        this.people=people;
        this.id=id;
    }

}
