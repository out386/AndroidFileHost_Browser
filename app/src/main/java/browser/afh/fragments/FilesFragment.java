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

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import browser.afh.MainActivity;
import browser.afh.R;
import browser.afh.data.FindFiles;
import browser.afh.tools.Constants;

public class FilesFragment extends Fragment {
    private FindFiles findFiles;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.files_fragment, container, false);
        MainActivity activity = (MainActivity) getActivity();
        activity.setText(getResources().getString(R.string.files_list_header_text));
        activity.expand();
        findFiles = new FindFiles(rootView, activity);
        Bundle bundle = getArguments();

        if (bundle != null) {
            String did = bundle.getString(Constants.EXTRA_DEVICE_ID, null);
            findFiles.start(did);
        }
        return rootView;
    }

    @Override
    public void onDestroyView() {
        findFiles.reset();
        super.onDestroyView();
    }
}
