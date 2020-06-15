package com.example.techasians_appchat.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techasians_appchat.R;
import com.example.techasians_appchat.activity.ShowPhotoActivity;
import com.example.techasians_appchat.model.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
        final Message message = listMes.get(position);

        Date date = new Date(message.getTime());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        String formatTime = simpleDateFormat.format(date);
        holder.timeChat.setText(formatTime);

        if (message.getType().equals("text")) {
            holder.txtChat.setVisibility(View.VISIBLE);
            holder.imgChat.setVisibility(View.GONE);
            holder.txtChat.setText(message.getMessage().trim());
        } else {
            holder.txtChat.setVisibility(View.GONE);
            holder.imgChat.setVisibility(View.VISIBLE);
            Picasso.with(context).load(message.getMessage())
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(holder.imgChat);
            holder.imgChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ShowPhotoActivity.class);
                    intent.putExtra("image", message.getMessage());
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listMes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtChat;
        ImageView imgChat;
        TextView timeChat;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtChat = itemView.findViewById(R.id.txt_chat);
            imgChat = itemView.findViewById(R.id.img_chat);
            timeChat = itemView.findViewById(R.id.time_chat);
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