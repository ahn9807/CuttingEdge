package com.example.cuttingedge;

import android.widget.Button;

public class JoinInformation {
    String startTime;
    String endTime;
    int peopleNum;
//    Button
    public JoinInformation(String startTime, String endTime, int peopleNum){
        this.startTime=startTime;
        this.endTime=endTime;
        this.peopleNum=peopleNum;
    }

}
