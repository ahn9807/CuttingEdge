package com.example.cuttingedge;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;

public class PartyListAdapter extends RecyclerView.Adapter<PartyListAdapter.ViewHolder> {
    private RecycleViewClickListener listener;
    private ArrayList<PartyInformation> mData=null;
    private Context mContext;
    private Activity activity;

    public PartyListAdapter(ArrayList<PartyInformation> list, final Context context, final Activity activity) {
        this.activity=activity;
        mContext = context;
        this.mData = list;
        listener=new RecycleViewClickListener() {
            @Override
            public void onClickButton(final int position, final View v) {
                AlertDialog.Builder removeDialog = new AlertDialog.Builder(v.getContext());
                removeDialog.setTitle("나가기");
                removeDialog.setMessage("팟에서 나가시겠습니까?");

                removeDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final PartyInformation partyInformation = mData.get(position);

                        NetworkManager.getInstance().ExitGroup(v.getContext(), new AlgorithmData(partyInformation.id), new NetworkListener() {
                            @Override
                            public void onSuccess(JSONObject jsonObject) {
                                System.out.println("나간다");
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        removeAt(position);


                                    }
                                });

                            }

                            @Override
                            public void onFailed(JSONObject jsonObject) {

                            }
                        });
                    }
                });

                removeDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                removeDialog.show();

            }

        };

    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView partyLocation;
        TextView partyDay;
        TextView partyTime;
        TextView partyPeople;
        Button exitButton;

        ViewHolder(View itemView){
            super(itemView);
            partyLocation=itemView.findViewById(R.id.partyLocation);
            partyDay=itemView.findViewById(R.id.partyDay);
            partyTime=itemView.findViewById(R.id.partyTime);
            partyPeople=itemView.findViewById(R.id.partyPeople);
            exitButton=itemView.findViewById(R.id.exitButton);
        }

        public void bind(final RecycleViewClickListener listener){
            exitButton.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
//                    if(v.get)
                    if(v.getId()==exitButton.getId()){
                        int position=getAdapterPosition();
                        listener.onClickButton(position, v);

                    }
                }
            });
        }



    }

    @NonNull
    @Override
    public PartyListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.party_list_item, parent, false);
        PartyListAdapter.ViewHolder viewHolder = new PartyListAdapter.ViewHolder(view);

        return viewHolder;
    }

    public void onBindViewHolder(PartyListAdapter.ViewHolder holder, int position) {
        final PartyInformation partyInformation = mData.get(position);
        holder.partyLocation.setText(partyInformation.departure + " -> " + partyInformation.destination);

        holder.partyDay.setText(partyInformation.startTime.substring(4, 6) + " / " + partyInformation.startTime.substring(6, 8));
        holder.partyTime.setText(partyInformation.startTime.substring(8, 10) + ":" + partyInformation.startTime.substring(10, 12) + " ~ " +
                partyInformation.endTime.substring(8, 10) + ":" + partyInformation.endTime.substring(10, 12));
        holder.partyPeople.setText(partyInformation.member.length() + "/4");


        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) holder.itemView.getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        //int deviceWidth = displayMetrics.widthPixels;  // 핸드폰의 가로 해상도를 구함.
        int deviceHeight = displayMetrics.heightPixels;  // 핸드폰의 세로 해상도를 구함.
        deviceHeight = deviceHeight / 9;
        //int deviceWidth = (int) (deviceHeight * 1.5);  // 세로의 길이를 가로의 길이의 1.5배로 하고 싶었다.
        holder.itemView.getLayoutParams().height = deviceHeight;  // 아이템 뷰의 세로 길이를 구한 길이로 변경
        holder.itemView.requestLayout(); // 변경 사항 적용

        holder.bind(listener);

        holder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Context context=mContext;
                Intent intent=new Intent(context, ChattingActivity.class);
                //데이터 넣어야함
                intent.putExtra("id", partyInformation.id);
                intent.putExtra("location",partyInformation.departure + " -> " + partyInformation.destination );
                intent.putExtra("day",partyInformation.startTime.substring(4, 6) + " / " + partyInformation.startTime.substring(6, 8));
                intent.putExtra("time",partyInformation.startTime.substring(8, 10) + ":" + partyInformation.startTime.substring(10, 12) + " ~ " +
                        partyInformation.endTime.substring(8, 10) + ":" + partyInformation.endTime.substring(10, 12));
                intent.putExtra("people",partyInformation.member.length());


                context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });


    }

    @Override
    public int getItemCount() {
        return mData.size() ;
    }

    public void removeAt(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mData.size());
    }




//        holder.partyLocation.setText(joinInformation.);





}
