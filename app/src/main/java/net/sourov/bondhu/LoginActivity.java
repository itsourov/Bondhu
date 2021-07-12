package net.sourov.bondhu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    String dateOfBirth,name,image_url,number;
    private static final int RC_SIGN_IN = 4;
    private static final String TAG = "tag";
    EditText emailInputOnLogin, passInputOnLogin;
    String email, password;
    Toast myToast;
    private FirebaseAuth mAuth;

    ProgressBar progressBar,spin_kit2OnMobileAuth;
    TextView textInsideButtonOnLogin;
    RelativeLayout btnLoginOnLogin;

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

        emailInputOnLogin = findViewById(R.id.inputEmailOnLogin);
        passInputOnLogin = findViewById(R.id.inputPasswordOnLogin);
        btnLoginOnLogin = findViewById(R.id.btnGetOtpOnMobileAuth);

        progressBar = findViewById(R.id.spin_kitOnMobileAuth);
        spin_kit2OnMobileAuth = findViewById(R.id.spin_kit2OnMobileAuth);
        textInsideButtonOnLogin = findViewById(R.id.textInsideButtonOnMobileAuth);


        btnLoginOnLogin.setOnClickListener(v -> {


            email = emailInputOnLogin.getText().toString().trim();
            password = passInputOnLogin.getText().toString().trim();
            if (email.isEmpty()) {
                emailInputOnLogin.setError("email is empty");
                emailInputOnLogin.requestFocus();
            } else if (password.isEmpty()) {
                passInputOnLogin.setError("password is empty");
                passInputOnLogin.requestFocus();
            } else {
                btnLoginOnLogin.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                textInsideButtonOnLogin.setText("Please wait...");

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        btnLoginOnLogin.setEnabled(true);
                        if (mAuth.getCurrentUser().isEmailVerified()) {
                            startActivity(new Intent(getApplicationContext(), Dashboard.class));
                            finish();
                        } else {
                            mAuth.getCurrentUser().sendEmailVerification();
                            myToast.setText("Please verify your email. check your inbox or spamBox");
                            myToast.show();
                        }

                    } else {
                        btnLoginOnLogin.setEnabled(true);
                        textInsideButtonOnLogin.setText("Log in");
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            passInputOnLogin.setError("Password didn't match");
                            passInputOnLogin.requestFocus();
                        } else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                            emailInputOnLogin.setError("Email wasn't found on server");
                            emailInputOnLogin.requestFocus();
                        } else {
                            myToast.setText(Objects.requireNonNull(task.getException()).getMessage());
                            myToast.show();
                        }
                    }
                });
            }
        });
        findViewById(R.id.goToSignUpOnLogin).setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
            finish();
        });

        findViewById(R.id.googleImageOnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

    }

   private void signIn() {
        spin_kit2OnMobileAuth.setVisibility(View.VISIBLE);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        spin_kit2OnMobileAuth.setVisibility(View.GONE);
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
                        if (!task.getResult().getAdditionalUserInfo().isNewUser()){
                            startActivity(new Intent(LoginActivity.this,Dashboard.class));
                            finish();
                        }else {
                            sendUserDataFromGmail();
                            Toast.makeText(LoginActivity.this,"Account Created with the name "+ mAuth.getCurrentUser().getDisplayName(), Toast.LENGTH_SHORT).show();

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
        spin_kit2OnMobileAuth.setVisibility(View.VISIBLE);
        FirebaseUser user = mAuth.getCurrentUser();

        assert user != null;
        name = user.getDisplayName();
        email= user.getEmail();
        number = user.getPhoneNumber();
        image_url = String.valueOf(user.getPhotoUrl());


        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", name);
        hashMap.put("email", email);
        hashMap.put("number", number);
        if (image_url == null) {
            hashMap.put("imageUrl", "https://image.shutterstock.com/image-vector/picture-vector-icon-no-image-260nw-1732584341.jpg");
        }else {
            hashMap.put("imageUrl", image_url);
        }

        hashMap.put("dateOFBirth", user.getMetadata());
        hashMap.put("userID", mAuth.getCurrentUser().getUid());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Users").child(mAuth.getCurrentUser().getUid()).child("selfInfo").setValue(hashMap)
                .addOnCompleteListener(task -> {
                    spin_kit2OnMobileAuth.setVisibility(View.GONE);

                    myToast.setText("information collected by database");
                    myToast.show();
                    startActivity(new Intent(LoginActivity.this,EditUserDetails.class));
                    finish();
                });

    }
}