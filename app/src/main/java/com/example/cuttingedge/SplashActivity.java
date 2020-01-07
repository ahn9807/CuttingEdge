package com.example.cuttingedge;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Network;
import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends Activity {

    UserData userData = new UserData();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(getApplicationContext(), "서버 연결에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }, 3000);

        final Context giveContext = getApplicationContext();

        NetworkManager.getInstance().CheckSession(this, userData, new NetworkListener() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                try{
                    userData = UserData.fromJSONObject(jsonObject.getJSONObject("data"));
                    GlobalEnvironment.SetUserData(giveContext, userData);
                    startActivity(new Intent(giveContext, LoginActivity.class));
                } catch (Exception e) {
                    e.printStackTrace();
                    startActivity(new Intent(giveContext, LoginActivity.class));
                }
            }

            @Override
            public void onFailed(JSONObject jsonObject) {
                NetworkManager.getInstance().Disconnect();
                startActivity(new Intent(giveContext, LoginActivity.class));
            }
        });
        //fb 해결해서 메인짜기전까지 이걸로함.
    }
}
