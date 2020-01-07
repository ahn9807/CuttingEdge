package com.example.cuttingedge;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JoinListAdapter extends RecyclerView.Adapter<JoinListAdapter.ViewHolder>{
    private RecycleViewClickListener listener;
    private ArrayList<JoinInformation> mData=null;
    private Context mContext;
    private Activity activity;



    public JoinListAdapter(ArrayList<JoinInformation> list, final Context context, final Activity activity){
        this.activity=activity;
        mContext=context;
        this.mData=list;
        listener=new RecycleViewClickListener() {
            @Override
            public void onClickButton(int position, View v) {
                final JoinInformation joinInformation = mData.get(position);

                NetworkManager.getInstance().JoinGroup(v.getContext(), new AlgorithmData(joinInformation.id), new NetworkListener() {
                    @Override
                    public void onSuccess(JSONObject jsonObject) {
//
                        NetworkManager.getInstance().GetGroupInformation(context, new AlgorithmData(joinInformation.id), new NetworkListener() {
                            @Override
                            public void onSuccess(JSONObject jsonObject) {
//                                Log.d("test_join",joinInformation.id);
//                                Log.d("test11", jsonObject.toString());

                            }

                            @Override
                            public void onFailed(JSONObject jsonObject) {

                            }
                        });
                    }

                    @Override
                    public void onFailed(JSONObject jsonObject) {

                    }
                });

            }
        };
    }

   ; //이거쓰는건가?


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView day;
        TextView startText ;
        TextView endText ;
        TextView peopleNumText;
        Button joinButton;
//        FirstScreenActivity firstScreenActivity=new FirstScreenActivity();

        ViewHolder(View itemView) {
            super(itemView) ;

            // 뷰 객체에 대한 참조. (hold strong reference)
            day=itemView.findViewById(R.id.joinDay);
            startText = itemView.findViewById(R.id.startTime) ;
            endText = itemView.findViewById(R.id.endTime) ;
            peopleNumText=itemView.findViewById(R.id.peopleNum);
            joinButton=itemView.findViewById(R.id.joinButton);

        }

        public void bind( final RecycleViewClickListener listener){

            joinButton.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(final View v) {
                    System.out.println("77");
                    if(v.getId()==joinButton.getId() && joinButton.isEnabled()){ //방에 들어감
                         final int position=getAdapterPosition();
                         activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                JoinInformation joinInformation = mData.get(position);
                                listener.onClickButton(position, v);
                                joinButton.setText("탑승 중");

                                joinButton.setEnabled(false);
                                joinButton.invalidate();
                                peopleNumText.setText(joinInformation.people.length()+1+"/4");
                                peopleNumText.invalidate();
//                                firstScreenActivity.partyRecycler.invalidate();
//                                firstScreenActivity.refresh();
                            }
                        });

                    }
                }
            });
         }
    }


    @NonNull
    @Override
    public JoinListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.join_list_item, parent, false);
        JoinListAdapter.ViewHolder viewHolder = new JoinListAdapter.ViewHolder(view);

        return viewHolder;
    }

    public void onBindViewHolder(JoinListAdapter.ViewHolder holder, int position) {
        JoinInformation joinInformation = mData.get(position) ;
        holder.day.setText(joinInformation.startTime.substring(4, 6)+"/"+joinInformation.startTime.substring(6,8));
        holder.startText.setText(joinInformation.startTime.substring(8,10)+":"+joinInformation.startTime.substring(10,12)) ;
        holder.endText.setText(joinInformation.endTime.substring(8,10)+":"+joinInformation.endTime.substring(10,12));
        holder.peopleNumText.setText(joinInformation.people.length()+"/4");

        GlobalEnvironment globalEnvironment=new GlobalEnvironment(); //버튼 설정

        holder.joinButton.setText("탑승");
        holder.joinButton.setEnabled(true);

    try {
        for (int i = 0; i < joinInformation.people.length(); i++) {
            System.out.println(joinInformation.people.getString(i)+" "+globalEnvironment.GetMyUserData(mContext).id);
            if (joinInformation.people.getString(i).equals(globalEnvironment.GetMyUserData(mContext).id)) { //이게 불러와지나? DB에 people이 들어갔는가?
                System.out.println("오잉");
                holder.joinButton.setText("탑승 중");
                holder.joinButton.setEnabled(false);
                break;
            }
        }

    }catch (JSONException e){

    }

    if(joinInformation.people.length()>=4) {
        holder.joinButton.setText("마감");
        holder.joinButton.setEnabled(false);
    }
//

        System.out.println("119");
        holder.bind(listener);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) holder.itemView.getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        //int deviceWidth = displayMetrics.widthPixels;  // 핸드폰의 가로 해상도를 구함.
        int deviceHeight = displayMetrics.heightPixels;  // 핸드폰의 세로 해상도를 구함.
        deviceHeight = deviceHeight / 9;
        //int deviceWidth = (int) (deviceHeight * 1.5);  // 세로의 길이를 가로의 길이의 1.5배로 하고 싶었다.
        holder.itemView.getLayoutParams().height = deviceHeight;  // 아이템 뷰의 세로 길이를 구한 길이로 변경
        holder.itemView.requestLayout(); // 변경 사항 적용

        //버튼 온클릭




        }

        // getItemCount() - 전체 데이터 갯수 리턴.
        @Override
        public int getItemCount() {
            return mData.size() ;
        }


    }





