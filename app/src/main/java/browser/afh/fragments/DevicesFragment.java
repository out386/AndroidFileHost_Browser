/*
 * Copyright (C) 2016 Ritayan Chakraborty (out386) and Harsh Shandilya (MSF-Jarvis)
 *
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

package browser.afh.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import browser.afh.MainActivity;
import browser.afh.R;
import browser.afh.data.FindDevices;
import browser.afh.interfaces.DevicesSearchInterface;

public class DevicesFragment extends Fragment {
    private MainActivity activity;
    private FindDevices findDevices;
    private DevicesSearchInterface dsi;
    private String searchQuery;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity) activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dsi = query -> searchQuery = query;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_fragment, container, false);
        activity.showSearch(true, true);

        findDevices = new FindDevices();
        findDevices.initialize(rootView, activity, dsi);
        findDevices.findFirstDevice();
        return rootView;
    }

    @Override
    public void onResume() {
        findDevices.resume(searchQuery);
        super.onResume();
    }

    @Override
    public void onPause() {
        findDevices.unregisterReceiver();
        super.onPause();
    }
}
