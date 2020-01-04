package com.example.cuttingedge;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class AlgorithmData {
    public String id;
    public JSONArray member;
    public String departureDateFrom;
    public String departureDateTo;
    public String departureLocation;
    public String destinationLocation;

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("id",id);
            jsonObject.put("member", member);
            jsonObject.put("departureDateFrom", departureDateFrom);
            jsonObject.put("departureDateTo", departureDateTo);
            jsonObject.put("departureLocation", departureLocation);
            jsonObject.put("destinationLocation",destinationLocation);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
