package net.sourov.bondhu.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import net.sourov.bondhu.R;
import net.sourov.bondhu.SaveState;

import java.util.Objects;


public class ChangeThemeFg extends Fragment {

    RadioGroup radioGroup;
    RadioButton radioButton1, radioButton2, radioButton3;
    SaveState saveState;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_change_theme_fg, container, false);

        SettingsActivity mYourActivity = (SettingsActivity) getActivity();
        assert mYourActivity != null;
        Objects.requireNonNull(mYourActivity.getSupportActionBar()).setTitle("Change theme");

        radioGroup = view.findViewById(R.id.radioGroupOnChangeThemeFg);

        radioButton1 = view.findViewById(R.id.systemDefultRadioButton);
        radioButton2 = view.findViewById(R.id.lightRadioButton);
        radioButton3 = view.findViewById(R.id.darkRadioButton);

        saveState = new SaveState(getActivity());

        if (saveState.getState() == 0) {
            radioButton1.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else if (saveState.getState() == 1) {
            radioButton2.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        } else if (saveState.getState() == 2) {
            radioButton3.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }


        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {



            // Check which radio button was clicked
            if (checkedId == R.id.systemDefultRadioButton) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                saveState.setState(0);
            } else if (checkedId == R.id.lightRadioButton) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                saveState.setState(1);
            } else if (checkedId == R.id.darkRadioButton) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                saveState.setState(2);

            }
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.settingsParent, new ChangeThemeFg());
            fragmentTransaction.commit();

        });


        return view;
    }
}