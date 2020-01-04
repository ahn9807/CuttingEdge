package com.example.cuttingedge;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Arrays;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
public class MainActivity extends AppCompatActivity {
    CallbackManager callbackManager;
    LoginButton loginButton;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final NetworkManager networkManager = NetworkManager.getInstance();
        final UserData userData = new UserData();
        userData.password = "temp";
        userData.id = "temp";


        Intent login1=new Intent(getApplicationContext(), FacebookLoginActivity.class);
        startActivity(login1);



    }
}

