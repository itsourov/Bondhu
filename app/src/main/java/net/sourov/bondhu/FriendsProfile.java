package net.sourov.bondhu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.sourov.bondhu.auth.LoginActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsProfile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    CircleImageView profileImageOnFriendsProfile;
    TextView nameTextOnFriendsProfile, nameOnFriendsProfile, numberOnFriendsProfile, dateOnFriendsProfile, addressOnFriendsProfile, friendAddedOnFriendsProfile;
    String name, number, dateOfBirth, address, image_url, unique_id,fd_added_date;
    FirebaseAuth mAuth;

    DrawerLayout drawerLayout;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_profile);
        mAuth = FirebaseAuth.getInstance();



        name = getIntent().getStringExtra("name");
        number = getIntent().getStringExtra("number");
        dateOfBirth = getIntent().getStringExtra("dateOfBirth");
        address = getIntent().getStringExtra("address");
        image_url = getIntent().getStringExtra("image_url");
        fd_added_date = getIntent().getStringExtra("fd_added_date");
        unique_id = getIntent().getStringExtra("unique_id");

        //hooks for menu layout
        Toolbar toolbar;
        toolbar = findViewById(R.id.toolbarOnFriendsProfile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Details of "+name);
        drawerLayout = findViewById(R.id.drawer_layout_friendProfile);
        navigationView = findViewById(R.id.navViewOnFriendsProfile);
        navigationView.setItemIconTintList(null);

        //navigation toggle
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_deawer_open, R.string.navigation_deawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        profileImageOnFriendsProfile = findViewById(R.id.profileImageOnFriendsProfile);

        nameTextOnFriendsProfile = findViewById(R.id.nameTextOnFriendsProfile);
        nameOnFriendsProfile = findViewById(R.id.nameOnFriendsProfile);
        numberOnFriendsProfile = findViewById(R.id.numberOnFriendsProfile);
        dateOnFriendsProfile = findViewById(R.id.dateOnFriendsProfile);
        addressOnFriendsProfile = findViewById(R.id.addressOnFriendsProfile);
        friendAddedOnFriendsProfile = findViewById(R.id.friendAddedOnFriendsProfile);



        nameTextOnFriendsProfile.setText(name);
        nameOnFriendsProfile.setText(name);
        numberOnFriendsProfile.setText(number);
        dateOnFriendsProfile.setText(dateOfBirth);
        addressOnFriendsProfile.setText(address);
        friendAddedOnFriendsProfile.setText(fd_added_date);

        Glide.with(getApplicationContext()).load(image_url).placeholder(R.drawable.loading).into(profileImageOnFriendsProfile);

        //Todo   friendAddedOnFriendsProfile.setText(name);

        profileImageOnFriendsProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(),ImageViewer.class);
            intent.putExtra("image_url", image_url);
            startActivity(intent);
        });

        findViewById(R.id.openGalleryOnEditContactDetailsendOnFriendsProfile).setOnClickListener(v -> deleteThisFriend());
        findViewById(R.id.openCameraOnEditContactDetails).setOnClickListener(v -> editThisFriend());

    }

    private void editThisFriend() {
        Intent goTOFriendProfile = new Intent(getApplicationContext(), EditContactDetails.class);
        goTOFriendProfile.putExtra("name", name);
        goTOFriendProfile.putExtra("number", number);
        goTOFriendProfile.putExtra("dateOfBirth", dateOfBirth);
        goTOFriendProfile.putExtra("address", address);
        goTOFriendProfile.putExtra("image_url", image_url);
        goTOFriendProfile.putExtra("unique_id", unique_id);
        startActivity(goTOFriendProfile);
        finish();
    }

    private void deleteThisFriend() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(mAuth.getCurrentUser().getUid()).child("Friends").child(unique_id);

        AlertDialog.Builder builder = new AlertDialog.Builder(FriendsProfile.this);
        builder.setTitle("Confirm deleting " + name + " ...");
        builder.setIcon(R.drawable.delete);
        builder.setMessage("Are you sure you want to delete " + name + " from your friend list?");
        builder.setPositiveButton("Yes", (dialog, which) -> reference.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(FriendsProfile.this, "deleted", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(FriendsProfile.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }));
        builder.setNegativeButton("No", (dialog, which) -> {

        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull @org.jetbrains.annotations.NotNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.home_menu) {
            startActivity(new Intent(getApplicationContext(), Dashboard.class));
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