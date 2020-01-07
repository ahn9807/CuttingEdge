package com.example.cuttingedge;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;

public class ChattingActivity extends AppCompatActivity {
    EditText editMessage;
    ScrollView scrollView;
    //채팅방 메세지 불러오는 곳
    ArrayList<ChatData> chatDatas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //디버그 데이터들
        ChatData c1 = new ChatData(); c1.date="12/31 2:31"; c1.nickname="junho"; c1.message="택시서비스 어디로 가실 생각이세요?";
        ChatData c2 = new ChatData(); c2.date="12/31 2:32"; c2.nickname="junyoung"; c2.message="아은동 태평소요.";
        ChatData c3 = new ChatData(); c3.date="12/31 2:32"; c3.nickname="junho"; c3.message="그럼 저는 n1건물 앞으로 갈께요.";
        ChatData c4 = new ChatData(); c4.date="12/31 2:45"; c4.nickname="junyoung"; c4.message="네 그럼 조금 있다 뵈요 ㅎㅎ";
        chatDatas.add(c1);
        chatDatas.add(c2);
        chatDatas.add(c3);
        chatDatas.add(c4);

        //변수 초기화
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        final UserData myUserData = GlobalEnvironment.GetMyUserData(this);

        RecyclerView recyclerView = findViewById(R.id.chatroomrecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final ChatroomAdapter adapter = new ChatroomAdapter(chatDatas);
        recyclerView.setAdapter(adapter);

        scrollView = findViewById(R.id.chatscroll);

        //채팅방의 스크롤이 항상 아래로 향하게
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

        NetworkManager.getInstance().JoinChatroom(this, "id", new NetworkListener() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                //처음 시작하면 모든 데이터 가져옴

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });

                NetworkManager.getInstance().FetchMessage(getApplicationContext(), "id", new NetworkListener() {
                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        Log.d("test_fetch",jsonObject.toString());
                        try{
                            JSONObject chatroom = jsonObject.getJSONObject("data");
                            Gson gson = new Gson();
                            Type type = new TypeToken<ArrayList<ChatData>>(){}.getType();
                            chatDatas = gson.fromJson(chatroom.getString("message"),type);
                            adapter.notifyDataSetChanged();
                            System.out.println("TEST1");
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("TEST2");
                        }
                    }

                    @Override
                    public void onFailed(JSONObject jsonObject) {
                        System.out.println("TEST3");
                        Log.d("test_fetch",jsonObject.toString());

                    }
                });
                Log.d("test_join_success",jsonObject.toString());
            }

            @Override
            public void onFailed(JSONObject jsonObject) {
                Log.d("test_join_fail",jsonObject.toString());

            }
        });

        //채팅방에 메세지 보내는 곳
        editMessage = this.findViewById(R.id.chatroomeditmessage);
        editMessage.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int keyCode, KeyEvent event) {
                //Enter key Action
                if (keyCode == EditorInfo.IME_ACTION_SEND) {
                    ChatData chatData = new ChatData();
                    chatData.nickname = myUserData.name;
                    chatData.message = editMessage.getText().toString();
                    chatData.date = new Date().toString();
                    chatDatas.add(chatData);
                    adapter.notifyItemInserted(chatDatas.size()-1);
                    NetworkManager.getInstance().EmitMessage(getApplicationContext(), "id", editMessage.getText().toString(), new NetworkListener() {
                        @Override
                        public void onSuccess(JSONObject jsonObject) {
                        }

                        @Override
                        public void onFailed(JSONObject jsonObject) {

                        }
                    });
                    editMessage.getText().clear();

                    return true;
                }
                return false;
            }
        });

    }
}
