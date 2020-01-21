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
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class HomeActivity extends AppCompatActivity {
    Button btnLogout, btnTakeImg;
    ImageView capturedProfile;
    //String pathToFile;
    String passUsername[];
    String passProfile;
    TextView displayUsername, displayBio;
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // set captured profile image in this activity
        capturedProfile = findViewById(R.id.image_captured);
        String pathToImage = getIntent().getExtras().getString("imagepath");
        Bitmap bitmap = BitmapFactory.decodeFile(pathToImage);
        capturedProfile.setImageBitmap(bitmap);

//        displayUsername = findViewById(R.id.usernameView);
//        passUsername = getIntent().getExtras().getString("Userbio").split(",");
//        displayUsername.setText(passUsername[0]);
//
//        displayBio = findViewById(R.id.shortBio);
//        displayBio.setText(passUsername[1]);




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