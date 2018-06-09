package com.example.android.sunshine;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
// TODO completed (4) Create SettingsFragment and extend PreferenceFragmentCompat

// Do steps 5 - 11 within SettingsFragment

// TODO completed (5) Override onCreatePreferences and add the preference xml file using addPreferencesFromResource

public class SettingsFragment
        extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener,
        Preference.OnPreferenceChangeListener {


    public SettingsFragment() {
        // Required empty public constructor
    }

    // Do step 9 within onCreatePreference
    // TODO completed (9) Set the preference summary on each preference that isn't a CheckBoxPreference
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.prefs);
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        int count = preferenceScreen.getPreferenceCount();
        for (int i = 0; i < count; i++) {
            Preference preference = preferenceScreen.getPreference(i);
            if (!(preference instanceof CheckBoxPreference)) {
                setPreferenceSummary(preference,
                        sharedPreferences.getString(preference.getKey(), ""));
            }
            preference.setOnPreferenceChangeListener(this);
        }
    }

    // TODO completed (10) Implement OnSharedPreferenceChangeListener from SettingsFragment
    // TODO completed (11) Override onSharedPreferenceChanged to update non CheckBoxPreferences when they are changed
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if (preference != null) {
            setPreferenceSummary(preference,
                    sharedPreferences.getString(preference.getKey(), ""));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TextView textView = new TextView(getActivity());
        textView.setText(R.string.hello_blank_fragment);
        return textView;
    }

    // TODO completed (12) Register SettingsFragment (this) as a SharedPreferenceChangedListener in onStart
    @Override
    public void onStart() {
        super.onStart();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }


    // TODO completed (13) Unregister SettingsFragment (this) as a SharedPreferenceChangedListener in onStop
    @Override
    public void onStop() {
        super.onStop();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(SettingsFragment.this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return false;
    }

    // TODO completed (8) Create a method called setPreferenceSummary that accepts a Preference and an Object and sets the summary of the preference
    private void setPreferenceSummary(Preference preference, Object object) {
        // Location
        if (preference instanceof EditTextPreference) {
            preference.setSummary((String) object);
        } else if (preference instanceof ListPreference) {
            int index = ((ListPreference) preference).findIndexOfValue((String) object);
            if (index >= 0)
                preference.setSummary(((ListPreference) preference).getEntries()[index]);
        }
    }
}
