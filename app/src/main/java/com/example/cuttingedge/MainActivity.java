package com.example.cuttingedge;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;

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


        Intent login1=new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(login1);



    }
}

