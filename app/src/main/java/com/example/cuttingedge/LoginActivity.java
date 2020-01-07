package com.example.cuttingedge;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {
    //그냥 로그인 관련 변수
    String idString=null;
    String pwString=null;
    Button loginLocal;
    Button registerLocal;
    CallbackManager callbackManager;
    LoginButton loginButton;
    Button loginButton2;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //페북 로그인
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        setContentView(R.layout.activity_login);
        Button creditButton=findViewById(R.id.credit);

        creditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Icon made by Pixel perfect from www.flaticon.com",Toast.LENGTH_LONG);
            }
        });


        loginButton = findViewById(R.id.login_button1);
        loginButton.setReadPermissions(Arrays.asList("public_profile"));
        callbackManager = CallbackManager.Factory.create();
        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(getApplicationContext(), "페북 성공", Toast.LENGTH_SHORT);
//                Intent email=new Intent(getApplicationContext(), EmailActivity.class);
//                startActivity(email);
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile"));
            }
        });

        loginButton2=findViewById(R.id.login_button2);
        loginButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent noFBIntent=new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(noFBIntent);
            }
        });



//        LoginManager.getInstance().registerCallback(callbackManager,
//                new FacebookCallback<LoginResult>() {
//                    @Override
//                    public void onSuccess(LoginResult loginResult) {
//                        // App code
//                    }
//
//                    @Override
//                    public void onCancel() {
//                        // App code
//                    }
//
//                    @Override
//                    public void onError(FacebookException exception) {
//                        // App code
//                    }
//                });

        final EditText idText=(EditText) findViewById(R.id.idText);
        final EditText pwText=(EditText) findViewById(R.id.pwText);
//        pwText.setInputType(InputType.TYPE_CLASS_TEXT | ;


        loginLocal=findViewById(R.id.loginLocal);

        loginLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idString=idText.getText().toString();
                pwString=pwText.getText().toString();

                final UserData userData=new UserData();
                userData.password = pwString;
                userData.id = idString;

                final NetworkManager networkManager = NetworkManager.getInstance();
                networkManager.Connect(new NetworkListener() {
                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        networkManager.Login(getApplicationContext(), userData, "local", new NetworkListener() {
                            @Override
                            public void onSuccess(JSONObject jsonObject) {
                                UserData myUserData;
                                try {
                                    myUserData =  UserData.fromJSONObject(jsonObject.getJSONObject("data"));
                                    GlobalEnvironment.SetUserData(getApplicationContext(), myUserData);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Intent first=new Intent(getApplicationContext(), FirstScreenActivity.class);
                                startActivity(first);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "로그인 되었습니다", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onFailed(JSONObject jsonObject) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "아이디/비밀번호를 다시확인해 주세요", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }

                    @Override
                    public void onFailed(JSONObject jsonObject) {
                        //서버 연결 실패
                        System.out.println("connect fail");
//                        Toast.makeText(getApplicationContext(), "서버연결실패", Toast.LENGTH_SHORT);
                    }
                });
//                Intent noFBIntent=new Intent(getApplicationContext(), NoFacebookLoginActivity.class);
//                startActivity(noFBIntent);
            }
        });

        registerLocal=findViewById(R.id.registerLocal);
        registerLocal.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent register=new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(register);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}