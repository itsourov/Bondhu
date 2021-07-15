package net.sourov.bondhu;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseAuth;

import net.sourov.bondhu.auth.LoginActivity;


public class MainActivity extends AppCompatActivity {

    SaveState saveState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



    }

    @Override
    protected void onStart() {
        super.onStart();
        saveState = new SaveState(this);
        if (saveState.getState()==1){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }else if (saveState.getState() ==2){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else if (saveState.getState()==0){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }


        FirebaseAuth  mAuth = FirebaseAuth.getInstance(); //initialize firebase auth system

        if (mAuth.getCurrentUser()!=null){
            startActivity(new Intent(MainActivity.this, Dashboard.class));
            finish();
        }else {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
    }
}