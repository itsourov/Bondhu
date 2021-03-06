package net.sourov.bondhu.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.sourov.bondhu.Dashboard;
import net.sourov.bondhu.EditUserDetails;
import net.sourov.bondhu.R;
import net.sourov.bondhu.VerifyActivity;

import java.util.HashMap;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    String name,email, image_url, number;
    private static final int RC_SIGN_IN = 4;
    private static final String TAG = "tag";
    EditText inputNumberOnLogin;
    String numberInput;
    Toast myToast;
    private FirebaseAuth mAuth;

    ProgressBar spin_kitOnMobileAuth;
    Button btnLoginOnLogin;

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance(); //initialize firebase auth system
        myToast = Toast.makeText(getApplicationContext(), null, Toast.LENGTH_LONG);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        inputNumberOnLogin = findViewById(R.id.inputNumberOnLogin);
        btnLoginOnLogin = findViewById(R.id.btnGetOtpOnMobileAuth);

        spin_kitOnMobileAuth = findViewById(R.id.spin_kitOnMobileAuth);


        btnLoginOnLogin.setOnClickListener(v -> {


            numberInput = inputNumberOnLogin.getText().toString().trim();
            Intent intent = new Intent(LoginActivity.this, VerifyActivity.class);
            intent.putExtra("number",numberInput);
            startActivity(intent);
        });
        findViewById(R.id.goToSignUpOnLogin).setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            finish();
        });

        findViewById(R.id.googleImageOnLogin).setOnClickListener(v -> signIn());

    }

    private void signIn() {
        spin_kitOnMobileAuth.setVisibility(View.VISIBLE);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        spin_kitOnMobileAuth.setVisibility(View.GONE);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");
                        if (task.getResult().getAdditionalUserInfo().isNewUser()) {

                            sendUserDataFromGmail();
                            Toast.makeText(LoginActivity.this, "Account Created with the name " + mAuth.getCurrentUser().getDisplayName(), Toast.LENGTH_SHORT).show();
                        } else {
                            goToDashboard();
                        }


                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        myToast.setText(task.getException().getMessage());
                        myToast.show();
                    }
                });
    }

    private void sendUserDataFromGmail() {
        spin_kitOnMobileAuth.setVisibility(View.VISIBLE);
        FirebaseUser user = mAuth.getCurrentUser();

        assert user != null;
        name = user.getDisplayName();
        email = user.getEmail();
        number = user.getPhoneNumber();
        image_url = String.valueOf(user.getPhotoUrl());


        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", name);
        hashMap.put("email", email);
        hashMap.put("number", number);
        if (image_url == null) {
            hashMap.put("imageUrl", "https://image.shutterstock.com/image-vector/picture-vector-icon-no-image-260nw-1732584341.jpg");
        } else {
            hashMap.put("imageUrl", image_url);
        }

        hashMap.put("dateOFBirth", user.getMetadata());
        hashMap.put("userID", mAuth.getCurrentUser().getUid());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Users").child(mAuth.getCurrentUser().getUid()).child("selfInfo").setValue(hashMap)
                .addOnCompleteListener(task -> {
                    spin_kitOnMobileAuth.setVisibility(View.GONE);

                    myToast.setText("information collected by database");
                    myToast.show();

                    startActivity(new Intent(LoginActivity.this, EditUserDetails.class));
                    finish();
                });

    }

    private void goToDashboard() {
        startActivity(new Intent(LoginActivity.this, Dashboard.class));
        finish();
    }
}