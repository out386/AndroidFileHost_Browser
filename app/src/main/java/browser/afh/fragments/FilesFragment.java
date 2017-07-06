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
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import browser.afh.MainActivity;
import browser.afh.R;
import browser.afh.data.FindFiles;
import browser.afh.tools.Constants;
import browser.afh.types.Files;

public class FilesFragment extends Fragment {
    private FindFiles findFiles;
    private ArrayList<Files> filesD;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            filesD = (ArrayList<Files>) savedInstanceState.getSerializable(Constants.KEY_FILES_LIST);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.files_fragment, container, false);
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.setText(getResources().getString(R.string.files_list_header_text));
        mainActivity.expand();
        mainActivity.showSearch(false, true);
        findFiles = new FindFiles(rootView, mainActivity);
        Bundle bundle = getArguments();

        if (filesD != null) {
            findFiles.setList(filesD);
            filesD = null;
        } else if (bundle != null) {
            String did = bundle.getString(Constants.EXTRA_DEVICE_ID, null);
            findFiles.start(did);
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        ArrayList<Files> out = new ArrayList<>();

        for (Files f : findFiles.getFiles()) {
            Files files2 = new Files();

            files2.name = f.name;
            files2.url = f.url;
            files2.file_size = f.file_size;
            files2.upload_date = f.upload_date;
            files2.screenname = f.screenname;
            files2.downloads = f.downloads;

            out.add(files2);
        }

        outState.putSerializable(Constants.KEY_FILES_LIST, out);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        findFiles.reset();
        super.onDestroyView();
    }
}
