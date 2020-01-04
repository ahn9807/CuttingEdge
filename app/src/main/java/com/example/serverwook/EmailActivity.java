package com.example.serverwook;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

public class EmailActivity extends AppCompatActivity {
    private EditText textView = null;
    private EditText codeInput = null;
    private Button getButton = null;
    private Button verifyButton = null;
    private String answer=null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.email);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build());

        textView = (EditText) findViewById(R.id.u_mail); //받는 사람의 이메일
        codeInput = (EditText) findViewById(R.id.message); //본문 내용


        
        getButton = (Button) findViewById(R.id.getCode);
        getButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    GMailSender gMailSender = new GMailSender("ska98611@gmail.com", "skaska980611.");
                    //GMailSender.sendMail(제목, 본문내용, 받는사람);
                    System.out.println("57");
//                    gMailSender.sendMail("메일메일", message.getText().toString(), textView.getText().toString());
                    answer=gMailSender.getEmailCode();
                    gMailSender.sendMail("끼룩끼룩", "verification code: "+ answer, textView.getText().toString()+"@kaist.ac.kr");
                    System.out.println("59");

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
        });

        verifyButton=(Button) findViewById(R.id.verifyCode);
        verifyButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(codeInput.getText().toString().equals(answer)){
                    Toast.makeText(getApplicationContext(), "인증 성공 푸하하하하핫!!", Toast.LENGTH_SHORT).show();

                }

            }
        }
    }


}
