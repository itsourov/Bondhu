package net.sourov.bondhu.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.sourov.bondhu.EditUserDetails;
import net.sourov.bondhu.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    EditText inputNameOnSignUp, inputNumberOnSignUp, inputDateOnSignUp, inputEmailOnSignUp, inputPasswordOnSignUp;
    String name, email, password, number,dateOfBirth,image_url;
    ProgressBar spin_kitOnSignUp;
    Toast myToast;

    private static final int RC_SIGN_IN = 4;
    private static final String TAG = "tag";
    private GoogleSignInClient mGoogleSignInClient;

    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();

        myToast = Toast.makeText(getApplicationContext(), null, Toast.LENGTH_LONG);
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        inputNameOnSignUp = findViewById(R.id.inputNameOnSignUp);
        inputNumberOnSignUp = findViewById(R.id.inputNumberOnSignUp);
        inputDateOnSignUp = findViewById(R.id.inputDateOnSignUp);
        inputEmailOnSignUp = findViewById(R.id.inputEmailOnSignUp);
        inputPasswordOnSignUp = findViewById(R.id.inputPasswordOnSignUp);
        spin_kitOnSignUp = findViewById(R.id.spin_kitOnSignUp);

        inputDateOnSignUp.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(
                    SignUpActivity.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    mDateSetListener,
                    year, month, day);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });
        mDateSetListener = (datePicker, year, month, day) -> {
            month = month + 1;
           String date = day + "/" + month + "/" + year;
            inputDateOnSignUp.setText(date);
        };

        findViewById(R.id.signUpButtonOnSignUp).setOnClickListener(v -> {
            name = inputNameOnSignUp.getText().toString().trim();
            email = inputEmailOnSignUp.getText().toString().trim();
            password = inputPasswordOnSignUp.getText().toString().trim();
            number = inputNumberOnSignUp.getText().toString().trim();
            dateOfBirth = inputDateOnSignUp.getText().toString().trim();

            spin_kitOnSignUp.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    sendData();
                } else {
                    spin_kitOnSignUp.setVisibility(View.GONE);
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        inputEmailOnSignUp.setError("The email already exists");
                        inputEmailOnSignUp.requestFocus();
                    } else {
                        myToast.setText(Objects.requireNonNull(task.getException()).getMessage());
                        myToast.show();
                    }
                }
            });


        });

        findViewById(R.id.goToLogInOnSignUp).setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        });

        findViewById(R.id.googleImageOnSignUp).setOnClickListener(v -> {
            signIn();
        });


    }

    private void signIn() {
        spin_kitOnSignUp.setVisibility(View.VISIBLE);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        spin_kitOnSignUp.setVisibility(View.GONE);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
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
                            goToEditUserDetails();
                        } else {
                            sendUserDataFromGmail();
                            Toast.makeText(SignUpActivity.this, "Account Created with the name " + mAuth.getCurrentUser().getDisplayName(), Toast.LENGTH_SHORT).show();

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
        spin_kitOnSignUp.setVisibility(View.VISIBLE);
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
        hashMap.put("imageUrl", image_url);

        hashMap.put("dateOFBirth", user.getMetadata());
        hashMap.put("userID", mAuth.getCurrentUser().getUid());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Users").child(mAuth.getCurrentUser().getUid()).child("selfInfo").setValue(hashMap)
                .addOnCompleteListener(task -> {
                    spin_kitOnSignUp.setVisibility(View.GONE);

                    myToast.setText("information collected by database");
                    myToast.show();

                    goToEditUserDetails();
                });

    }


    private void sendData() {
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser()
                .getUid()).child("selfInfo");
        HashMap<String, Object> hashMap = new HashMap<>();

            hashMap.put("name", name);
            hashMap.put("email", email);
            hashMap.put("number", number);
            hashMap.put("imageUrl", image_url);
            hashMap.put("dateOFBirth", dateOfBirth);
            hashMap.put("userID", mAuth.getCurrentUser().getUid());

        reference.updateChildren(hashMap)
                .addOnCompleteListener(task -> {
                    Toast.makeText(this, "Info collected by database", Toast.LENGTH_SHORT).show();
                    spin_kitOnSignUp.setVisibility(View.GONE);

                    goToEditUserDetails();
                });
         }

    private void goToEditUserDetails() {
        startActivity(new Intent(SignUpActivity.this, EditUserDetails.class));
        finish();
    }
}