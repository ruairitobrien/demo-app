package com.bmac.demo;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Simple class to allow settings to be persisted. Provides a default UI.
 * Apparently there's some new way of doing this but I haven't looked it up yet.
 */
public class SettingsActivity extends PreferenceActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}