package com.example.cuttingedge;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.borax12.materialdaterangepicker.time.RadialPickerLayout;
import com.borax12.materialdaterangepicker.time.TimePickerDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.zip.Inflater;

public class FirstScreenActivity extends AppCompatActivity implements OnMapReadyCallback, TimePickerDialog.OnTimeSetListener {
    private GoogleMap mMap;

    LinearLayout firstLayout;

    List<MapInformation> goingList;
    List<MapInformation> comingList;
    List<Marker> goingMarkers = new ArrayList<>();
    List<Marker> comingMarkers = new ArrayList<>();
    ArrayList<String> spinnerDepartureArray;
    ArrayAdapter<String> arrayAdapterDeparture;
    ArrayList<String> spinnerDestinationArray;
    ArrayAdapter<String> arrayAdapterDestination;
    Spinner spinnerDeparture;
    Spinner spinnerDestination;

    String startT=null;
    String endT=null;

    String date=null;
    String departureDateFrom;
    String departureDateTo;
    String departureLocation = "KAIST";
    String destinationLocation = "KAIST";
    AlgorithmData algorithmData;

    ArrayList<JoinInformation> joinArrayList;
    JoinListAdapter joinListAdapter;
    RecyclerView joinRecycler;
    TextView t1; //몇대


    CheckBox[] checkBoxes;
    Button selectDateRecycler;
//    ArrayList<JoinInformation> joinArrayList;

    String detailString = null; //백엔드 연결 후 keyvalue 혹은 배열로 바꿔야할듯
    Boolean direction = true; //학교에서 출발

    int icon = R.drawable.taxi_blue;
    int icon2 = R.drawable.taxi_red;

    EditText editDate;
    EditText editTime;

    String date_time = "";
    int mYear;
    int mMonth;
    int mDay;

    int mHour;
    int mMinute;

    //    ArrayLi
    ArrayList[] markerArray;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_screen);

        firstLayout=(LinearLayout) findViewById(R.id.firstLayout);
        firstLayout.setVisibility(View.INVISIBLE);

        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec ts1 = tabHost.newTabSpec("Tab Spec 1");
        ts1.setContent(R.id.content1);
        ts1.setIndicator("택시 팟 찾기");
        tabHost.addTab(ts1);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this); //꼭 메인쓰레드에서 선언되어야함

        final ImageButton addTaxiButton = findViewById(R.id.add_taxi);
        addTaxiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder addTaxiDialog = new AlertDialog.Builder(FirstScreenActivity.this);
                addTaxiDialog.setTitle("택시팟 만들기").setMessage("출발지, 목적지, 원하는 시간을 입력해주세요!");

                spinnerDepartureArray = new ArrayList<>(Arrays.asList("KAIST", "궁동이마트", "둔산동 갤러리아", "대전복합터미널", "대전역", "서대전역", "유성고속버스", "유성시외버스", "청사고속버스",
                        "청사시외버스", "남경욱집", "안준호집"
                ));
                spinnerDestinationArray = new ArrayList<>(Arrays.asList("KAIST", "궁동이마트", "둔산동 갤러리아", "대전복합터미널", "대전역", "서대전역", "유성고속버스", "유성시외버스", "청사고속버스",
                        "청사시외버스", "남경욱집", "안준호집"
                ));

//                Inflater inflater=new Inflater();
//                inflate()
//                View addView=inflater.inflate(R.layout.add_taxi_dialog, getView , false);
//                addTaxiDialog.setView(addView);
                LayoutInflater inflater = getLayoutInflater();
                View addView = inflater.inflate(R.layout.add_taxi_dialog, null);
                addTaxiDialog.setView(addView);
//                addTaxiDialog.setView(R.layout.add_taxi_dialog);


                arrayAdapterDeparture = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, spinnerDepartureArray);
                arrayAdapterDestination = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, spinnerDestinationArray);

                spinnerDeparture = (Spinner) addView.findViewById(R.id.spinner_departure);
                spinnerDestination = (Spinner) addView.findViewById(R.id.spinner_destination);

                spinnerDeparture.setAdapter(arrayAdapterDeparture);
                spinnerDestination.setAdapter(arrayAdapterDestination);

                spinnerDeparture.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (!destinationLocation.equals("KAIST")) {
                            Toast.makeText(getApplicationContext(), "출발지와 목적지 중 하나는 KAIST여야 합니다.", Toast.LENGTH_SHORT).show();
                        }
                        destinationLocation = "KAIST";
                        spinnerDestination.setSelection(0);
                        departureLocation = spinnerDepartureArray.get(position);
