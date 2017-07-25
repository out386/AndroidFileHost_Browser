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

package browser.afh.data;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;

import com.baoyz.widget.PullRefreshLayout;
import com.crashlytics.android.Crashlytics;
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
import browser.afh.types.AfhFolders;
import browser.afh.types.Files;
import hugo.weaving.DebugLog;
import retrofit2.Call;
import retrofit2.Callback;

public class FindFiles {
    private final PullRefreshLayout pullRefreshLayout;
    private final String TAG = Constants.TAG;
    private final SimpleDateFormat sdf;
    private final View rootView;
    private ArrayList<Files> filesD = new ArrayList<>();
    private AfhAdapter adapter;
    private String savedID;
    private boolean sortByDate;
    private ApiInterface retroApi;
    private Context mContext;
    private Intent snackbarIntent = new Intent(Constants.INTENT_SNACKBAR);
    private ListView fileList;
    private CheckBox sortCB;

    @DebugLog
    public FindFiles(final View rootView, Context context) {
        this.rootView = rootView;
        mContext = context;
        sdf = new SimpleDateFormat("yyyy/MM/dd, HH:mm", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getDefault());
        retroApi = RetroClient.getApi(rootView.getContext(), true);
        fileList = rootView.findViewById(R.id.list);
        sortCB = rootView.findViewById(R.id.sortCB);
        Utils.tintCheckbox(sortCB, mContext);

        sortCB.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
                    sortByDate = isChecked;
                    print(true);
                }
        );
        pullRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        pullRefreshLayout.setOnRefreshListener(() -> {
                    filesD.clear();
                    adapter.notifyDataSetChanged();
                    retroApi = RetroClient.getApi(rootView.getContext(), false);
                    start(savedID);
                }
        );
        adapter = new AfhAdapter(rootView.getContext(), R.layout.afh_items, filesD);
        fileList.setAdapter(adapter);
    }

    @DebugLog
    public void start(final String did) {
        savedID = did;
        pullRefreshLayout.setRefreshing(true);
        Call<AfhDevelopers> call = retroApi.getDevelopers("developers", did, 100);
        call.enqueue(new Callback<AfhDevelopers>() {
            @Override
            public void onResponse(Call<AfhDevelopers> call, retrofit2.Response<AfhDevelopers> response) {
                List<AfhDevelopers.Developer> fid;
                if (response.isSuccessful()) {
                    try {
                        fid = response.body().data;
                    } catch (Exception e) {
                        //try-catch needed to log with Fabric
                        Crashlytics.log("did : " + did);
                        Crashlytics.logException(e);
                        Crashlytics.log("did : " + did);

                        // Files might exist, we just didn't get a list of them.
                        // Should probably retry, though.
                        showSnackbar(R.string.files_list_no_files_text);
                        return;
                    }
                    if (fid != null && fid.size() > 0) {
                        queryDirs(fid);
                    } else {
                        pullRefreshLayout.setRefreshing(false);
                        showSnackbar(R.string.files_list_no_files_text);
                    }
                } else if (response.code() == 502) {
                    // Keeps happening for some devices, suspected for files, too. Re-queuing probably won't help, though.
                    // Let's get to know if it happens to files, too.
                    try {
                        throw new IllegalArgumentException();
                    } catch (Exception e) {
                        // Have to catch Exception, Crashlytics doesn't seem to want to know specifics.
                        if (!BuildConfig.DEBUG) {
                            Crashlytics.logException(e);
                            Crashlytics.log("did : " + did);
                        }
                    }
                    call.clone().enqueue(this);
                    showSnackbar(R.string.files_list_502_text);
                } else {
                    try {
                        // Crashlytics sure loves getting stuff thrown at it.
                        throw new IllegalArgumentException();
                    } catch (Exception e) {
                        // Have to catch Exception, Crashlytics doesn't seem to want to know specifics.
                        Crashlytics.logException(e);
                        Crashlytics.log("Error code : " + response.code());
                        Crashlytics.log("did : " + did);
                    }
                }
            }

            @Override
            public void onFailure(Call<AfhDevelopers> call, Throwable t) {
                if (t instanceof UnknownHostException) {
                    showSnackbar(R.string.files_list_no_cache_text);
                    pullRefreshLayout.setRefreshing(false);
                    return;
                }
                if (!(t instanceof JsonSyntaxException)
                        && !t.toString().contains("Canceled"))
                    call.clone().enqueue(this);
            }
        });
    }

    @DebugLog
    private void queryDirs(final List<AfhDevelopers.Developer> did) {

        for (final AfhDevelopers.Developer url : did) {

            Call<AfhFolders> call = retroApi.getFolderContents("folder", url.flid, 100);
            call.enqueue(new Callback<AfhFolders>() {
                @Override
                public void onResponse(Call<AfhFolders> call, retrofit2.Response<AfhFolders> response) {
                    List<Files> filesList = null;
                    List<AfhDevelopers.Developer> foldersList = null;
                    if (response.isSuccessful()) {
                        try {
                            filesList = response.body().data.files;
                            foldersList = response.body().data.folders;
                        } catch (Exception e) {
                            Crashlytics.logException(e);
                            Crashlytics.log("flid : " + url.flid);
                        }

                        if (filesList != null && filesList.size() > 0) {
                            pullRefreshLayout.setRefreshing(false);

                            for (Files file : filesList) {
                                file.screenname = url.screenname;

                                try {
                                    file.file_size = Utils.sizeFormat(Integer.parseInt(file.file_size));
                                    file.upload_date = sdf.format(new Date(Integer.parseInt(file.upload_date) * 1000L));
                                } catch (Exception e) {
                                    Crashlytics.logException(e);
                                    Crashlytics.log("flid : " + url.flid);
                                    Crashlytics.log("name : " + file.name);
                                    Crashlytics.log("file_size : " + file.file_size);
                                    Crashlytics.log("upload_date : " + file.upload_date);
                                }

                                if (BuildConfig.PLAY_COMPATIBLE) {
                                    if (file.name.endsWith(".apk") || file.name.endsWith(".APK")) {
                                        // Filtering out APK files as Google Play hates them
                                        // But but but...!
                                        continue;
                                    }
                                }

                                if (BuildConfig.PLAY_COMPATIBLE) {
                                /* Attempting to filter out private files, which typically get less than 10 downloads
                                * This will also hide all newly uploaded files, sorry.
                                * Getting complaints of pissed off devs having their private builds passed around.
                                */
                                    if (file.downloads < 10)
                                        continue;
                                }

                                filesD.add(file);
                            }
                            print(false);
                        }

                        if (foldersList != null && foldersList.size() > 0) {
                            for (AfhDevelopers.Developer folder : foldersList) {
                                folder.screenname = url.screenname;
                            }
                            queryDirs(foldersList);
                        }
                    } else if (response.code() == 502) {
                        // Keeps happening for some devices, suspected for files, too. Re-queuing probably won't help, though.
                        // Let's get to know if it happens to files, too.
                        try {
                            throw new IllegalArgumentException();
                        } catch (Exception e) {
                            // Have to catch Exception, Crashlytics doesn't seem to want to know specifics.
                            if (!BuildConfig.DEBUG) {
                                Crashlytics.logException(e);
                                Crashlytics.log("flid : " + url.flid);
                            }
                        }
                        call.clone().enqueue(this);
                    } else {
                        try {
                            // Crashlytics sure loves getting stuff thrown at it.
                            throw new IllegalArgumentException();
                        } catch (Exception e) {
                            // Have to catch Exception, Crashlytics doesn't seem to want to know specifics.
                            Crashlytics.logException(e);
                            Crashlytics.log("Error code : " + response.code());
                            Crashlytics.log("flid : " + url.flid);
                        }
                    }
                }

                @Override
                public void onFailure(Call<AfhFolders> call, Throwable t) {

                    pullRefreshLayout.setRefreshing(false);
                    // AfhFolders.DATA will be an Object, but if it is empty, it'll be an array
                    if (!(t instanceof UnknownHostException)
                            && !(t instanceof IllegalStateException)
                            && !(t instanceof JsonSyntaxException)
                            && !t.toString().contains("Canceled")) {
                        Log.i(TAG, "onErrorResponse dirs " + t.toString());
                    } else if (t instanceof UnknownHostException) {
                        showSnackbar(R.string.files_list_no_cache_text);
                    }
                }
            });
        }

    }

    @DebugLog
    private void print(boolean isRestore) {
        if (sortByDate) {
            Collections.sort(filesD, Comparators.byUploadDate);
        } else {
            Collections.sort(filesD, Comparators.byFileName);
        }
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "New Files: Files changed : " + filesD.size() + " items");
        }
        if (isRestore) {
            AfhAdapter adapter = new AfhAdapter(rootView.getContext(), R.layout.afh_items, filesD);
            fileList.setAdapter(adapter);
        } else
            adapter.notifyDataSetChanged();
    }

    public void reset() {
        RetroClient.cancelRequests();
        retroApi = RetroClient.getApi(rootView.getContext(), true);
        filesD.clear();
        adapter.clear();
    }

    private void showSnackbar(int messageRes) {
        snackbarIntent.removeExtra(Constants.EXTRA_SNACKBAR_MESSAGE);
        snackbarIntent.putExtra(Constants.EXTRA_SNACKBAR_MESSAGE, messageRes);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(snackbarIntent);
    }

    public ArrayList<Files> getFiles() {
        return filesD;
    }

    public void setList(ArrayList<Files> list) {
        filesD = list;
        print(true);
    }
}
