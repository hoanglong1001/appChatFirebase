package com.example.techasians_appchat.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.techasians_appchat.R;
import com.example.techasians_appchat.fragment.AboutFragment;
import com.example.techasians_appchat.fragment.ContactFragment;
import com.example.techasians_appchat.fragment.MessageFragment;
import com.example.techasians_appchat.fragment.StoryFragment;
import com.example.techasians_appchat.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initBottomNavigation();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog_Alert);
        dialog.setTitle("Đăng xuất");
        dialog.setMessage("Bạn có muốn đăng xuất");
        dialog.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                FirebaseAuth.getInstance().signOut();
                mGoogleSignInClient.signOut();
                finish();
            }
        });
        dialog.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog.show();
    }

    private void initView() {
        bottomNav = findViewById(R.id.bottom_navigation);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void initBottomNavigation() {
        final User user = (User) getIntent().getSerializableExtra("user");
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment selectedFragment = null;
                switch (menuItem.getItemId()) {
                    case R.id.nav_message:
                        selectedFragment = new MessageFragment();
                        break;
                    case R.id.nav_contact:
                        selectedFragment = new ContactFragment();
                        break;
                    case R.id.nav_story:
                        selectedFragment = new StoryFragment();
                        break;
                    case R.id.nav_about:
                        selectedFragment = AboutFragment.newInstant(user);
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                return true;
            }
        });
        bottomNav.setSelectedItemId(R.id.nav_message);
    }
}
