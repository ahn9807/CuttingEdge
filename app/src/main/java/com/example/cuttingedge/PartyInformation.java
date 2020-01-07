package com.example.cuttingedge;

import org.json.JSONArray;

public class PartyInformation {
    String departure;
    String destination;
    String startTime;
    String endTime;
    JSONArray member;
    String id;

    public PartyInformation(String departure, String destination, String startTime, String endTime, JSONArray member, String id){
        this.departure=departure;
        this.destination=destination;
        this.startTime=startTime;
        this.endTime=endTime;
        this.member=member;
        this.id=id;
    }


}
