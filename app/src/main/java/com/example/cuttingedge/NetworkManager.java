package com.example.cuttingedge;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
                mSocket = IO.socket(NetworkSetting.GetServerAddress());
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

    public void Login(final Context context, UserData userData, String method, final NetworkListener callback) {
        if(isConnect() == true) {
            if(method == "local") {
                mSocket.emit("client_login", userData.toJSONObject());
                mSocket.on("server_result", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        JSONObject result;
                        result = (JSONObject)args[0];
                        try {
                            if(result.getString("type").equals("success")) {
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
            } else if(method == "facebook") {
                mSocket.emit("client_login_facebook", userData.toJSONObject());
                mSocket.on("server_result", new Emitter.Listener() {
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

    public void Logout(Context context) {
        if(isConnect() == true) {
            mSocket.emit("client_logout", NetworkSetting.GetToken(context));
        } else {

        }
        mSocket.disconnect();
    }

    public void Signup(final Context context, final UserData userData, String method, final NetworkListener callback) {
        NetworkSetting.SetLoginMethod(context, method);
        if(isConnect() == true) {
            if(method == "local")                {
                mSocket.emit("client_signup", userData.toJSONObject());
                mSocket.on("server_result", new Emitter.Listener() {
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
                mSocket.emit("client_signup_facebook", userData.toJSONObject());
                mSocket.on("server_result", new Emitter.Listener() {
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

    public void ChangeUserData(Context context, UserData userData, final NetworkListener callback) {
        if(isConnect() == true) {
            mSocket.emit("client_change_userdata", userData);
            mSocket.on("server_result", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    JSONObject result = (JSONObject)args[0];
                    try {
                        if(result.getString("type") == "success") {
                            callback.onSuccess(result);
                        } else {
                            callback.onFailed(result);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {

        }
    }

    //첫번째 화면
    public void GetCurrentState(final NetworkListener callback) {
        if(isConnect()) {
            mSocket.emit("client_get_algorithmdata");
            mSocket.on("server_put_algorithmdata", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    JSONObject jsonObject = (JSONObject)args[0];

                    try {
                        if(jsonObject.getString("server_result") == "success") {
                            callback.onSuccess(jsonObject);
                        } else {
                            callback.onFailed(jsonObject);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    //두번째 화면


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
}