//                        spinnerDeparture.setSelection(position);
                        //목적지 카이스트로 설정
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                spinnerDestination.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        //목적지 카이스트로 설정
                        if (!departureLocation.equals("KAIST")) {
                            Toast.makeText(getApplicationContext(), "출발지와 목적지 중 하나는 KAIST여야 합니다.", Toast.LENGTH_SHORT).show();
                        }
                        departureLocation = "KAIST";
                        spinnerDeparture.setSelection(0);
                        destinationLocation = spinnerDestinationArray.get(position);
//                        spinnerDestination.setSelection(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                //시간골랑


                editDate = (EditText) addView.findViewById(R.id.editDate);
                editDate.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        datePicker();
//                        editDate.setText(date_time);
                    }
                });


//                pickTime=(Button) addView.findViewById(R.id.pickTime);
                editTime = (EditText) addView.findViewById(R.id.editTime);

                editTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { //안에 무슨말?

                        Calendar now = Calendar.getInstance();
                        TimePickerDialog tpd = TimePickerDialog.newInstance(
                                FirstScreenActivity.this,
                                now.get(Calendar.HOUR_OF_DAY),
                                now.get(Calendar.MINUTE),
                                false
                        );
                        tpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                Log.d("TimePicker", "Dialog was cancelled");
                            }
                        });
                        tpd.show(getFragmentManager(), "Timepickerdialog");

                    }
                });






                addTaxiDialog.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //글로벌변수 넣어서 코드 개드러움

                        AlgorithmData algorithmData=new AlgorithmData(date_time+startT, date_time+endT, destinationLocation, departureLocation);

                        if(date_time!=null && startT!=null && endT!=null && departureLocation!=null && destinationLocation!=null){
                            NetworkManager.getInstance().MakeNewGroup(getApplicationContext(), algorithmData, new NetworkListener() {
                                @Override
                                public void onSuccess(JSONObject jsonObject) {

                                }

                                @Override
                                public void onFailed(JSONObject jsonObject) {

                                }
                            });

                        }

                        date_time=null;
                        startT=null;
                        endT=null;
                        destinationLocation="KAIST";
                        departureLocation="KAIST";

                    }
                });
