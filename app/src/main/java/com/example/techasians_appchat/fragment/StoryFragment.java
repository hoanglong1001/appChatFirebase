package com.example.techasians_appchat.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techasians_appchat.R;
import com.example.techasians_appchat.adapter.UserStoryAdapter;
import com.example.techasians_appchat.callback.ItemClickListener;
import com.example.techasians_appchat.model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoryFragment extends Fragment implements ItemClickListener {

    FirebaseUser fuser;
    DatabaseReference reference;
    private RecyclerView recUserStory;
    private UserStoryAdapter userStoryAdapter;
    private ArrayList<Post> listPost;
    private ArrayList<Post> listSearch;
    private EditText searchStory;
    private TextView txtNumsOfLike;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_story, container, false);

        txtNumsOfLike = view.findViewById(R.id.num_favorite);
        recUserStory = view.findViewById(R.id.rec_user_story);
        searchStory = view.findViewById(R.id.search_story);
        showPost();

        searchStory.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                searchStories(editable.toString().toLowerCase());
            }
        });

        return view;
    }

    private void searchStories(String s) {
        listSearch = new ArrayList<>();
        if (s.isEmpty()) {
            showPost();
        } else {
           listSearch.clear();
           for (Post post : listPost) {
               if (post.getName().toUpperCase().contains(s) ||
                       post.getName().toLowerCase().contains(s)) {
                   listSearch.add(post);
               }
           }
           userStoryAdapter = new UserStoryAdapter(getContext(), listSearch, StoryFragment.this);
           recUserStory.setAdapter(userStoryAdapter);
        }
    }

    private void showPost() {
        listPost = new ArrayList<>();
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
        reference.child("posts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listPost.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    String key = snapshot.getKey();
                    assert post != null;
                    assert fuser != null;
                    post.setKey(key);
                    listPost.add(post);
                }
                userStoryAdapter = new UserStoryAdapter(getContext(), listPost, StoryFragment.this);
                recUserStory.setAdapter(userStoryAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view, int position) {

    }

    @Override
    public void likePost(int position) {
        if (listPost != null && listPost.size() > position) {
            Post post = listPost.get(position);
            List<String> likes = new ArrayList<>();
            if (post.getNumberLike(post.getLikes()).size() <= 0) {
                likes.add(fuser.getUid());
            } else {
                likes.addAll(post.getNumberLike(post.getLikes()));
                if (!likes.contains(fuser.getUid())) {
                    likes.add(fuser.getUid());
                } else {
                    likes.remove(fuser.getUid());
                }
            }

            listPost.get(position).setLikes(post.setLikesString(likes));
            Map<String, Object> map = new HashMap<>();
            map.put("likes", listPost.get(position).getLikes());
            reference.child("posts").child(post.getKey()).updateChildren(map);

            if (userStoryAdapter != null) {
                userStoryAdapter.notifyDataSetChanged();
            } else {
                userStoryAdapter = new UserStoryAdapter(getContext(), listPost, StoryFragment.this);
                recUserStory.setAdapter(userStoryAdapter);
            }
        }
    }
}
