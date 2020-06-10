package com.example.techasians_appchat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techasians_appchat.R;
import com.example.techasians_appchat.model.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RoomChatAdapter extends RecyclerView.Adapter<RoomChatAdapter.ViewHolder> {

    public static final int MY_CHAT = 1;
    public static final int YOUR_CHAT = 0;
    FirebaseUser fuser;

    Context context;
    ArrayList<Message> listMes;

    public RoomChatAdapter(Context context, ArrayList<Message> listMes) {
        this.context = context;
        this.listMes = listMes;
    }

    @NonNull
    @Override
    public RoomChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MY_CHAT) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.item_my_message, parent, false);
            return new RoomChatAdapter.ViewHolder(view);
        } else {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.item_your_message, parent, false);
            return new RoomChatAdapter.ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RoomChatAdapter.ViewHolder holder, int position) {
        Message message = listMes.get(position);
        if (message.getType().equals("text")) {
            holder.txtChat.setVisibility(View.VISIBLE);
            holder.imgChat.setVisibility(View.GONE);
            holder.txtChat.setText(message.getMessage());
        } else {
            holder.txtChat.setVisibility(View.GONE);
            holder.imgChat.setVisibility(View.VISIBLE);
            Picasso.with(context).load(message.getMessage())
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(holder.imgChat);
        }
    }

    @Override
    public int getItemCount() {
        return listMes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtChat;
        ImageView imgChat;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtChat = itemView.findViewById(R.id.txt_chat);
            imgChat = itemView.findViewById(R.id.img_chat);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (listMes.get(position).getSender().equals(fuser.getUid())) {
            return MY_CHAT;
        } else {
            return YOUR_CHAT;
        }
    }
}