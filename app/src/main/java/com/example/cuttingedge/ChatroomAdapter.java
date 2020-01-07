package com.example.cuttingedge;

import android.content.Context;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.cuttingedge.ChatData;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ChatroomAdapter extends RecyclerView.Adapter<ChatroomAdapter.ViewHolder> {
    private ArrayList<ChatData> mData = null;

    public ChatroomAdapter(ArrayList<ChatData> list) {
        mData = list;
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
    public ChatroomAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.chatroom_item, parent, false);
        ChatroomAdapter.ViewHolder vh = new ChatroomAdapter.ViewHolder(view);

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