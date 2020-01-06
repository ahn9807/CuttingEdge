package com.example.cuttingedge;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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

    public static AlgorithmData fromJSONObject(JSONObject input) {
        Gson gson = new Gson();
        return gson.fromJson(input.toString(), AlgorithmData.class);
    }

//    public static String DateToString(int year, int month, int day, int hour, int min) {
////        Date from = new Date(year, month, day, hour, min);
//        return
////                SimpleDateFormat("yyyyMMddhhmm", Locale.KOREA).format(from);
//
//
//    }
//
//    public static Date StringToDate(String input) {
//        try{
//            return new SimpleDateFormat("yyyyMMddhhmm", Locale.KOREA).parse(input);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return new Date();
//    }
}
