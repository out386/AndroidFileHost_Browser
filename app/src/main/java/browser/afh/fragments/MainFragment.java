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

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import browser.afh.data.FindDevices;
import browser.afh.data.FindDevices.AppbarScroll;
import browser.afh.data.FindDevices.FragmentRattach;
import browser.afh.R;
import browser.afh.tools.Constants;
import browser.afh.tools.VolleySingleton;

public class MainFragment extends Fragment {
    View rootView;
    AppbarScroll appbarScroll;
    FragmentRattach fragmentRattach;
    private FindDevices findDevices;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            appbarScroll = (AppbarScroll) activity;
            fragmentRattach = (FragmentRattach) activity;
        } catch (ClassCastException e) {

        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.main_fragment, container, false);
        findDevices = new FindDevices(rootView, VolleySingleton.getInstance(getActivity()).getRequestQueue(), appbarScroll, fragmentRattach);
        findDevices.findFirstDevice();
        return rootView;
    }

    @Override
    public void onResume() {
        findDevices.registerReceiver();
        super.onResume();
    }

    @Override
    public void onPause() {
        findDevices.unregisterReceiver();
        super.onPause();
    }
}
