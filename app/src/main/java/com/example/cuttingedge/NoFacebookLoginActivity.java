package com.example.cuttingedge;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.json.JSONObject;

public class NoFacebookLoginActivity extends Activity {


    String idString=null;
    String pwString=null;
    Button loginLocal;
    Button registerLocal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.no_facebook_login);
        final EditText idText=(EditText) findViewById(R.id.idText);
        final EditText pwText=(EditText) findViewById(R.id.pwText);


        loginLocal=findViewById(R.id.loginLocal);

        loginLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idString=idText.getText().toString();
                pwString=pwText.getText().toString();

                final UserData userData=new UserData();
                userData.password = pwString;
                userData.id = idString;

                Log.d("test", idString+" "+pwString+" "+userData.toJSONObject().toString());

                final NetworkManager networkManager = NetworkManager.getInstance();
                networkManager.Connect(new NetworkListener() {
                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        networkManager.Login(getApplicationContext(), userData, "local", new NetworkListener() {
                            @Override
                            public void onSuccess(JSONObject jsonObject) {
                                //로그인 성공
                                Log.d("test","asdfasdf");

                            }

                            @Override
                            public void onFailed(JSONObject jsonObject) {
                                //로그인 실패
                                Toast.makeText(getApplicationContext(), "로그인실패", Toast.LENGTH_SHORT);
                            }
                        });
                    }

                    @Override
                    public void onFailed(JSONObject jsonObject) {
                        //서버 연결 실패
                        Toast.makeText(getApplicationContext(), "서버연결실패", Toast.LENGTH_SHORT);
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
}
