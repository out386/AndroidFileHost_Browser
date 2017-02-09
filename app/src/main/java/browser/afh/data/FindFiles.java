package browser.afh.data;

/*
 * Copyright (C) 2016 Ritayan Chakraborty (out386) and Harsh Shandilya (MSF-Jarvis)
 */
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

import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;

import com.baoyz.widget.PullRefreshLayout;
import com.google.gson.JsonSyntaxException;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import browser.afh.BuildConfig;
import browser.afh.R;
import browser.afh.adapters.AfhAdapter;
import browser.afh.tools.Comparators;
import browser.afh.tools.Constants;
import browser.afh.tools.Retrofit.ApiInterface;
import browser.afh.tools.Retrofit.RetroClient;
import browser.afh.tools.Utils;
import browser.afh.types.AfhDevelopers;
import browser.afh.types.AfhDevelopersList;
import browser.afh.types.AfhFiles;
import browser.afh.types.AfhFolderContentResponse;
import hugo.weaving.DebugLog;
import retrofit2.Call;
import retrofit2.Callback;

class FindFiles {
    private final PullRefreshLayout pullRefreshLayout;
    private final String TAG = Constants.TAG;
    private final SimpleDateFormat sdf;
    private List<AfhFiles> filesD = new ArrayList<>();
    private AfhAdapter adapter;
    private String savedID;
    private boolean sortByDate;
    private ApiInterface retro;

    @DebugLog
    FindFiles(View rootView) {

        sdf = new SimpleDateFormat("yyyy/MM/dd, HH:mm", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getDefault());
        retro = new RetroClient().getRetrofit(rootView.getContext(), false).create(ApiInterface.class);
        ListView fileList = (ListView) rootView.findViewById(R.id.list);
        CheckBox sortCB = (CheckBox) rootView.findViewById(R.id.sortCB);

        sortCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    sortByDate = true;
                    Collections.sort(filesD,Comparators.byUploadDate);
                } else {
                    sortByDate = false;
                    Collections.sort(filesD,Comparators.byFileName);
                }
                adapter.notifyDataSetChanged();
            }
        });
        pullRefreshLayout = (PullRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                filesD.clear();
                adapter.notifyDataSetChanged();
                start(savedID);
            }
        });
        adapter = new AfhAdapter(rootView.getContext(), R.layout.afh_items, filesD);
        fileList.setAdapter(adapter);
    }

    @DebugLog
    void start(final String did) {
        savedID = did;
        Call<AfhDevelopersList> call = retro.getDevelopers("developers", did, 100);
        call.enqueue(new Callback<AfhDevelopersList>() {
                         @Override
                         public void onResponse(Call<AfhDevelopersList> call, retrofit2.Response<AfhDevelopersList> response) {
                             List<AfhDevelopers> fid = response.body().data;
                             if (fid != null && fid.size() > 0) {
                                 queryDirs(fid);
                             }
                         }
            @Override
            public void onFailure(Call<AfhDevelopersList> call, Throwable t) {
                if (! (t instanceof UnknownHostException) && ! (t instanceof JsonSyntaxException))
                    start(did);
            }
        });
    }

    @DebugLog
    private void queryDirs(final List<AfhDevelopers> did) {

        for (final AfhDevelopers url : did) {

            Call<AfhFolderContentResponse> call = retro.getFolderContents("folder", url.flid, 100);
            call.enqueue(new Callback<AfhFolderContentResponse>() {
                @Override
                public void onResponse(Call<AfhFolderContentResponse> call, retrofit2.Response<AfhFolderContentResponse> response) {
                    List<AfhFiles> filesList = response.body().data.files;
                    List<AfhDevelopers> foldersList = response.body().data.folders;

                    if (filesList != null && filesList.size() > 0) {
                        pullRefreshLayout.setRefreshing(false);

                        for (AfhFiles file : filesList) {
                            file.screenname = url.screenname;
                            file.file_size = Utils.sizeFormat(Integer.parseInt(file.file_size));
                            file.upload_date = sdf.format(new Date(Integer.parseInt(file.upload_date) * 1000L));

                            if (BuildConfig.PLAY_COMPATIBLE) {
                                if (file.name.endsWith(".apk") || file.name.endsWith(".APK")) {
                                    // Filtering out APK files as Google Play hates them
                                    continue;
                                }
                            }

                            if (BuildConfig.ANGRY_DEVS) {
                                /* Attempting to filter out private files, which typically get less than 10 downloads
                                * This will also hide all newly uploaded files, sorry.
                                * Getting complaints of pissed off devs having their private builds passed around.
                                */
                                if (file.downloads < 10)
                                    continue;
                            }
                            
                            filesD.add(file);
                        }
                        print();
                    }

                    if (foldersList != null && foldersList.size() > 0) {
                        for (AfhDevelopers folder : foldersList) {
                            folder.screenname = url.screenname;
                        }
                            queryDirs(foldersList);
                    }
                }

                @Override
                public void onFailure(Call<AfhFolderContentResponse> call, Throwable t) {
                    // AfhFolderContentResponse.DATA will be an Object, but if it is empty, it'll be an array
                    if (! (t instanceof UnknownHostException) && ! (t instanceof IllegalStateException) && ! (t instanceof JsonSyntaxException)) {
                        Log.i(TAG, "onErrorResponse dirs " + t.toString());
                        pullRefreshLayout.setRefreshing(false);
                    }
                }
            });
        }

    }

    @DebugLog
    private void print() {
        if(sortByDate) {
            Collections.sort(filesD, Comparators.byUploadDate);
        } else {
            Collections.sort(filesD, Comparators.byFileName);
        }
        Log.i(TAG, "New Files: Data changed : " + filesD.size() + " items");
        adapter.notifyDataSetChanged();

    }

    void reset() {
        filesD.clear();
        print();
    }
}
