package ru.gleb.manyagin.weathermap.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import ru.gleb.manyagin.weathermap.MainActivity;
import ru.gleb.manyagin.weathermap.R;
import ru.gleb.manyagin.weathermap.another_tasks.FetchWeatherData;
import ru.gleb.manyagin.weathermap.provider.WeatherProviderContract;
import ru.gleb.manyagin.weathermap.services.WeatherService;

/**
 * Created by user on 03.05.2015.
 */
public class DataControlActivity extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add 'general' preferences, defined in the XML file
        addPreferencesFromResource(R.xml.preferences_data);
        // For all preferences, attach an OnPreferenceChangeListener so the UI summary can be
        // updated when the preference changes.

        //bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_show_debug_key)));



        Preference refreshBut = (Preference)findPreference(getString(R.string.action_refresh_key));
        refreshBut.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                FetchWeatherData task;
                task = new FetchWeatherData(getApplicationContext());
                task.execute();

                return true;
            }
        });

        Preference StartServiceBut = (Preference)findPreference(getString(R.string.action_start_service_key));
        StartServiceBut.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent srvIntent = new Intent(getApplicationContext(), WeatherService.class);
                startService(srvIntent);
                return true;
            }
        });

        Preference StopServiceBut = (Preference)findPreference(getString(R.string.action_stop_service_key));
        StopServiceBut.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent srvIntent = new Intent(getApplicationContext(), WeatherService.class);
                stopService(srvIntent);
                return true;
            }
        });

        Preference generalRectBut = (Preference)findPreference(getString(R.string.button_set_general_rect_key));
        generalRectBut.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
                myIntent.putExtra("set_general_rect", "true");
                DataControlActivity.this.startActivity(myIntent);
                return true;
            }
        });

        Preference specialRectBut = (Preference)findPreference(getString(R.string.button_set_special_rect_key));
        specialRectBut.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
                myIntent.putExtra("set_special_rect", "true");
                DataControlActivity.this.startActivity(myIntent);
                return true;
            }
        });
    }


    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(this);

        // Trigger the listener immediately with the preference's
        // current value.
        onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        String stringValue = value.toString();

        if (preference instanceof ListPreference)
        {
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        }
        else
        {
            preference.setSummary(stringValue);
        }
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        LinearLayout root = (LinearLayout)findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.tool_bar, root, false);
        bar.setTitle(getString(R.string.caption_data_base_commands));
        root.addView(bar, 0); // insert at top
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
