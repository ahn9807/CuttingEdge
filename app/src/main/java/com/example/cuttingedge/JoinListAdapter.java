package com.example.cuttingedge;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class JoinListAdapter extends RecyclerView.Adapter<JoinListAdapter.ViewHolder>{
    private ArrayList<JoinInformation> mData=null;
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView startText ;
        TextView endText ;
        TextView peopleNumText;
        Button joinButton;

        ViewHolder(View itemView) {
            super(itemView) ;

            // 뷰 객체에 대한 참조. (hold strong reference)
            startText = itemView.findViewById(R.id.startTime) ;
            endText = itemView.findViewById(R.id.endTime) ;
            peopleNumText=itemView.findViewById(R.id.peopleNum);
            joinButton=itemView.findViewById(R.id.joinButton);
        }
    }
    JoinListAdapter(ArrayList<JoinInformation> list){
        mData=list;
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
        holder.startText.setText(joinInformation.startTime) ;
        holder.endText.setText(joinInformation.endTime);
        holder.peopleNumText.setText(joinInformation.peopleNum+"/4");


        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) holder.itemView.getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        //int deviceWidth = displayMetrics.widthPixels;  // 핸드폰의 가로 해상도를 구함.
        int deviceHeight = displayMetrics.heightPixels;  // 핸드폰의 세로 해상도를 구함.
        deviceHeight = deviceHeight / 7;
        //int deviceWidth = (int) (deviceHeight * 1.5);  // 세로의 길이를 가로의 길이의 1.5배로 하고 싶었다.
        holder.itemView.getLayoutParams().height = deviceHeight;  // 아이템 뷰의 세로 길이를 구한 길이로 변경
        holder.itemView.requestLayout(); // 변경 사항 적용


        }

        // getItemCount() - 전체 데이터 갯수 리턴.
        @Override
        public int getItemCount() {
            return mData.size() ;
        }
    }