//
                addTaxiDialog.show();
            }
        });


        TabHost.TabSpec ts2 = tabHost.newTabSpec("Tab Spec 2");
        ts2.setContent(R.id.content2);
        ts2.setIndicator("내 택시 팟");
        tabHost.addTab(ts2);


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

        MapInformation g1 = new MapInformation("궁동이마트", 36.361251, 127.349924, detailString, icon);
        MapInformation g2 = new MapInformation("둔산동 갤러리아", 36.351716, 127.378197, detailString, icon);
        MapInformation g3 = new MapInformation("대전복합터미널", 36.351420, 127.437479, detailString, icon);
        MapInformation g4 = new MapInformation("대전역", 36.332568, 127.434329, detailString, icon);
        MapInformation g5 = new MapInformation("서대전역", 36.322603, 127.403912, detailString, icon);
        MapInformation g6 = new MapInformation("유성시외버스", 36.355835, 127.334712, detailString, icon);
        MapInformation g7 = new MapInformation("유성고속버스", 36.359934, 127.336256, detailString, icon);
        MapInformation g8 = new MapInformation("청사고속버스", 36.361159, 127.390393, detailString, icon);
        MapInformation g9 = new MapInformation("청사시외버스", 36.361703, 127.379675, detailString, icon);

        goingList=new ArrayList<>(Arrays.asList(g1,g2,g3,g4,g5,g6,g7,g8,g9));

        MapInformation c1 = new MapInformation("궁동이마트", 36.361251, 127.349924, detailString, icon2);
        MapInformation c2 = new MapInformation("둔산동 갤러리아", 36.351716, 127.378197, detailString, icon2);
        MapInformation c3 = new MapInformation("대전복합터미널", 36.351420, 127.437479, detailString, icon2);
        MapInformation c4 = new MapInformation("대전역", 36.332568, 127.434329, detailString, icon2);
        MapInformation c5 = new MapInformation("서대전역", 36.322603, 127.403912, detailString, icon2);
        MapInformation c6 = new MapInformation("유성시외버스", 36.355835, 127.334712, detailString, icon2);
        MapInformation c7 = new MapInformation("유성고속버스", 36.359934, 127.336256, detailString, icon2);
        MapInformation c8 = new MapInformation("청사고속버스", 36.361159, 127.390393, detailString, icon2);
        MapInformation c9 = new MapInformation("청사시외버스", 36.361703, 127.379675, detailString, icon2);

        comingList=new ArrayList<>(Arrays.asList(g1,g2,g3,g4,g5,g6,g7,g8,g9));


        final CheckBox checkGoing = (CheckBox) findViewById(R.id.checkGoingHome);
        final CheckBox checkComing = (CheckBox) findViewById(R.id.checkComingSchool);


        checkBoxes = new CheckBox[]{checkGoing, checkComing};

        showGoing();
        checkEnoughAndMakeDisabled(checkBoxes);

        checkGoing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkEnoughAndMakeDisabled(checkBoxes);
                if (isChecked) {
                    showGoing();
                } else {
                    hideGoing();
                }
            }
        });

        checkComing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkEnoughAndMakeDisabled(checkBoxes);
                if (isChecked) {
                    showComing();
                } else {
                    hideComing();
                }
            }
        });

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(final Marker marker) {
                selectDateRecycler = (Button) findViewById(R.id.selectDateRecycler);
                selectDateRecycler.setText("날짜 선택"); //초기화

                NetworkManager.getInstance().GetCurrentState(new NetworkListener() {
                    @Override
                    public void onSuccess(final JSONObject jsonObject) {
//                        Log.d("test", jsonObject.toString());
//                        Log.d("11", "succeed");
                        final JSONObject giveJson = jsonObject;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONArray objects = (JSONArray) jsonObject.get("data");
                                    t1 = (TextView) findViewById(R.id.detailText);

                                    RecyclerView joinList = findViewById(R.id.joinRecyclerView);
                                    joinArrayList = new ArrayList<>();
                                    //정보넣기


                                    for (int i = 0; i < objects.length(); i++) {

                                        JSONObject eachJSON = objects.getJSONObject(i);
// if 줄일 수 있을 것 같다.
                                        if (eachJSON.get("departureLocation").equals(marker.getTitle()) && eachJSON.get("destinationLocation").equals("KAIST") && checkComing.isChecked()) {
                                            System.out.println("true");
                                            JoinInformation j = new JoinInformation(eachJSON.getString("departureDateTo"),
                                                    eachJSON.getString("departureDateFrom"),
                                                    eachJSON.getJSONArray("member"),
                                                    eachJSON.getString("id")); //어느방인지
                                            joinArrayList.add(j);
                                        } else if (eachJSON.get("destinationLocation").equals(marker.getTitle()) && eachJSON.get("departureLocation").equals("KAIST") && checkGoing.isChecked()) {
                                            System.out.println("false");
                                            JoinInformation j = new JoinInformation(eachJSON.getString("departureDateTo"),
                                                    eachJSON.getString("departureDateFrom"),
                                                    eachJSON.getJSONArray("member"),
                                                    eachJSON.getString("id"));
                                            joinArrayList.add(j);

                                        }
                                    }

                                    joinRecycler = findViewById(R.id.joinRecyclerView);
//                                    tab3recyclerView.addItemDecoration(new DividerItemDecoration(tab3recyclerView.getContext(),1));
                                    joinRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//                                    JoinListAdapter joinListAdapter=new JoinListAdapter();
                                    joinListAdapter = new JoinListAdapter(joinArrayList, getApplicationContext());
                                    joinRecycler.setAdapter(joinListAdapter);

                                    if(!marker.getTitle().equals("KAIST"))
                                        if (checkComing.isChecked()) {
                                            t1.setText(marker.getTitle() + "-> KAIST: " + joinArrayList.size() + "대");
                                        } else {
                                            t1.setText("KAIST -> " + marker.getTitle() + ": " + joinArrayList.size() + "대");
                                        }
                                    else{
                                        t1.setText("KAIST");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                firstLayout.setVisibility(View.VISIBLE);

                                selectDateRecycler.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) { //글로벌변수 조심
                                        final Calendar ca = Calendar.getInstance();
                                        mYear = ca.get(Calendar.YEAR);
                                        mMonth = ca.get(Calendar.MONTH);
                                        mDay = ca.get(Calendar.DAY_OF_MONTH);

                                        DatePickerDialog datePickerDialog = new DatePickerDialog(FirstScreenActivity.this,
                                                new DatePickerDialog.OnDateSetListener() {

                                                    @Override
                                                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                                        String result = (monthOfYear + 1) + "/" + dayOfMonth;
                                                        selectDateRecycler.setText(result);

                                                        ArrayList<JoinInformation> dateSelectList=new ArrayList<>();
                                                        String dateString= String.format("%d%02d%02d",year,(monthOfYear + 1),dayOfMonth);
                                                        for(int i=0;i<joinArrayList.size();i++){
                                                            if(dateString.equals(joinArrayList.get(i).startTime.substring(0,8))) {
//                                                                joinRecycler.removeViewAt(i);
                                                                dateSelectList.add(joinArrayList.get(i));
                                                            }
                                                        }
                                                        joinRecycler.setAdapter(new JoinListAdapter(dateSelectList, getApplicationContext()));
                                                        joinRecycler.invalidate();

                                                        if(!marker.getTitle().equals("KAIST"))
                                                            if (checkComing.isChecked()) {
                                                                t1.setText(marker.getTitle() + "-> KAIST: " + dateSelectList.size() + "대");
                                                            } else {
                                                                t1.setText("KAIST -> " + marker.getTitle() + ": " + dateSelectList.size() + "대");
                                                            }

//                                                        joinListAdapter=new JoinListAdapter(joinArrayList);
//                                                        joinRecycler.setAdapter(joinListAdapter); //새로고침 되나?
                                                        //*************Call Time Picker Here ********************
//                        tiemPicker();
                                                    }
                                                }, mYear, mMonth, mDay);
                                        datePickerDialog.show();
                                    }

                                });

                            }
                        });
                    }

                    @Override
                    public void onFailed(JSONObject jsonObject) {
                        Log.d("11", "fail");
                    }
                });
                return null;
            }
        });


