package com.example.cuttingedge;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class FirstScreenActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    List<MapInformation> goingList;
    List<MapInformation> comingList;
    List<Marker> goingMarkers=new ArrayList<>();
    List<Marker> comingMarkers=new ArrayList<>();
    CheckBox[] checkBoxes;

    String detailString=null; //백엔드 연결 후 keyvalue 혹은 배열로 바꿔야할듯
    Boolean direction=true; //학교에서 출발
    int icon=R.drawable.taxi_blue;
    int icon2=R.drawable.taxi_red;

    //    ArrayLi
    ArrayList[] markerArray;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_screen);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this); //꼭 메인쓰레드에서 선언되어야함

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

        LatLng SEOUL = new LatLng(36.372933, 127.359538);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(SEOUL)
                .title("KAIST")
                .snippet("택시")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.kaist));
        mMap.addMarker(markerOptions);

//        if(direction){
//            icon=R.drawable.taxi_blue;
//        }
//        else{
//            icon=R.drawable.taxi_red;
//        }

        goingList=new ArrayList<>();
        comingList=new ArrayList<>();

        MapInformation g1=new MapInformation("대전역",36.332568, 127.434329, detailString,icon );
        MapInformation g2=new MapInformation("복합터미널",36.351420, 127.437479, detailString,icon );
        MapInformation g3=new MapInformation("유성시외",36.355835, 127.334712, detailString, icon);
        goingList.add(g1);
        goingList.add(g2);
        goingList.add(g3);

        MapInformation c1=new MapInformation("대전역",36.332568, 127.434329, detailString,icon2 );
        MapInformation c2=new MapInformation("복합터미널",36.351420, 127.437479, detailString,icon2 );
        MapInformation c3=new MapInformation("유성시외",36.355835, 127.334712, detailString, icon2);
        comingList.add(c1);
        comingList.add(c2);
        comingList.add(c3);

        CheckBox checkGoing=(CheckBox) findViewById(R.id.checkGoingHome);
        CheckBox checkComing=(CheckBox) findViewById(R.id.checkComingSchool);
        checkBoxes=new CheckBox[]{checkGoing, checkComing};

        checkGoing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkEnoughAndMakeDisabled(checkBoxes);
                if(isChecked){
                    showGoing();
                }else {
                    hideGoing();
                }
            }
        });

        checkComing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkEnoughAndMakeDisabled(checkBoxes);
                if(isChecked){
                    showComing();
                }else {
                    hideComing();
                }
            }
        });

//        setMarker("대전역",36.332568, 127.434329, detailString,icon );
//        setMarker("복합터미널",36.351420, 127.437479, detailString,icon );
//        setMarker("유성시외",36.355835, 127.334712, detailString, icon );
//        setMarker("대전역",36.332568, 127.434329, detailString,icon2 );
//        setMarker("복합터미널",36.351420, 127.437479, detailString,icon2 );
//        setMarker("유성시외",36.355835, 127.334712, detailString, icon2 );


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
//                Toast.makeText(getApplicationContext(), marker.getTitle(),Toast.LENGTH_SHORT);
                System.out.println(marker.getTitle());

                return true;
            }
        });

//        mMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(36.359112, 127.401836), 11.8f));

    }

//    public void setMarker(String name, Double longitude, Double latitude, String detail, int icon){
//        MarkerOptions markerOptions = new MarkerOptions();
//
//        markerOptions.position(new LatLng(longitude, latitude))
//                .title(name)
//                .snippet(detail)
//                .icon(BitmapDescriptorFactory.fromResource(icon));
//        mMap.addMarker(markerOptions);
////        return markerOptions;
//    }

    public void showGoing(){
        goingMarkers.clear();
        for(MapInformation information: goingList){
            Marker marker=mMap.addMarker(new MarkerOptions()
            .position(new LatLng(information.longitude, information.latitude))
                    .title(information.name)
                    .snippet(information.details)
                    .icon(BitmapDescriptorFactory.fromResource(information.icon)));
            goingMarkers.add(marker);
        }
    }

    public void showComing(){
        comingMarkers.clear();
        for(MapInformation information: comingList){
            Marker marker=mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(information.longitude, information.latitude))
                    .title(information.name)
                    .snippet(information.details)
                    .icon(BitmapDescriptorFactory.fromResource(information.icon)));
            comingMarkers.add(marker);
        }
    }

    public void hideGoing(){
        for(Marker marker: goingMarkers){
            marker.remove();
        }
    }
    public void hideComing(){
        for(Marker marker: comingMarkers){
            marker.remove();
        }
    }

    private void checkEnoughAndMakeDisabled(CheckBox checkBoxes[]) {
        int countChecked = 0;
        for (CheckBox cb : checkBoxes) {
            cb.setEnabled(true);
            if (cb.isChecked()) countChecked++;
        }
        //your variable
        if (1 <= countChecked) {
            for (CheckBox cb : checkBoxes) {
                if (!cb.isChecked()) cb.setEnabled(false);
            }
        }
    }
}
