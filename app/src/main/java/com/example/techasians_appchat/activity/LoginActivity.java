package com.example.techasians_appchat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.techasians_appchat.R;
import com.example.techasians_appchat.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private RoundedImageView ggLogin;
    private Button btnLogin;
    private EditText username;
    private EditText password;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    private String TAG = "MainActivity";
    private int GG_SIGN_IN = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseDatabase.getInstance().setPersistenceEnabled(false);

        initView();
    }

    private void initView() {
        username = findViewById(R.id.edt_username);
        password = findViewById(R.id.edt_password);
        btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);

        ggLogin = findViewById(R.id.gg_login);
        mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        ggLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                if (!username.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
                    loginEmail(username.getText().toString(), password.getText().toString());
                } else {
                    Toast.makeText(this, "Bạn hãy nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.gg_login:
                signIn();
                break;
        }

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GG_SIGN_IN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GG_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount acc = completedTask.getResult(ApiException.class);
            Toast.makeText(this, "Signed In Successfully", Toast.LENGTH_SHORT).show();
            firebaseGoogleAuth(acc);
        } catch (ApiException e) {
            Toast.makeText(this, "Sign In Failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void loginEmail(String email, String pass) {
        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    Toast.makeText(LoginActivity.this, "Signed In Successfully", Toast.LENGTH_SHORT).show();
                    FirebaseUser firebaseUser = task.getResult().getUser();
                    String personName = firebaseUser.getDisplayName();
                    String personId = firebaseUser.getUid();
                    String personPhoto = null;
                    if (firebaseUser.getPhotoUrl() != null) {
                        personPhoto = firebaseUser.getPhotoUrl().toString();
                    }
                    User user = new User(personId, personName, personPhoto, "on");
                    Log.d("TEST", "onComplete: " + user);
                    addUserDatabase(user);
                } else {
                    updateUI(null);
                }
            }
        });
    }

    private void firebaseGoogleAuth(GoogleSignInAccount acc) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(acc.getIdToken(), null);
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
//                    Toast.makeText(LoginActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                } else {
//                    Toast.makeText(LoginActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });
    }

    private void updateUI(FirebaseUser firebaseUser) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (account != null) {
            String personName = firebaseUser.getDisplayName();
            String personId = firebaseUser.getUid();
            String personPhoto = null;
            if (firebaseUser.getPhotoUrl() != null) {
                personPhoto = account.getPhotoUrl().toString();
            }
            User user = new User(personId, personName, personPhoto, "on");
            addUserDatabase(user);
        } else {
            Toast.makeText(LoginActivity.this, "Login Failed ", Toast.LENGTH_SHORT).show();
        }
    }

    private void addUserDatabase(final User user) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").child(user.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            Map<String, Object> map = new HashMap<>();
                            map.put(user.getId(), user);
                            databaseReference.child("users").updateChildren(map)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "onSuccess: add user");
                                            movingMainScreen(user);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(LoginActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                                        }
                                    });
                        } else {
                            User user1 = dataSnapshot.getValue(User.class);
                            movingMainScreen(user1);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(LoginActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void movingMainScreen(User user) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);

    }
}
