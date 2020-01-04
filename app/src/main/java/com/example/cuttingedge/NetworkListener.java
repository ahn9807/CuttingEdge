package com.example.cuttingedge;

import org.json.JSONObject;

public interface NetworkListener {
    void onSuccess(JSONObject jsonObject);
    void onFailed(JSONObject jsonObject);
}