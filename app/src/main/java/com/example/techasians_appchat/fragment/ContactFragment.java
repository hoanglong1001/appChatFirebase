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
import com.example.techasians_appchat.adapter.UserContactAdapter;
import com.example.techasians_appchat.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ContactFragment extends Fragment {
    FirebaseUser firebaseUser;
    DatabaseReference reference;

    private RecyclerView recUserContact;
    private UserContactAdapter userContactAdapter;
    private ArrayList<User> listUser;
    private EditText searchUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        recUserContact = view.findViewById(R.id.rec_user_cont);
        recUserContact.setHasFixedSize(true);
        searchUser = view.findViewById(R.id.search_cont);
        listUser = new ArrayList<>();

        searchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                searchUsers(editable.toString().toLowerCase());
            }
        });
        readUsersContact();
        return view;
    }

    private void searchUsers(String username) {
        if (username.isEmpty()) {
            readUsersContact();
        } else {
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            Query query = FirebaseDatabase.getInstance().getReference("users").orderByChild("search")
                    .startAt(username)
                    .endAt(username + "\uf8ff");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    listUser.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        assert user != null;
                        assert firebaseUser != null;
                        if (!user.getId().equals(firebaseUser.getUid())) {
                            listUser.add(user);
                        }
                    }
                    userContactAdapter = new UserContactAdapter(getContext(), listUser);
                    recUserContact.setAdapter(userContactAdapter);
                    userContactAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void readUsersContact() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
        reference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (searchUser.getText().toString().equals("")) {
                    listUser.clear();

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        User user = dataSnapshot1.getValue(User.class);
                        assert user != null;
                        assert firebaseUser != null;
                        if (!user.getId().equals(firebaseUser.getUid())) {
                            listUser.add(user);
                        }
                    }

                    userContactAdapter = new UserContactAdapter(getContext(), listUser);
                    recUserContact.setAdapter(userContactAdapter);
                    userContactAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("TEST", "onCancelled: ");
            }
        });
    }
}
