package com.example.cuttingedge;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

public class RegisterActivity extends AppCompatActivity {

    EditText newId;
    EditText newPW;
    EditText newPW2;
    EditText newNickname;
    EditText u_mail;
    EditText code;
    Button getCode;
    Button registerConfirm;
    String idString, pwString=null;
    String pw2String="ff";
    String nicknameString, emailString, codeString;
    String answer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build());

        newId=(EditText) findViewById(R.id.newId);
        newPW=(EditText) findViewById(R.id.newPW);
        newPW2=(EditText) findViewById(R.id.newPW2);
        newNickname=(EditText) findViewById(R.id.newPW);
        u_mail=(EditText) findViewById(R.id.u_mail);


        getCode=(Button) findViewById(R.id.getCode);
        getCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pwString.equals(pw2String)) {
                    Toast.makeText(getApplicationContext(), "비밀번호 같아야함.", Toast.LENGTH_SHORT);
                }
//                else if(){
//
//                }
                else {
                    try {
                        GMailSender gMailSender = new GMailSender("ska98611@gmail.com", "skaska980611.");
                        //GMailSender.sendMail(제목, 본문내용, 받는사람);
//                        System.out.println("57");
//                    gMailSender.sendMail("메일메일", message.getText().toString(), textView.getText().toString());
                        answer = gMailSender.getEmailCode();
                        gMailSender.sendMail("끼룩끼룩", "verification code: " + answer, u_mail.getText().toString() + "@kaist.ac.kr");
//                        System.out.println("59");

                        Toast.makeText(getApplicationContext(), "이메일을 성공적으로 보냈습니다.", Toast.LENGTH_SHORT).show();




                    } catch (SendFailedException e) {
                        Toast.makeText(getApplicationContext(), "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                    } catch (MessagingException e) {
                        Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주십시오", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        registerConfirm=(Button) findViewById(R.id.registerConfirm);
        registerConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                code=(EditText) findViewById(R.id.code);
                codeString=code.getText().toString();

                idString=newId.getText().toString();
                pwString=newPW.getText().toString();
                pw2String=newPW2.getText().toString();
                nicknameString=newNickname.getText().toString();
                emailString=u_mail.getText().toString();

//                System.out.println("ㅎㅇㅎ"+codeString+ " "+answer);
//                if(answer.equals(codeString)){
                if(true){
//                    Toast.makeText(getApplicationContext(), "메일 인증 성공ㅎㅎ", Toast.LENGTH_SHORT);
                    System.out.println("ㅎㅇㅎ");

                    final UserData user=new UserData();
                    user.email=emailString;
                    user.id=idString;
                    user.password=pwString;
                    user.name=nicknameString;

                    Log.d("dd", user.toJSONObject().toString());

                    final NetworkManager networkManager = NetworkManager.getInstance();
                    networkManager.Connect(new NetworkListener() {
                        @Override
                        public void onSuccess(JSONObject jsonObject) {
                            System.out.println("114");
                            networkManager.Signup(getApplicationContext(), user, "local", new NetworkListener() {
                                @Override
                                public void onSuccess(JSONObject jsonObject) {
                                    Intent loginAgain=new Intent(getApplicationContext(), NoFacebookLoginActivity.class);
                                    startActivity(loginAgain);
                                }

                                @Override
                                public void onFailed(JSONObject jsonObject) {
                                    //Toast.makeText(getApplicationContext(), "DB failed", Toast.LENGTH_SHORT);
                                    System.out.println("fail");

                                }
                            });
                        }
                        @Override
                        public void onFailed(JSONObject jsonObject) {
                            ///Toast.makeText(getApplicationContext(), "Connection failed", Toast.LENGTH_SHORT);
                            System.out.println("connec fail");

                        }
                    });


                }
            }
        });


    }
}
