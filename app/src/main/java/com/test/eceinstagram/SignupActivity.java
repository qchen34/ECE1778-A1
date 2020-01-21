package com.test.eceinstagram;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.method.SingleLineTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class SignupActivity extends AppCompatActivity {
    // Constants for Firebase database
    public static final String USERNAME = "username";
    public static final String BIO = "bio";
    public static final String USER_INFO = "userInfo";

    public static DocumentReference mDocRef = FirebaseFirestore.getInstance().document("sampleData/UserInfo");
    // naming for items in signup page
    EditText emailID, password, passwordConfirm, username, bio;
    Button btnSignUp, btntakeImage;
    ImageView profileCaptured;

    TextView tvSignIn;
    String UserInfoConca;
    private FirebaseAuth mFirebaseAuth;
    String pathToFile;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailID = findViewById(R.id.email);
        password = findViewById(R.id.pwd);
        passwordConfirm = findViewById(R.id.pwdc);
        username = findViewById(R.id.usernamexml);
        bio = findViewById(R.id.bioxml);

        profileCaptured = findViewById(R.id.profile);
        btntakeImage = findViewById(R.id.capture_button);

        btnSignUp = findViewById(R.id.signup);
        tvSignIn = findViewById(R.id.signin);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailID.getText().toString();
                String pwd = password.getText().toString();
                String pwdc = passwordConfirm.getText().toString();

                // save these two String to firebase
                String user = username.getText().toString();
                String bioInfo = bio.getText().toString();

                final Map<String, Object> UserInfo = new HashMap<>();
                UserInfo.put(USERNAME, user);
                UserInfo.put(BIO, bioInfo);
                mDocRef.set(UserInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(USER_INFO, "Document has been saved");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(USER_INFO, "Document was not saved!", e);
                    }
                });





                if (email.isEmpty()) {
                    emailID.setError("Please enter email");
                    emailID.requestFocus();
                } else if (pwd.isEmpty()) {
                    password.setError("Please enter your password");
                    password.requestFocus();
                } else if (user.isEmpty()) {
                    username.setError("Please enter your username");
                    username.requestFocus();
                } else if (!pwdc.equals(pwd)) {
                    passwordConfirm.setError("Please enter the same password");
                    passwordConfirm.requestFocus();
                } else if (email.isEmpty() && pwd.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Fields Are Empty", Toast.LENGTH_SHORT).show();
                } else if (!(email.isEmpty() && pwd.isEmpty())) {
                    mFirebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(SignupActivity.this, "SignUp Unsuccessful, Please Try Again", Toast.LENGTH_SHORT).show();
                            } else {
                                startActivity(new Intent(SignupActivity.this, HomeActivity.class));
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(SignupActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                }

                // passing taken profile image path to HomeActivity
                fetchUserInfo();
                // passImage();
                passUserInfo();

                // passing username and bio into home activity
//                Intent i = new Intent(SignupActivity.this, HomeActivity.class);
//                sendUsername = username.getText().toString();
//                sendBio = bio.getText().toString();
//                String userbio = sendUsername + "," + sendBio;
//                i.putExtra("Userbio", userbio);
//                startActivity(i);
//                finish();


            }
        });
        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignupActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }
        btntakeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchPictureTakerAction();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            Bitmap bitmap = BitmapFactory.decodeFile(pathToFile);
            profileCaptured.setImageBitmap(bitmap);

        }

    }
    private void dispatchPictureTakerAction() {
        Intent takePic =new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePic.resolveActivity(getPackageManager()) != null) {
            File photoFile = createPhotoFile();
            if (photoFile != null) {
                pathToFile = photoFile.getAbsolutePath();
                Uri photoURI = FileProvider.getUriForFile(SignupActivity.this,
                        "com.test.eceinstagram.fileprovider", photoFile);
                takePic.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePic, 1);
            }
        }

    }

    private File createPhotoFile() {
        String name = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {

            image = File.createTempFile(name, ".jpg", storageDir);
        } catch (IOException e) {
            Log.d("mylog", "Excep : " + e.toString());
        }
        return image;
    }

    public void fetchUserInfo () {
        mDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String userName = documentSnapshot.getString(USERNAME);
                    String shortBio = documentSnapshot.getString(BIO);

                    if (!userName.isEmpty() && !shortBio.isEmpty()) {
                        UserInfoConca = userName + "," + shortBio + "," + pathToFile;
                    } else {
                        UserInfoConca = "userName missing" + "," + "Bio missing" + "," + pathToFile;
                    }
                }
            }
        });
    }

    private void passImage () {
        // passing taken profile image path to HomeActivity
        Intent passImagePath = new Intent(SignupActivity.this, HomeActivity.class);
        passImagePath.putExtra("imagepath", pathToFile);
        startActivity(passImagePath);
    }
    private  void passUserInfo() {
        if (UserInfoConca != null) {
            Intent passUserInfo = new Intent(SignupActivity.this, HomeActivity.class);
            passUserInfo.putExtra("passUserInfo", UserInfoConca);
            startActivity(passUserInfo);
        }
    }

}

