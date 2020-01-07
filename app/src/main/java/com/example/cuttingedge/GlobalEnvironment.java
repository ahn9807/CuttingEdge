package com.example.cuttingedge;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class GlobalEnvironment {
    public static UserData myUserData;
    public static ArrayList<String> myChatrooms;

    public static UserData GetMyUserData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("global environment", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonText = sharedPreferences.getString("userdata", null);
        return gson.fromJson(jsonText, UserData.class);
    }
    public static void SetUserData(Context context, UserData userData) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("global environment",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        editor.putString("userdata", gson.toJson(userData).toString());
        editor.commit();
    }
    public static ArrayList<String> GetChatrooms(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("global environment", Context.MODE_PRIVATE);
        String jsonText = sharedPreferences.getString("chatrooms", null);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<String>>(){}.getType();
        ArrayList<String> result = gson.fromJson(jsonText.toString(),type);

        return result;
    }
    public static void SetChatrooms(Context context, ArrayList<String> chatrooms) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("global environment",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<String>>(){}.getType();
        editor.putString("chatrooms", gson.toJson(chatrooms, type));
        editor.commit();
    }
}
