package net.sourov.bondhu.settings;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import net.sourov.bondhu.R;

import java.util.Objects;


public class ChangePassword extends Fragment {

    String currentPass, newPass, newPass2;
    ProgressBar spinner;
    EditText currentPassOnChangePasswordFg, newPassOnChangePasswordFg, retypeNewPassOnChangePasswordFg;

    public ChangePassword() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chnage_password, container, false);

        SettingsActivity mYourActivity = (SettingsActivity) getActivity();
        assert mYourActivity != null;
        Objects.requireNonNull(mYourActivity.getSupportActionBar()).setTitle("Change Password");

        spinner = view.findViewById(R.id.spin_kitOnChangePasswordFg);

        currentPassOnChangePasswordFg = view.findViewById(R.id.currentPassOnChangePasswordFg);
        newPassOnChangePasswordFg = view.findViewById(R.id.newPassOnChangePasswordFg);
        retypeNewPassOnChangePasswordFg = view.findViewById(R.id.retypeNewPassOnChangePasswordFg);


        view.findViewById(R.id.updatePasswordOnChangePasswordFg).setOnClickListener(v -> {
            currentPass = currentPassOnChangePasswordFg.getText().toString().trim();
            newPass = newPassOnChangePasswordFg.getText().toString().trim();
            newPass2 = retypeNewPassOnChangePasswordFg.getText().toString().trim();
            if (currentPass.isEmpty()) {
                Toast.makeText(getActivity(), "trying to get back to main setting", Toast.LENGTH_SHORT).show();
            } else {
                if (newPass.equals(newPass2)) {
                    changePass();
                } else {
                    retypeNewPassOnChangePasswordFg.setError("Password didn't match");
                }
            }

        });
        return view;
    }

    private void changePass() {
        spinner.setVisibility(View.VISIBLE);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        AuthCredential credential = EmailAuthProvider
                .getCredential(user.getEmail(), currentPass);

        user.reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        user.updatePassword(newPass).addOnCompleteListener(task1 -> {
                            spinner.setVisibility(View.GONE);
                            if (task1.isSuccessful()) {

                                Toast.makeText(getActivity(), "Password updated", Toast.LENGTH_SHORT).show();
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                fragmentManager.popBackStack();
                            } else {
                                Toast.makeText(getActivity(), "Error password not updated", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        spinner.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "Current Password is wrong", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}