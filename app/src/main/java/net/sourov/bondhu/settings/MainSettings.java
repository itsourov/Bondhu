package net.sourov.bondhu.settings;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.sourov.bondhu.R;
import net.sourov.bondhu.SaveState;

import java.util.Objects;


public class MainSettings extends Fragment {
SaveState saveState;

    public MainSettings() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_main_settings, container, false);

        SettingsActivity mYourActivity = (SettingsActivity) getActivity();
        assert mYourActivity != null;
        Objects.requireNonNull(mYourActivity.getSupportActionBar()).setTitle("Settings");

        saveState = new SaveState(getActivity());

        if (saveState.getState() == 0) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else if (saveState.getState() == 1) {

            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        } else if (saveState.getState() == 2) {

            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        view.findViewById(R.id.changePasswordOnFgMainSettings).setOnClickListener(v -> {
            Fragment fragment = new ChangePassword();
            fragmentTransaction.replace(R.id.settingsParent, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
        view.findViewById(R.id.changeThemeOnFgMainSettings).setOnClickListener(v -> {
            Fragment fragment = new ChangeThemeFg();
            fragmentTransaction.replace(R.id.settingsParent, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
        view.findViewById(R.id.deleteAccountOnFgMainSettings).setOnClickListener(v -> {
            Fragment fragment = new DeleteAccountFg();
            fragmentTransaction.replace(R.id.settingsParent, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });


        return view;
    }
}