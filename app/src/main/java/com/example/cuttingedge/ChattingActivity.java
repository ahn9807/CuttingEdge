package com.example.cuttingedge;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.renderscript.RenderScript;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class ChattingActivity extends AppCompatActivity {
    EditText editMessage;
    ScrollView scrollView;
    //채팅방 메세지 불러오는 곳
    ArrayList<ChatData> chatDatas = new ArrayList<>();
    SimpleDateFormat format = new SimpleDateFormat("MM/dd hh:mm", Locale.KOREA);
    private LinearLayoutManager mManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //변수 초기화
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        final UserData myUserData = GlobalEnvironment.GetMyUserData(this);

        mManager = new LinearLayoutManager(this);

        final RecyclerView recyclerView = findViewById(R.id.chatroomrecycler);
        recyclerView.setLayoutManager(mManager);

        final ChatroomAdapter adapter = new ChatroomAdapter(chatDatas);
        recyclerView.setAdapter(adapter);

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeChanged(int positionStart, final int itemCount) {
                recyclerView.smoothScrollToPosition(chatDatas.size()-1);
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
                        try{
                            JSONObject chatroom = jsonObject.getJSONObject("data");
                            Gson gson = new Gson();
                            Type type = new TypeToken<ArrayList<ChatData>>(){}.getType();
                            chatDatas.addAll((ArrayList<ChatData>)gson.fromJson(chatroom.getString("message"),type));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                    recyclerView.smoothScrollToPosition(chatDatas.size()-1);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(JSONObject jsonObject) {

                    }
                });
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
                    chatData.date = format.format(new Date());
                    chatDatas.add(chatData);
                    recyclerView.smoothScrollToPosition(chatDatas.size()-1);
                    NetworkManager.getInstance().EmitMessage(getApplicationContext(), "id", chatData.message, new NetworkListener() {
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

        //채팅방 메세지 받는 곳
        TimerTask MessageTimer = new TimerTask() {
            @Override
            public void run() {
                NetworkManager.getInstance().NextMessage(getApplicationContext(), "id", chatDatas.size() -1, new NetworkListener() {
                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        try {
                            Log.d("test", jsonObject.toString());
                            JSONArray chatroom = jsonObject.getJSONArray("data");
                            Gson gson = new Gson();
                            Type type = new TypeToken<ArrayList<ChatData>>(){}.getType();
                            ArrayList<ChatData> receivedData = (ArrayList<ChatData>) gson.fromJson(chatroom.toString(), type);
                            if (receivedData != null) {
                                chatDatas.addAll(receivedData);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), String.valueOf(chatDatas.size()), Toast.LENGTH_SHORT);
                                        adapter.notifyDataSetChanged();
                                        recyclerView.smoothScrollToPosition(chatDatas.size()-1);
                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(JSONObject jsonObject) {

                    }
                });
            }
        };
        Timer timer = new Timer();
        timer.schedule(MessageTimer, 5000, 1000);
    }
}
