package com.test.eceinstagram;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.test.eceinstagram.SignupActivity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import static com.test.eceinstagram.SignupActivity.BIO;
import static com.test.eceinstagram.SignupActivity.USER_INFO;


public class HomeActivity extends AppCompatActivity {
    Button btnLogout;
    ImageView capturedProfile;
    TextView displayUserName;
    TextView displayBio;
    String[] passUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        passUserInfo = getIntent().getExtras().getString("passUserInfo").split(",");
        // set captured profile image in this activity
        capturedProfile = findViewById(R.id.image_captured);
        // String pathToImage = getIntent().getExtras().getString("imagepath");
        Bitmap bitmap = BitmapFactory.decodeFile(passUserInfo[2]);
        Log.d("imagepath", passUserInfo[2]);
        capturedProfile.setImageBitmap(bitmap);

        displayUserName = findViewById(R.id.usernameView);
        displayUserName.setText(passUserInfo[0]);
        displayBio = findViewById(R.id.shortBio);
        displayBio.setText(passUserInfo[1]);
        // set userinfo from firebase database


        btnLogout = findViewById(R.id.logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intToMain = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intToMain);
            }
        });
    }




}