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
import com.example.techasians_appchat.model.Message;
import com.example.techasians_appchat.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserChatAdapter extends RecyclerView.Adapter<UserChatAdapter.RecyclerViewHolder> {

    String lastMessage;

    Context context;
    ArrayList<User> listUser;

    public UserChatAdapter(Context context, ArrayList<User> listUser) {
        this.context = context;
        this.listUser = listUser;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_user_chat_message, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        final User user = listUser.get(position);
        holder.txtName.setText(user.getName());
        Glide.with(context).load(user.getAvatar())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imgAvatar);
        holder.setItemClick(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(context, RoomChatActivity.class);
                intent.putExtra("userId", user.getId());
                context.startActivity(intent);
            }

            @Override
            public void likePost(int position) {

            }
        });
        handleLastMessage(user.getId(), holder.txtChat);
    }

    @Override
    public int getItemCount() {
        return listUser.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ItemClickListener itemClick;
        ImageView imgAvatar;
        TextView txtName;
        TextView txtChat;
        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.img_user_chat);
            txtName = itemView.findViewById(R.id.name_user_chat);
            txtChat = itemView.findViewById(R.id.chat_user_chat);
            itemView.setOnClickListener(this);
        }

        public void setItemClick(ItemClickListener itemClick) {
            this.itemClick = itemClick;
        }

        @Override
        public void onClick(View view) {
            itemClick.onClick(view, getAdapterPosition());
        }
    }

    private void handleLastMessage(final String userId, final TextView mes) {
        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    if (message.getReceiver().equals(fuser.getUid()) && message.getSender().equals(userId) ||
                            message.getSender().equals(fuser.getUid()) && message.getReceiver().equals(userId)) {
                        if (message.getType().equals("text")) {
                            lastMessage = message.getMessage().trim();
                            lastMessage = lastMessage.replaceAll("\n", " ");
                            mes.setText(lastMessage);
                        } else {
                            mes.setText("send a photo");
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

