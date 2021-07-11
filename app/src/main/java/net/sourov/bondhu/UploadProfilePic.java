package net.sourov.bondhu;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class UploadProfilePic extends AppCompatActivity {

    CircleImageView profileImageOnUploadProfile;

    FirebaseAuth mAuth;
    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;
    Toast myToast;

    private Uri photoURI = null;
    private Uri croppedPhotoUri;
    public static final int CAMERA_CODE = 200;
    private static final int PICK_IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_profile_pic);
        myToast = Toast.makeText(getApplicationContext(), null, Toast.LENGTH_SHORT);


        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mAuth = FirebaseAuth.getInstance();

        profileImageOnUploadProfile = findViewById(R.id.profileImageOnUploadProfile);


        findViewById(R.id.openGalleryOnUploadProfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGalleryIntent();
            }
        });
        findViewById(R.id.openCameraOnUploadProfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCameraIntent();
            }
        });

        findViewById(R.id.submitOnUploadProfile).setOnClickListener(v -> {

            if (croppedPhotoUri!=null){
                try {
                    checkImageBeforeUpload();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                myToast.setText("You must select or pick a image");
            }
        });
    }


    private void openGalleryIntent() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");


        Intent pickIntent = new Intent(Intent.ACTION_PICK);
        pickIntent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});


        try {
            startActivityForResult(pickIntent, PICK_IMAGE);

        } catch (Exception e) {
            e.printStackTrace();
            startActivityForResult(chooserIntent, PICK_IMAGE);
        }
    }

    private void openCameraIntent() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "temp_title");
        contentValues.put(MediaStore.Images.Media.TITLE, "temp_desc");
        photoURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        startActivityForResult(intent, CAMERA_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_CODE) {
                if (photoURI == null) {
                    myToast.setText("The selected image appears to be blank");

                } else {
                    cropImage(photoURI);
                }

            } else if (requestCode == UCrop.REQUEST_CROP) {
                croppedPhotoUri = UCrop.getOutput(data);
                profileImageOnUploadProfile.setImageURI(croppedPhotoUri);
            } else if (requestCode == PICK_IMAGE) {
                photoURI = data.getData();
                cropImage(photoURI);
            }
        }
    }


    private void cropImage(Uri photoURI) {
        UCrop.of(photoURI, Uri.fromFile(new File(getCacheDir(), UUID.randomUUID() + ".jpg")))
                .withAspectRatio(1, 1)
                .start(UploadProfilePic.this);
    }

    private void checkImageBeforeUpload() throws IOException {
        File f = new File(croppedPhotoUri.getPath());
        long sizeInByte = f.length();
        if (sizeInByte < 51200) {
            uploadImage(f);
        } else {
            File compressedImageFile = new Compressor(this)
                    .setMaxWidth(600)
                    .setMaxHeight(600)
                    .setCompressFormat(Bitmap.CompressFormat.WEBP)
                    .compressToFile(f);
            uploadImage(compressedImageFile);
        }

    }
    private void uploadImage(File imageFile) {
        ProgressDialog progressDialog
                = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
        StorageReference ref
                = FirebaseStorage.getInstance().getReference()
                .child("users/" + mAuth.getCurrentUser().getUid()).child("profilePic/").child(UUID.randomUUID().toString() + ".webp");
        ref.putFile(Uri.fromFile(imageFile)).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> task1 = taskSnapshot.getStorage().getDownloadUrl();
            task1.addOnSuccessListener(uri -> {

                uploadData(uri.toString());
            });

            progressDialog.dismiss();
            myToast.setText("Image Uploaded!!");
            myToast.show();

        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            myToast.setText("Failed " + e.getMessage());
            myToast.show();
        }).addOnProgressListener(taskSnapshot -> {
            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
            progressDialog.setMessage("Uploaded " + (int) progress + "%");
        });

    }


    private void uploadData(String imageUrl) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("selfInfo");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("imageUrl", imageUrl);


        reference.updateChildren(hashMap)
                .addOnCompleteListener(task -> {

                    myToast.setText("data received by database");
                    startActivity(new Intent(UploadProfilePic.this, Dashboard.class));
                    finish();
                });
    }
}