package com.example.techasians_appchat.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.techasians_appchat.R;
import com.example.techasians_appchat.adapter.RoomChatAdapter;
import com.example.techasians_appchat.model.Message;
import com.example.techasians_appchat.model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.HashMap;

public class RoomChatActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE = 100;
    private static final int IMAGE_GALLERY = 200;
    private ImageView imgBack;
    private RoundedImageView mImgAvatar;
    private TextView mTxtName;
    private ImageButton mImgSend;
    private ImageButton mImgPicture;
    private EditText mEdtChat;

    RoomChatAdapter roomChatAdapter;
    ArrayList<Message> messageList;
    RecyclerView recRoomChat;

    DatabaseReference reference;
    FirebaseUser fuser;
    private String userID;
    private Uri fileUri;
    private StorageTask uploadTask;
    private String myUrl = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_chat);

        Intent intent = getIntent();
        userID = intent.getStringExtra("userId");
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        initView();
        initData();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initView() {
        imgBack = findViewById(R.id.img_back);
        mImgAvatar = findViewById(R.id.img_user_chat);
        mTxtName = findViewById(R.id.name_user_chat);
        mEdtChat = findViewById(R.id.edt_chat);
        mImgSend = findViewById(R.id.btn_send);
        mImgPicture = findViewById(R.id.btn_picture);

        recRoomChat = findViewById(R.id.rec_user_room_chat);
        recRoomChat.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recRoomChat.setLayoutManager(linearLayoutManager);

        mImgSend.setOnClickListener(this);
        mImgPicture.setOnClickListener(this);

    }

    private void initData() {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("users").child(userID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                mTxtName.setText(user.getName());
                Glide.with(RoomChatActivity.this).load(user.getAvatar())
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(mImgAvatar);
                readChat(fuser.getUid(), userID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                fuser = FirebaseAuth.getInstance().getCurrentUser();
                String msg = mEdtChat.getText().toString();
                if (!msg.equals("")) {
                    sendChat(fuser.getUid(), userID, msg, ServerValue.TIMESTAMP, "text");
                } else {
                    Toast.makeText(this, "Không gửi được tin nhắn", Toast.LENGTH_SHORT).show();
                }
                mEdtChat.setText("");
                break;
            case R.id.btn_picture:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent.createChooser(intent, "Select image"), REQUEST_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            fileUri = data.getData();
            uploadImage();
        }
    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sending image...");
        progressDialog.show();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images");
        final StorageReference filePath = storageReference.child(System.currentTimeMillis() + ".jpg");
        uploadTask = filePath.putFile(fileUri);
        uploadTask.continueWithTask(new Continuation() {
            @Override
            public Object then(@NonNull Task task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return filePath.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUrl = task.getResult();
                    myUrl = downloadUrl.toString();
                    fuser = FirebaseAuth.getInstance().getCurrentUser();
                    sendChat(fuser.getUid(), userID, myUrl, ServerValue.TIMESTAMP, "image");
                    progressDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(RoomChatActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendChat(String sender, String receiver, String message, Object time, String type) {
        reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("time", time);
        hashMap.put("type", type);
        reference.child("chats").push().setValue(hashMap);
    }

    private void readChat(final String myId, final String yourId) {
        messageList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    if (message.getReceiver().equals(yourId) && message.getSender().equals(myId) ||
                            message.getReceiver().equals(myId) && message.getSender().equals(yourId)) {
                        messageList.add(message);
                    }
                }
                roomChatAdapter = new RoomChatAdapter(RoomChatActivity.this, messageList);
                recRoomChat.setAdapter(roomChatAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
