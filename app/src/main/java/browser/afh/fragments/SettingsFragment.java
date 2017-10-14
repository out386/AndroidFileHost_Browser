package browser.afh.fragments;

/*
 * This file is part of AFH Browser.
 *
 * AFH Browser is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AFH Browser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AFH Browser. If not, see <http://www.gnu.org/licenses/>.
 */

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

import com.afollestad.materialdialogs.color.ColorChooserDialog;

import browser.afh.MainActivity;
import browser.afh.R;

import static browser.afh.tools.Constants.TAG;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        MainActivity mainActivity = (MainActivity) getActivity();
        getPreferenceScreen().getPreference(2).setOnPreferenceClickListener(preference -> {
            mainActivity.showColourDialog(1);
            return true;
        });

        getPreferenceScreen().getPreference(3).setOnPreferenceClickListener(preference -> {
            mainActivity.showColourDialog(2);
            return true;
        });
    }
}