//        mMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(36.359112, 127.401836), 11.8f));

    }

    public void showGoing() {
        goingMarkers.clear();
        for (MapInformation information : goingList) {
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(information.longitude, information.latitude))
                    .title(information.name)
                    .snippet(information.details)
                    .icon(BitmapDescriptorFactory.fromResource(information.icon)));
            goingMarkers.add(marker);
        }
    }

    public void showComing() {
        comingMarkers.clear();
        for (MapInformation information : comingList) {
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(information.longitude, information.latitude))
                    .title(information.name)
                    .snippet(information.details)
                    .icon(BitmapDescriptorFactory.fromResource(information.icon)));
            comingMarkers.add(marker);
        }
    }

    public void hideGoing() {
        for (Marker marker : goingMarkers) {
            marker.remove();
        }
    }

    public void hideComing() {
        for (Marker marker : comingMarkers) {
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

    private void datePicker() {

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        date_time = String.format("%d%02d%02d",year,(monthOfYear + 1),dayOfMonth);
                        editDate.setText(monthOfYear+1+" / "+dayOfMonth);
                        //*************Call Time Picker Here ********************
//                        tiemPicker();
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
//        date=

    }


//    TimePickerDialog timePickerDialog=TimePickerDialog.newInstance(, )

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int hourOfDayEnd, int minuteEnd) {
        String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
        String minuteString = minute < 10 ? "0" + minute : "" + minute;
        String hourStringEnd = hourOfDayEnd < 10 ? "0" + hourOfDayEnd : "" + hourOfDayEnd;
        String minuteStringEnd = minuteEnd < 10 ? "0" + minuteEnd : "" + minuteEnd;
        String time = hourString + "시 " + minuteString + "분 ~ " + hourStringEnd + "시 " + minuteStringEnd + "분";



        startT=hourString+minuteString;
        endT=hourStringEnd+minuteStringEnd;
        editTime.setText(time);
    }

}






