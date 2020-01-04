package com.example.cuttingedge;

import org.json.JSONObject;

public class UserData {
    public String id;
    public String password;
    public String name;
    public String email;
    public String gender;
    public String phone;
    public String isAuth;
    public String fbToken;
    public String jsonWebToken;

    public JSONObject toJSONObject() {
        JSONObject returnJSON = new JSONObject();
        try{
            returnJSON.put("id",id);
            returnJSON.put("password",password);
            returnJSON.put("name",name);
            returnJSON.put("email", email);
            returnJSON.put("gender",gender);
            returnJSON.put("phone",phone);
            returnJSON.put("isAuth",isAuth);
            returnJSON.put("fbToken",fbToken);
            returnJSON.put("jsonWebToken", jsonWebToken);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return returnJSON;
    }
}
