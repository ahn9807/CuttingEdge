package com.example.cuttingedge;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class JoinListAdapter extends RecyclerView.Adapter<JoinListAdapter.ViewHolder>{
    private ArrayList<CodingItem> mData=null;
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView startText ;
        TextView end ;

        ViewHolder(View itemView) {
            super(itemView) ;

            // 뷰 객체에 대한 참조. (hold strong reference)
            dateText = itemView.findViewById(R.id.date) ;
            timeText = itemView.findViewById(R.id.time) ;

        }
    }

}
