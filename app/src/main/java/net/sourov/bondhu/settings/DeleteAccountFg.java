package net.sourov.bondhu.settings;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.sourov.bondhu.R;

import java.util.Objects;

public class DeleteAccountFg extends Fragment {

      public DeleteAccountFg() {
        // Required empty public constructor
    }

  @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=  inflater.inflate(R.layout.fragment_delete_account_fg, container, false);

      SettingsActivity mYourActivity = (SettingsActivity) getActivity();
      assert mYourActivity != null;
      Objects.requireNonNull(mYourActivity.getSupportActionBar()).setTitle("Delete Account");

      return view;
    }
}