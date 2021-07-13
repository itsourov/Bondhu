package net.sourov.bondhu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import net.sourov.bondhu.auth.LoginActivity;


public class MainActivity extends AppCompatActivity {

    Toast myToast;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance(); //initialize firebase auth system
        myToast = Toast.makeText(getApplicationContext(), null, Toast.LENGTH_SHORT);

        if (mAuth.getCurrentUser()!=null){
            startActivity(new Intent(MainActivity.this, Dashboard.class));
            finish();
        }else {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }

    }


}