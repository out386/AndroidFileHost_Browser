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
    public static final String KEY_FILES_RETAIN_FRAGMENT = "retainFragment";
    private FindFiles findFiles;
    private ArrayList<Files> filesD;
    private FilesRetainFragment filesRetainFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        filesRetainFragment = (FilesRetainFragment) getFragmentManager()
                .findFragmentByTag(KEY_FILES_RETAIN_FRAGMENT);
        if (filesRetainFragment == null) {
            filesRetainFragment = new FilesRetainFragment();
            getFragmentManager()
                    .beginTransaction()
                    .add(filesRetainFragment, KEY_FILES_RETAIN_FRAGMENT)
                    .commit();
        } else {
            filesD = filesRetainFragment.getFiles();
        }
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

        if (filesD != null && filesD.size() > 0) {
            findFiles.setList(filesD);
            filesD = null;
        } else if (bundle != null) {
            String did = bundle.getString(Constants.EXTRA_DEVICE_ID, null);
            findFiles.start(did);
        }
        return rootView;
    }

    @Override
    public void onDestroyView() {
        filesRetainFragment = (FilesRetainFragment) getFragmentManager()
                .findFragmentByTag(KEY_FILES_RETAIN_FRAGMENT);
        if (filesRetainFragment != null) {
            ArrayList<Files> f = new ArrayList<>();
            for (Files file : findFiles.getFiles()) {
                Files fi = new Files();
                fi.downloads = file.downloads;
                fi.file_size = file.file_size;
                fi.name = file.name;
                fi.screenname = file.screenname;
                fi.url = file.url;
                fi.upload_date = file.upload_date;
                fi.upload_date_long = file.upload_date_long;
                f.add(fi);
            }
            filesRetainFragment.setFiles(f);
        }
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        findFiles.reset();
        super.onDestroy();
    }
}
