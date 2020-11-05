package com.example.techasians_appchat.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techasians_appchat.R;
import com.example.techasians_appchat.adapter.UserChatAdapter;
import com.example.techasians_appchat.adapter.UserOnlineAdapter;
import com.example.techasians_appchat.model.Message;
import com.example.techasians_appchat.model.Sender;
import com.example.techasians_appchat.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MessageFragment extends Fragment {

    FirebaseUser firebaseUser;
    DatabaseReference reference;
    private EditText searchUser;
    private RecyclerView recUserOnline;
    private RecyclerView recUserChat;
    private UserOnlineAdapter userOnlineAdapter;
    private UserChatAdapter userChatAdapter;
    private ArrayList<User> usersChat;
    private ArrayList<User> usersOnline;
    private ArrayList<User> listSearch;
    private List<Sender> listUserChat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        recUserOnline = view.findViewById(R.id.rec_user_online_mes);
        recUserOnline.setHasFixedSize(true);
        recUserChat = view.findViewById(R.id.rec_user_chat_mes);
        recUserChat.setHasFixedSize(true);
        searchUser = view.findViewById(R.id.search_mes);
        readUserChat();
        showUsersOnline();
        searchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                searchUsers(editable.toString());
            }
        });

        return view;
    }

    private void searchUsers(String username) {
        listSearch = new ArrayList<>();
        if (username.isEmpty()) {
            readUserChat();
        } else {
            listSearch.clear();
            for (User user : usersChat) {
                if (user.getName().toUpperCase().contains(username) ||
                        user.getName().toLowerCase().contains(username)) {
                    listSearch.add(user);
                }
            }
            userChatAdapter = new UserChatAdapter(getContext(), listSearch);
            recUserChat.setAdapter(userChatAdapter);
        }
    }

    private void readUserChat() {
        listUserChat = new ArrayList<>();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listUserChat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    if (message.getSender().equals(firebaseUser.getUid())) {
                        Sender sender = new Sender(message.getReceiver(), message.getTime());
                        if (!listUserChat.contains(sender)) {
                            listUserChat.add(sender);
                        }
                    }
                    if (message.getReceiver().equals(firebaseUser.getUid())) {
                        Sender sender = new Sender(message.getSender(), message.getTime());
                        if (!listUserChat.contains(sender)) {
                            listUserChat.add(sender);
                        }
                    }
                }
                Collections.sort(listUserChat, new Comparator<Sender>() {
                    @Override
                    public int compare(Sender sender, Sender t1) {
                        if (sender.getTime() > t1.getTime()) {
                            return -1;
                        } else  if (sender.getTime() < t1.getTime()) {
                            return 1;
                        }
                        return 0;
                    }
                });
                showUserChat();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showUserChat() {
        usersChat = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersChat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    for (int i = 0; i <  listUserChat.size(); i++) {
                        if (user.getId().equals(listUserChat.get(i).getId())) {
                           if (!usersChat.contains(user)) {
                               user.setIndex(i);
                               usersChat.add(user);
                           }
                        }
                    }
                }
                Collections.sort(usersChat, new Comparator<User>() {
                    @Override
                    public int compare(User user, User t1) {
                        if (user.getIndex() > t1.getIndex()) {
                            return 1;
                        } else  if (user.getIndex() < t1.getIndex()) {
                            return -1;
                        }
                        return 0;
                    }
                });
                userChatAdapter = new UserChatAdapter(getContext(), usersChat);
                recUserChat.setAdapter(userChatAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void showUsersOnline() {
        usersOnline = new ArrayList<>();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
        reference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersOnline.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    assert user != null;
                    assert firebaseUser != null;
                    if (user.getState() != null && user.getState().equals("on") && user.getId() != null &&
                            !user.getId().equals(firebaseUser.getUid())) {
                        usersOnline.add(user);
                    }
                }

                userOnlineAdapter = new UserOnlineAdapter(getContext(), usersOnline);
                recUserOnline.setAdapter(userOnlineAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("TEST", "onCancelled: ");
            }
        });
    }
}
