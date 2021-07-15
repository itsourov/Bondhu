package net.sourov.bondhu.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import net.sourov.bondhu.AddFriends;
import net.sourov.bondhu.Dashboard;
import net.sourov.bondhu.FriendList;
import net.sourov.bondhu.R;
import net.sourov.bondhu.SaveState;
import net.sourov.bondhu.auth.LoginActivity;

import org.jetbrains.annotations.NotNull;

public class SettingsActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    SaveState saveState;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //hooks for menu layout
        Toolbar toolbar;
        toolbar = findViewById(R.id.toolbarOnSettings);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout_Settings);
        navigationView = findViewById(R.id.nav_view_Setings);
        navigationView.setItemIconTintList(null);

        //navigation toggle
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_deawer_open, R.string.navigation_deawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction frt = fragmentManager.beginTransaction();
        frt.add(R.id.settingsParent,new MainSettings());
        frt.commit();

        saveState = new SaveState(this);

        if (saveState.getState() == 0) {

            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else if (saveState.getState() == 1) {

            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        } else if (saveState.getState() == 2) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @org.jetbrains.annotations.NotNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.home_menu) {
            startActivity(new Intent(SettingsActivity.this, Dashboard.class));
            finish();
        } else if (itemId == R.id.fd_list_menu) {
            startActivity(new Intent(SettingsActivity.this, FriendList.class));
            finish();

        } else if (itemId == R.id.add_fd_menu) {
            startActivity(new Intent(SettingsActivity.this, AddFriends.class));
            finish();
        } else if (itemId == R.id.log_out_menu) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
            finish();
        }
        return true;
    }
    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
           // super.onBackPressed();
            startActivity(new Intent(SettingsActivity.this,Dashboard.class));
            finish();
            //additional code
        } else {
            getSupportFragmentManager().popBackStack();
        }

    }

}