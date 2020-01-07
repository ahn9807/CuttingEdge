package com.example.cuttingedge;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatroomAdapter extends RecyclerView.Adapter<ChatroomAdapter.ViewHolder> {
    private ArrayList<ChatData> mData = null;
    private String myId;

    public ChatroomAdapter(ArrayList<ChatData> list) {
        mData = list;
        if(GlobalEnvironment.GetMyUserDataByNative() != null && GlobalEnvironment.GetMyUserDataByNative().id != null) {
            myId = GlobalEnvironment.GetMyUserDataByNative().id;
        } else {
            myId = "Junho is really cute... ";
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView chatroommessage;
        TextView chatroomdate;
        ImageView chatroomimage;
        TextView chatroomnickname;

        ViewHolder(View itemView) {
            super(itemView);

            chatroommessage = itemView.findViewById(R.id.chatroommessage);
            chatroomdate = itemView.findViewById(R.id.chatroomdate);
            chatroomimage = itemView.findViewById(R.id.chatroomimage);
            chatroomnickname = itemView.findViewById(R.id.chatroomnickname);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(myId == null) {
            return 0;
        }
        if(mData.get(position).id == null) {
            return 0;
        }
        if(mData.get(position).id.equals(myId)) {
            return 1;
        }

        return 0;
    }

    @Override
    public ChatroomAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.chatroom_item_gray, parent, false);
        ViewHolder vh = new ChatroomAdapter.ViewHolder(view);
        if(viewType == 1) {
            view = inflater.inflate(R.layout.chatroom_item_green, parent, false);
            vh = new ChatroomAdapter.ViewHolder(view);
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(ChatroomAdapter.ViewHolder holder, int position) {
        holder.chatroommessage.setText(mData.get(position).message);
        holder.chatroomdate.setText(mData.get(position).date);
        holder.chatroomnickname.setText(mData.get(position).nickname);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}