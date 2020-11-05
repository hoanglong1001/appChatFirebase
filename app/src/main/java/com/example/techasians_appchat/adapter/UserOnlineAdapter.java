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

import com.bumptech.glide.Glide;
import com.example.techasians_appchat.R;
import com.example.techasians_appchat.activity.RoomChatActivity;
import com.example.techasians_appchat.callback.ItemClickListener;
import com.example.techasians_appchat.model.User;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class UserOnlineAdapter extends RecyclerView.Adapter<UserOnlineAdapter.ViewHolder> {

    Context context;
    ArrayList<User> listUser;

    public UserOnlineAdapter(Context context, ArrayList<User> listUser) {
        this.context = context;
        this.listUser = listUser;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_user_online_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user = listUser.get(position);
        holder.txtName.setText(user.getName());
        Glide.with(context).load(user.getAvatar())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imgAvatar);
        if (user.getState().equals("on")) {
            holder.imgOn.setVisibility(View.VISIBLE);
            holder.imgOff.setVisibility(View.GONE);
        } else {
            holder.imgOn.setVisibility(View.GONE);
            holder.imgOff.setVisibility(View.VISIBLE);
        }
        holder.imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, RoomChatActivity.class);
                intent.putExtra("userId", user.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ItemClickListener itemClick;
        ImageView imgAvatar;
        TextView txtName;
        RoundedImageView imgOn;
        RoundedImageView imgOff;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.img_user_online);
            txtName = itemView.findViewById(R.id.name_user_online);
            imgOn = itemView.findViewById(R.id.stt_on_user_online);
            imgOff = itemView.findViewById(R.id.stt_off_user_online);
        }
    }
}
