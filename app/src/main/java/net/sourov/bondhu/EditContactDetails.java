package net.sourov.bondhu;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yalantis.ucrop.UCrop;

import net.sourov.bondhu.auth.LoginActivity;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class EditContactDetails extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    String name, number, dateOfBirth, address, image_url, unique_id;

    CircleImageView ImageOnEditContactDetails;
    TextView nameTextOnEditContactDetails;
    EditText nameOnEditContactDetails,dateOnEditContactDetails,numberOnEditContactDetails,addressOnEditContactDetails;

    DrawerLayout drawerLayout;
    NavigationView navigationView;

    FirebaseAuth mAuth;
    StorageReference storageReference;
    Toast myToast;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private Uri photoURI = null;
    private Uri croppedPhotoUri ;
    public static final int CAMERA_CODE = 200;
    private static final int PICK_IMAGE = 100;
    private String uniqueID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact_details);
        mAuth = FirebaseAuth.getInstance();
        myToast = Toast.makeText(getApplicationContext(), null, Toast.LENGTH_SHORT);

        uniqueID = UUID.randomUUID().toString();

        // get the Firebase  storage reference
        storageReference = FirebaseStorage.getInstance().getReference();


        name = getIntent().getStringExtra("name");
        number = getIntent().getStringExtra("number");
        dateOfBirth = getIntent().getStringExtra("dateOfBirth");
        address = getIntent().getStringExtra("address");
        image_url = getIntent().getStringExtra("image_url");
        unique_id = getIntent().getStringExtra("unique_id");

        //hooks for menu layout
        Toolbar toolbar;
        toolbar = findViewById(R.id.toolbarOnEditContactDetails);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Details of "+name);
        drawerLayout = findViewById(R.id.drawer_layout_EditContactDetails);
        navigationView = findViewById(R.id.nav_view_EditContactDetails);
        navigationView.setItemIconTintList(null);

        //navigation toggle
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_deawer_open, R.string.navigation_deawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        ImageOnEditContactDetails = findViewById(R.id.ImageOnEditContactDetails);
        nameTextOnEditContactDetails = findViewById(R.id.nameTextOnEditContactDetails);

        nameOnEditContactDetails = findViewById(R.id.nameOnEditContactDetails);
        dateOnEditContactDetails = findViewById(R.id.dateOnEditContactDetails);
        numberOnEditContactDetails = findViewById(R.id.numberOnEditContactDetails);
        addressOnEditContactDetails = findViewById(R.id.addressOnEditContactDetails);



        setDetails();

        dateOnEditContactDetails.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(
                    EditContactDetails.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    mDateSetListener,
                    year, month, day);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });

        mDateSetListener = (datePicker, year, month, day) -> {
            month = month + 1;
            String date = day + "/" + month + "/" + year;
            dateOnEditContactDetails.setText(date);
        };

        findViewById(R.id.openGalleryOnEditContactDetails).setOnClickListener(v -> openGalleryIntent());
        findViewById(R.id.openCameraOnEditContactDetails).setOnClickListener(v -> openCameraIntent());


        findViewById(R.id.updateProfileBtnOnEditContactDetails).setOnClickListener(v -> {
            name = nameOnEditContactDetails.getText().toString().trim();
            number = numberOnEditContactDetails.getText().toString().trim();
            dateOfBirth = dateOnEditContactDetails.getText().toString().trim();
            address = addressOnEditContactDetails.getText().toString().trim();
           if (croppedPhotoUri==null){
               sendData();
           }else {
               try {
                   checkImageBeforeUpload();
               } catch (IOException e) {
                   e.printStackTrace();
               }
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
                ImageOnEditContactDetails.setImageURI(croppedPhotoUri);
            } else if (requestCode == PICK_IMAGE) {
                photoURI = data.getData();
                cropImage(photoURI);
            }
        }
    }

    private void cropImage(Uri photoURI) {
        UCrop.of(photoURI, Uri.fromFile(new File(getCacheDir(), UUID.randomUUID() + ".jpg")))
                .withAspectRatio(1, 1)
                .start(EditContactDetails.this);
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





    private void setDetails() {
        Glide.with(getApplicationContext()).load(image_url).placeholder(R.drawable.loading).into(ImageOnEditContactDetails);
        nameTextOnEditContactDetails.setText(name);

        nameOnEditContactDetails.setText(name);
        dateOnEditContactDetails.setText(dateOfBirth);
        numberOnEditContactDetails.setText(number);
        addressOnEditContactDetails.setText(address);

    }



    private void uploadImage(File imageFile) {
        ProgressDialog progressDialog
                = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        StorageReference ref
                = storageReference
                .child("users/").child(mAuth.getCurrentUser().getUid()).child("friends").child(uniqueID + ".webp");
        ref.putFile(Uri.fromFile(imageFile)).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> task1 = taskSnapshot.getStorage().getDownloadUrl();
            task1.addOnSuccessListener(uri -> {
                image_url = uri.toString();
                sendData();
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

    private void sendData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("Friends").child(unique_id);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", name);
        hashMap.put("number", number);
        hashMap.put("dateOfBirth", dateOfBirth);
        hashMap.put("address", address);
        hashMap.put("imageUrl", image_url);


        reference.updateChildren(hashMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                nameOnEditContactDetails.setText(name);
                myToast.setText("data sent to database");
                myToast.show();
            }

        });
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull @org.jetbrains.annotations.NotNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.home_menu) {
            finish();

        } else if (itemId == R.id.fd_list_menu) {
            startActivity(new Intent(getApplicationContext(), FriendList.class));
            finish();

        } else if (itemId == R.id.add_fd_menu) {
            startActivity(new Intent(getApplicationContext(), AddFriends.class));
            finish();
        } else if (itemId == R.id.log_out_menu) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }
        return true;
    }
}