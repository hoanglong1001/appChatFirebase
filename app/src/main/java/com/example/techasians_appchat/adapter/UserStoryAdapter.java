package com.example.techasians_appchat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.techasians_appchat.R;
import com.example.techasians_appchat.callback.ItemClickListener;
import com.example.techasians_appchat.model.Post;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UserStoryAdapter extends RecyclerView.Adapter<UserStoryAdapter.RecyclerViewHolder> {

    Context context;
    ArrayList<Post> listPost;
    ItemClickListener itemClickListener;

    public UserStoryAdapter(Context context, ArrayList<Post> listPost, ItemClickListener itemClickListener) {
        this.context = context;
        this.listPost = listPost;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_user_story, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, final int position) {
        Post post = listPost.get(position);
        holder.txtName.setText(post.getName());
        Glide.with(context).load(post.getAvatar())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imgAvatar);

        Date date = new Date(post.getTimedate());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        String formatTime = simpleDateFormat.format(date);
        holder.txtTime.setText(formatTime);

        holder.txtStatus.setText(post.getStatus());
        Picasso.with(context).load(post.getPicture())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imgPicture);
        holder.num_favorite.setText(post.getNumberLike(post.getLikes()).size() + "");
        holder.ll_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.likePost(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listPost.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {

        RoundedImageView imgAvatar;
        TextView txtName;
        TextView txtTime;
        TextView txtStatus;
        TextView num_favorite;
        ImageView imgPicture;
        LinearLayout ll_like;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.img_user_story);
            txtName = itemView.findViewById(R.id.name_user_story);
            txtTime = itemView.findViewById(R.id.time_user_story);
            txtStatus = itemView.findViewById(R.id.stt_story);
            imgPicture = itemView.findViewById(R.id.img_story);
            num_favorite = itemView.findViewById(R.id.num_favorite);
            ll_like = itemView.findViewById(R.id.ll_like);
        }
    }
}

