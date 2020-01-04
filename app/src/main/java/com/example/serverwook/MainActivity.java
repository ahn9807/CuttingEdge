package com.example.serverwook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.Login;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

public class MainActivity extends AppCompatActivity {
//    = CallbackManager.Factory.create();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.INTERNET},
                        1);
            }
        }
//        setContentView(R.layout.activity_main);

//        if (AccessToken.getCurrentAccessToken() == null) {
//            Intent loginIntent = new Intent(MainActivity.this, FacebookLogin.class);
//            startActivity(loginIntent);
////일단 페이스북 접고
//        }
//


        Intent emailIntent = new Intent(getApplicationContext(), EmailActivity.class);
        startActivity(emailIntent);







    }

}

