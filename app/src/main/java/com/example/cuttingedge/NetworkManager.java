package com.example.cuttingedge;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.ArrayList;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class NetworkManager {
    private static NetworkManager singleton;
    private Socket mSocket;
    private static boolean isConnected = false;
    public static NetworkManager getInstance() {
        if(singleton == null) {
            singleton = new NetworkManager();
        }

        return singleton;
    }

    //로그인 관련 함수들
    public boolean Connect(final NetworkListener callback) {
        final JSONObject jsonObject = new JSONObject();
        try {jsonObject.put("type", "success");}
        catch (Exception e) {e.printStackTrace();}
        if(mSocket == null || mSocket.connected() == false) {
            try {
                if(mSocket == null) {
                    mSocket = IO.socket(NetworkSetting.GetServerAddress());
                }
                mSocket.connect();
                mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        callback.onSuccess(jsonObject);
                    }
                });
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                callback.onFailed(jsonObject);
                return false;
            }
        } else {
            callback.onSuccess(jsonObject);
            return true;
        }
    }

    public boolean isConnect() {
        if(mSocket == null || (mSocket.connected() == false)) {
            return false;
        }

        return true;
    }

    public boolean Disconnect() {
        if(mSocket == null || mSocket.connected() == false)
            return true;
        try{
            mSocket.emit("client_disconnect");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public void CheckSession(final Context context, final UserData userData, final NetworkListener callback) {
        Connect(new NetworkListener() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                if(isConnect()) {
                    mSocket.emit("client_check_session", NetworkSetting.AttachTokenToJSONObject(context, userData.toJSONObject()));
                    mSocket.on("server_result_check_session", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            AttachCallback(args[0], callback);
                            mSocket.off("server_result_check_session");
                        }
                    });
                }
            }

            @Override
            public void onFailed(JSONObject jsonObject) {

            }
        });
    }

    public void Login(final Context context, final UserData userData, final String method, final NetworkListener callback) {
        Connect(new NetworkListener() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                if(isConnect() == true) {
                    if(method == "local") {
                        mSocket.emit("client_login", userData.toJSONObject());
                        mSocket.on("server_result_login", new Emitter.Listener() {
                            @Override
                            public void call(Object... args) {
                                JSONObject result;
                                result = (JSONObject)args[0];
                                try {
                                    if(result.getString("type").equals("success")) {
                                        NetworkSetting.SetToken(context, result.getString("token"));
                                        callback.onSuccess(result);
                                        mSocket.off("server_result_login");
                                    } else {
                                        callback.onFailed(result);
                                        mSocket.off("server_result_login");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else if(method == "facebook") {
                        mSocket.emit("client_login_facebook", userData.toJSONObject());
                        mSocket.on("server_result_login", new Emitter.Listener() {
                            @Override
                            public void call(Object... args) {
                                JSONObject result = new JSONObject();
                                try {
                                    if(result.getString("type") == "success") {
                                        NetworkSetting.SetToken(context, result.getString("token"));
                                        callback.onSuccess(result);
                                    } else {
                                        callback.onFailed(result);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } else {

                }
            }

            @Override
            public void onFailed(JSONObject jsonObject) {

            }
        });
    }

    public void Logout(Context context) {
        if(isConnect() == true) {
            mSocket.emit("client_logout", NetworkSetting.GetToken(context));
        } else {

        }
        mSocket.disconnect();
    }

    //우선 로컬에서만 작동한다.
    public void CheckDupulicate(final Context context, final UserData userData, final String method, final NetworkListener callback) {
        Connect(new NetworkListener() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                NetworkSetting.SetLoginMethod(context, method);
                if(isConnect()) {
                    mSocket.emit("client_check_duplicate", userData.toJSONObject());
                    mSocket.on("server_result_check_duplicate", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            AttachCallback(args[0], callback);
                        }
                    });
                }
            }

            @Override
            public void onFailed(JSONObject jsonObject) {

            }
        });
    }

    public void Signup(final Context context, final UserData userData,final String method, final NetworkListener callback) {
        Connect(new NetworkListener() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                NetworkSetting.SetLoginMethod(context, method);
                if(isConnect() == true) {
                    if(method == "local")                {
                        mSocket.emit("client_signup", userData.toJSONObject());
                        mSocket.on("server_result_signup", new Emitter.Listener() {
                            @Override
                            public void call(Object... args) {
                                JSONObject result = new JSONObject();
                                result = (JSONObject)args[0];
                                try {
                                    if(result.getString("type").equals("success")) {
                                        NetworkSetting.SetToken(context, result.getString("token"));
                                        callback.onSuccess(result);
                                        return;
                                    } else {
                                        callback.onFailed(result);
                                        return;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else {
                        mSocket.emit("client_login_facebook", userData.toJSONObject());
                        mSocket.on("server_result_login_facebook", new Emitter.Listener() {
                            @Override
                            public void call(Object... args) {
                                JSONObject result = new JSONObject();
                                result = (JSONObject)args[0];
                                try {
                                    if(result.getString("type") == "success") {
                                        NetworkSetting.SetToken(context, result.getString("token"));
                                        callback.onSuccess(result);
                                        return;
                                    } else {
                                        callback.onFailed(result);
                                        return;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } else {

                }
            }

            @Override
            public void onFailed(JSONObject jsonObject) {

            }
        });
    }

    public void ChangeUserData(final Context context, final UserData userData, final NetworkListener callback) {
        Connect(new NetworkListener() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                if(isConnect() == true) {
                    mSocket.emit("client_change_userdata", NetworkSetting.AttachTokenToJSONObject(context, userData.toJSONObject()));
                    mSocket.on("server_result_change_userdata", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            AttachCallback(args[0], callback);
                        }
                    });
                } else {

                }
            }

            @Override
            public void onFailed(JSONObject jsonObject) {

            }
        });
    }

    //첫번째 화면
    public void GetCurrentState(final NetworkListener callback) {
        Connect(new NetworkListener() {
            @Override
            public void onSuccess(JSONObject jsonObject) {

                if(isConnect()) {
                    mSocket.emit("client_get_algorithmdata");
                    mSocket.on("server_result_get_algorithmdata", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            AttachCallback(args[0], callback);
                        }
                    });
                }
            }

            @Override
            public void onFailed(JSONObject jsonObject) {

            }
        });
    }

    //두번째 화면
    public void MakeNewGroup(final Context context, final AlgorithmData group, final NetworkListener callback) {
        Connect(new NetworkListener() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                if(isConnect()) {
                    mSocket.emit("client_new_group", NetworkSetting.AttachTokenToJSONObject(context, group.toJSONObject()));
                    mSocket.on("server_result_new_group", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            AttachCallback(args[0], callback);
                        }
                    });
                }
            }

            @Override
            public void onFailed(JSONObject jsonObject) {

            }
        });
    }

    public void JoinGroup(final Context context, final AlgorithmData group, final NetworkListener callback) {
        Connect(new NetworkListener() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                if(isConnect()) {
                    mSocket.emit("client_join_group", NetworkSetting.AttachTokenToJSONObject(context, group.toJSONObject()));
                    mSocket.on("server_result_join_group", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            AttachCallback(args[0], callback);
                        }
                    });
                }
            }

            @Override
            public void onFailed(JSONObject jsonObject) {

            }
        });
    }

    //세번째 탭
    public void GetGroupInformation(final Context context, final AlgorithmData group, final NetworkListener callback) {
        Connect(new NetworkListener() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                if(isConnect()) {
                    mSocket.emit("client_get_groupinformation", NetworkSetting.AttachTokenToJSONObject(context, group.toJSONObject()));
                    mSocket.on("server_result_get_groupinformation", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            AttachCallback(args[0], callback);
                        }
                    });
                }
            }

            @Override
            public void onFailed(JSONObject jsonObject) {

            }
        });
    }

    public void ExitGroup(final Context context, final AlgorithmData group, final NetworkListener callback) {
        Connect(new NetworkListener() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                if(isConnect()) {
                    mSocket.emit("client_exit_group", NetworkSetting.AttachTokenToJSONObject(context, group.toJSONObject()));
                    mSocket.on("server_result_exit_group", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            AttachCallback(args[0], callback);
                        }
                    });
                }
            }

            @Override
            public void onFailed(JSONObject jsonObject) {

            }
        });
    }

    public void JoinChatroom(final Context context, final String algorithmId, final NetworkListener callback) {
        Connect(new NetworkListener() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                if (isConnect()) {
                    JSONObject inputJSON = new JSONObject();
                    try {
                        inputJSON.put("id", algorithmId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mSocket.emit("client_join_chatroom", NetworkSetting.AttachTokenToJSONObject(context, inputJSON));
                    mSocket.on("server_result_join_chatroom", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            AttachCallback(args[0], callback);
                        }
                    });
                }
            }

            @Override
            public void onFailed(JSONObject jsonObject) {

            }
        });
    }

    public void ExitChatroom(final Context context, final String id, final NetworkListener callback) {
        Connect(new NetworkListener() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                if (isConnect()) {
                    JSONObject inputJSON = new JSONObject();
                    try {
                        inputJSON.put("id", id);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mSocket.emit("client_exit_chatroom", NetworkSetting.AttachTokenToJSONObject(context, inputJSON));
                    mSocket.on("server_result_exit_chatroom", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            AttachCallback(args[0], callback);
                        }
                    });
                }
            }

            @Override
            public void onFailed(JSONObject jsonObject) {

            }
        });
    }

    public void GetChatroom(final Context context, final NetworkListener callback) {
        Connect(new NetworkListener() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                if (isConnect()) {
                    mSocket.emit("client_get_chatroom", NetworkSetting.AttachTokenToJSONObject(context, new JSONObject()));
                    mSocket.on("server_result_get_chatroom", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            AttachCallback(args[0], callback);
                        }
                    });
                }
            }

            @Override
            public void onFailed(JSONObject jsonObject) {

            }
        });
    }

    public void EmitMessage(final Context context, final String id, final String message, final NetworkListener callback) {
        Connect(new NetworkListener() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                if (isConnect()) {
                    JSONObject inputJSON = new JSONObject();
                    try {
                        inputJSON.put("message", message);
                        inputJSON.put("id",id);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mSocket.emit("client_emit_message", NetworkSetting.AttachTokenToJSONObject(context, inputJSON));
                    mSocket.on("server_result_emit_message", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            AttachCallback(args[0], callback);
                        }
                    });
                }
            }

            @Override
            public void onFailed(JSONObject jsonObject) {

            }
        });
    }

    public void FetchMessage(final Context context, final String algorithmId, final NetworkListener callback) {
        Log.d("test_fetch","start fetching data");
        Connect(new NetworkListener() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                if (isConnect()) {
                    JSONObject inputJSON = new JSONObject();
                    try {
                        inputJSON.put("id", algorithmId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mSocket.emit("client_fetch_message", NetworkSetting.AttachTokenToJSONObject(context, inputJSON));
                    mSocket.on("server_result_fetch_message", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            AttachCallback(args[0], callback);
                        }
                    });
                }
            }

            @Override
            public void onFailed(JSONObject jsonObject) {

            }
        });
    }

    public void NextMessage(final Context context, final String algorithmId, final int index, final NetworkListener callback) {
        Connect(new NetworkListener() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                if (isConnect()) {
                    JSONObject inputJSON = new JSONObject();
                    try {
                        inputJSON.put("id", algorithmId);
                        inputJSON.put("index",index);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mSocket.emit("client_next_message", NetworkSetting.AttachTokenToJSONObject(context, inputJSON));
                    mSocket.on("server_result_next_message", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            AttachCallback(args[0], callback);
                            mSocket.off("server_result_next_message");
                        }
                    });
                }
            }

            @Override
            public void onFailed(JSONObject jsonObject) {

            }
        });
    }

    public static ArrayList<AlgorithmData> JSONObjectToArrayListAlgorithmData(JSONObject input) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<AlgorithmData>>(){}.getType();
        ArrayList<AlgorithmData> result = gson.fromJson(input.toString(),type);

        return result;
    }

    public static String BitmapToString(Bitmap bitmapPicture) {
        String encodedImage;
        bitmapPicture = Bitmap.createScaledBitmap(bitmapPicture, 300, 400, true);
        ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
        bitmapPicture.compress(Bitmap.CompressFormat.PNG, 100, byteArrayBitmapStream);
        byte[] b = byteArrayBitmapStream.toByteArray();
        encodedImage = Base64.encodeToString(b,Base64.DEFAULT);
        encodedImage = encodedImage.replace(System.getProperty("line.separator"), "");

        return encodedImage;
    }

    public static Bitmap StringToBitmap(String bitmapString) {
        byte[] decodedString = Base64.decode(bitmapString, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        return decodedByte;
    }

    private void AttachCallback(Object args, NetworkListener callback) {
        JSONObject jsonObject = (JSONObject)args;
        try {
            if(jsonObject.getString("type").equals("success")) {
                callback.onSuccess(jsonObject);
            } else {
                callback.onFailed(jsonObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
