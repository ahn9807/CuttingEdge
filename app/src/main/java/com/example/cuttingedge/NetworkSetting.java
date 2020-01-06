package com.example.cuttingedge;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONObject;

public class NetworkSetting {
    private static final String SERVER_ADDRESS = "http://307875f6.ngrok.io";
    private static String token = "";

    public static String GetServerAddress() {
        return SERVER_ADDRESS;
    }
    public static String GetLoginMethod(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("network", Context.MODE_PRIVATE);
        return sharedPreferences.getString("loginMethod", "local");
    }
    public static void SetLoginMethod(Context context, String method) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("network",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("loginMethod", method);
        editor.commit();
    }
    public static String GetToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("network", Context.MODE_PRIVATE);
        return sharedPreferences.getString("token", "");
    }
    public static void SetToken(Context context, String token) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("network",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.commit();
    }
    public static JSONObject AttachTokenToJSONObject(Context context, JSONObject input) {
        JSONObject returnObject = input;
        try {
            returnObject =input.put("token", GetToken(context));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return returnObject;
    }
}
