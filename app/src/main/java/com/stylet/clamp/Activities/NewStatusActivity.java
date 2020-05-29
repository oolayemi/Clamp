package com.stylet.clamp.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.stylet.clamp.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class NewStatusActivity extends AppCompatActivity {


    private Toolbar newStatusToolbar;
    private ImageView newStatusImage;
    private EditText newStatusDesc;
    private Button statusPostBtn;
    private ProgressBar newStatusProgress;

    private Uri statusImageUri = null;

    private Bitmap compressedImageFile;


    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private FirebaseUser user;

    private String current_user_id;
    private String current_user_name;
    private String current_user_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_status);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        user = firebaseAuth.getCurrentUser();

        current_user_id = firebaseAuth.getCurrentUser().getUid();
        current_user_name = firebaseAuth.getCurrentUser().getDisplayName();

        current_user_image = firebaseAuth.getCurrentUser().getPhotoUrl().toString();



        newStatusToolbar = findViewById(R.id.new_status_toolbar);
        newStatusImage = findViewById(R.id.new_status_image);
        newStatusDesc = findViewById(R.id.new_status_desc);
        statusPostBtn = findViewById(R.id.status_post_btn);
        newStatusProgress = findViewById(R.id.new_status_progress);

        setSupportActionBar(newStatusToolbar);
        getSupportActionBar().setTitle("New Status update");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        newStatusImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(256, 256)
                        .setAspectRatio(1,1)
                        .start(NewStatusActivity.this);
            }
        });

        statusPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String desc = newStatusDesc.getText().toString();

                if (!TextUtils.isEmpty(desc) && statusImageUri != null) {

                    newStatusProgress.setVisibility(View.VISIBLE);

                    final String randomName = UUID.randomUUID().toString();

                    // PHOTO UPLOAD
                    File newImageFile = new File(statusImageUri.getPath());
                    try {

                        compressedImageFile = new Compressor(NewStatusActivity.this)
                                .setMaxHeight(720)
                                .setMaxWidth(720)
                                .setQuality(50)
                                .compressToBitmap(newImageFile);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imageData = baos.toByteArray();

                    // PHOTO UPLOAD

                    final StorageReference imagePathFile = storageReference.child("status_images").child(randomName + ".jpg");
                    imagePathFile.putBytes(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imagePathFile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String downloadUri = uri.toString();


                                    Map<String, Object> postMap = new HashMap<>();
                                    postMap.put("username", current_user_name);
                                    postMap.put("user_image", current_user_image);
                                    postMap.put("user_id", current_user_id);
                                    postMap.put("image_url", downloadUri);
                                    postMap.put("desc", desc);
                                    postMap.put("timeset", FieldValue.serverTimestamp());

                                    firebaseFirestore.collection("Status").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {

                                            if (task.isSuccessful()) {

                                                Toast.makeText(NewStatusActivity.this, "New status added", Toast.LENGTH_LONG).show();
                                                Intent mainIntent = new Intent(NewStatusActivity.this, MainActivity.class);
                                                startActivity(mainIntent);
                                                finish();

                                            } else {


                                            }

                                            newStatusProgress.setVisibility(View.INVISIBLE);

                                        }
                                    });

                                }
                            });

                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                statusImageUri = result.getUri();
                newStatusImage.setImageURI(statusImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }

    }

}