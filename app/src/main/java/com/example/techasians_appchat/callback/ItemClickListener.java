package com.example.techasians_appchat.callback;

import android.view.View;

public interface ItemClickListener {
    void onClick(View view, int position);

    void likePost(int position);
}
