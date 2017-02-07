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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.baoyz.widget.PullRefreshLayout;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import com.turingtechnologies.materialscrollbar.AlphabetIndicator;
import com.turingtechnologies.materialscrollbar.TouchScrollBar;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import browser.afh.R;
import browser.afh.adapters.StickyHeaderAdapter;
import browser.afh.tools.CacheList;
import browser.afh.tools.Comparators;
import browser.afh.tools.Constants;
import browser.afh.tools.Prefs;
import browser.afh.tools.Retrofit.ApiInterface;
import browser.afh.tools.Retrofit.RetroClient;
import browser.afh.types.Device;
import browser.afh.types.DeviceData;
import hugo.weaving.DebugLog;
import retrofit2.Call;
import retrofit2.Callback;

public class FindDevices {

    private final String TAG = Constants.TAG;
    private final View rootView;
    private final PullRefreshLayout deviceRefreshLayout;
    private List<DeviceData> devices = new ArrayList<>();
    private int currentPage = 0;
    private final FastItemAdapter<DeviceData> devAdapter;
    private int pages[] = null;
    private final FindFiles findFiles;
    private boolean refresh = false, morePagesRequested = false, devicesWereEmpty = true;
    private final FragmentInterface fragmentInterface;
    private final CardView deviceHolder;
    private final CardView filesHolder;
    private final AppbarScroll appbarScroll;

    private final BroadcastReceiver searchReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            devAdapter.filter(intent.getStringExtra(Constants.INTENT_SEARCH_QUERY));
        }
    };
    private final BroadcastReceiver backReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (filesHolder.getVisibility() == View.VISIBLE)
                animateShowDevices();
            else
                fragmentInterface.onSuperBack();
        }
    };

    @DebugLog
    public FindDevices(final View rootView, final AppbarScroll appbarScroll, final FragmentInterface fragmentInterface) {
        this.rootView = rootView;
        this.fragmentInterface = fragmentInterface;
        this.appbarScroll = appbarScroll;
        deviceHolder = (CardView) rootView.findViewById(R.id.deviceCardView);
        filesHolder = (CardView) rootView.findViewById(R.id.filesCardView);
        deviceRefreshLayout = (PullRefreshLayout) rootView.findViewById(R.id.deviceRefresh);

        devAdapter = new FastItemAdapter<>();
        final RecyclerView deviceRecyclerView = (RecyclerView) rootView.findViewById(R.id.deviceList);
        final Prefs prefs = new Prefs(rootView.getContext());
        deviceRecyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));

        devAdapter.withOnCreateViewHolderListener(new FastAdapter.OnCreateViewHolderListener() {
            // Required for the images toggle
            @Override
            public DeviceData.ViewHolder onPreCreateViewHolder(ViewGroup parent, int type) {
                int deviceLayoutRes;
                if (prefs.get("device_image", true))
                    deviceLayoutRes = R.layout.device_items_image;
                else
                    deviceLayoutRes = R.layout.device_items_no_image;
                return devAdapter.getTypeInstance(type).getViewHolder(
                        LayoutInflater.from(rootView.getContext()).inflate(deviceLayoutRes, parent, false)
                );
            }
            @Override
            public RecyclerView.ViewHolder onPostCreateViewHolder(RecyclerView.ViewHolder viewHolder) {
                return viewHolder;
            }
        });

        deviceRecyclerView.setItemAnimator(new DefaultItemAnimator());
        devAdapter.withSelectable(true);
        devAdapter.withPositionBasedStateManagement(false);
        final StickyHeaderAdapter stickyHeaderAdapter = new StickyHeaderAdapter();
        final StickyRecyclerHeadersDecoration decoration = new StickyRecyclerHeadersDecoration(stickyHeaderAdapter);
        deviceRecyclerView.addItemDecoration(decoration);
        TouchScrollBar materialScrollBar = new TouchScrollBar(rootView.getContext(), deviceRecyclerView, true);
        materialScrollBar.setHandleColour(ContextCompat.getColor(rootView.getContext(), R.color.accent));
        materialScrollBar.addIndicator(new AlphabetIndicator(rootView.getContext()), true);

        /* Needed to prevent PullRefreshLayout from refreshing every time someone
         * tries to scroll down. The fast scrollbar needs RecyclerView to be a child
         * of a RelativeLayout. PullRefreshLayout needs a scrollable child. That makes this
         * workaround necessary.
         */
        deviceRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int scroll = deviceRecyclerView.computeVerticalScrollOffset();
                if (scroll == 0) {
                    appbarScroll.setText(null);
                    appbarScroll.expand();
                    deviceRefreshLayout.setEnabled(true);
                } else {
                    deviceRefreshLayout.setEnabled(false);
                    if (scroll > 50) {
                        appbarScroll.collapse();
                    }
                }
            }
        });

        devAdapter.withFilterPredicate(new IItemAdapter.Predicate<DeviceData>() {
            @Override
            public boolean filter(DeviceData item, CharSequence constraint) {

                return ! (item.device_name.toUpperCase().startsWith(String.valueOf(constraint).toUpperCase())
                        || item.manufacturer.toUpperCase().startsWith(String.valueOf(constraint).toUpperCase()));
            }
        });
        findFiles = new FindFiles(rootView);

        deviceRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                devices.clear();
                currentPage = 0;
                refresh = true;
                findFirstDevice();
            }
        });

        devAdapter.withOnClickListener(new FastAdapter.OnClickListener<DeviceData>() {
            @Override
            public boolean onClick(View v, IAdapter<DeviceData> adapter, DeviceData item, int position) {
                showDevice(item.did, position);
                return true;
            }
        });

        devAdapter.withOnLongClickListener(new FastAdapter.OnLongClickListener<DeviceData>() {
            @Override
            public boolean onLongClick(View v, IAdapter<DeviceData> adapter, DeviceData item, int position) {
                new Prefs(rootView.getContext()).put("device_id", item.did);
                new Prefs(rootView.getContext()).put("device_name", item.manufacturer + " " + item.device_name);
                Snackbar.make(rootView,item.device_name+"added to shortcut",Snackbar.LENGTH_SHORT);
                return false;
            }
        });
        deviceRecyclerView.setAdapter(stickyHeaderAdapter.wrap(devAdapter));

    }

    @DebugLog
    public void registerReceiver() {
        IntentFilter search = new IntentFilter();
        search.addAction(Constants.INTENT_SEARCH);
        LocalBroadcastManager.getInstance(rootView.getContext()).registerReceiver(searchReceiver, search);
        IntentFilter back = new IntentFilter();
        back.addAction(Constants.INTENT_BACK);
        LocalBroadcastManager.getInstance(rootView.getContext()).registerReceiver(backReceiver, back);
    }

    @DebugLog
    public void unregisterReceiver() {
        LocalBroadcastManager.getInstance(rootView.getContext()).unregisterReceiver(searchReceiver);
        LocalBroadcastManager.getInstance(rootView.getContext()).unregisterReceiver(backReceiver);
    }

    @DebugLog
    public void findFirstDevice() {

        deviceRefreshLayout.setRefreshing(true);
        ApiInterface retro = RetroClient.getRetrofit().create(ApiInterface.class);
        if (!refresh) {
            File cacheFile = new File(rootView.getContext().getCacheDir().toString() + "/devicelist");
            new ReadCache(cacheFile).execute();
            return;
        }

        for (int page = 1; page <= Constants.MIN_PAGES; page++) {
            findDevices(page, retro);
        }
    }

    @DebugLog
    private void findDevices(final int pageNumber, final ApiInterface retro) {
        Log.i(TAG, "findDevices: Queueing page : " + pageNumber);
        Call<Device> call = retro.getDevices("devices", pageNumber, 100);
        call.enqueue(new Callback<Device>() {
            @Override
            public void onResponse(Call<Device> call, retrofit2.Response<Device> response) {
                Log.i(TAG, "onResponse: Page number : " + pageNumber);
                currentPage++;
                String message = response.body().message;
                Log.i(TAG, "onResponseJson: " + message);
                List<DeviceData> deviceDatas;

                if (response.body().data == null) {
                    Log.i(TAG, "NULL!");
                    return;
                }
                deviceDatas = response.body().data;
                int size = devices.size();
                if (deviceDatas != null)
                    devices.addAll(deviceDatas);
                Log.i(TAG, "onResponseJson: in devices: " + devices.get(size == 0 ? 0 : size - 1).device_name + " " + devices.size() + "elements");

                if (pages == null) {
                    pages = findDevicePageNumbers(message);
                } else {
                    if (currentPage >= pages[3]) {
                        Collections.sort(devices, Comparators.byManufacturer);
                        displayDevices();
                    } else {
                        if (!morePagesRequested) {
                            morePagesRequested = true;
                            for (int newPages = Constants.MIN_PAGES + 1; newPages <= pages[3]; newPages++)
                                findDevices(newPages, retro);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Device> call, Throwable t) {
                Log.i(TAG, "onErrorResponse: " + t.toString());
                findDevices(pageNumber, retro);
            }
        });
    }

    @DebugLog
    private void displayDevices() {
        devAdapter.add(devices);
        deviceRefreshLayout.setRefreshing(false);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                CacheList.write(devices, new File(rootView.getContext().getCacheDir().toString() + "/devicelist"));
            }
        }
        );
        t.start();
        Log.i(TAG, "parseDevices: " + devices.size());
        if(devicesWereEmpty) {
            fragmentInterface.reattach();
        }
    }

    private int[] findDevicePageNumbers(String message) {
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(message);
        int pages[] = new int[4];
        int i = 0;
        while (!m.hitEnd()) {
            if (m.find() && i < 4)
                pages[i++] = Integer.parseInt(m.group());
        }
        return pages;
    }

    @DebugLog
    private void animateShowFiles() {
        filesHolder.setTranslationX(filesHolder.getWidth());
        filesHolder.setAlpha(0.0f);
        filesHolder.setVisibility(View.VISIBLE);
        fragmentInterface.showSearch(false);
        InputMethodManager inputMethodManager = (InputMethodManager) rootView.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(rootView.getWindowToken(), 0);

        deviceHolder.animate()
                .setDuration(Constants.ANIM_DURATION)
                .translationX(-deviceHolder.getWidth())
                .alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        deviceHolder.setVisibility(View.GONE);
                        appbarScroll.setText(rootView.getContext().getResources().getString(R.string.files_list_header_text));
                    }
                });
        filesHolder.animate()
                .translationX(0)
                .setDuration(Constants.ANIM_DURATION)
                .alpha(1.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        filesHolder.setVisibility(View.VISIBLE);
                    }
                });
    }

    @DebugLog
    private void animateShowDevices() {
        Log.i(TAG, "animateShowDevices: ");
        deviceHolder.setAlpha(0.0f);
        deviceHolder.setTranslationX(-deviceHolder.getWidth());
        deviceHolder.setVisibility(View.VISIBLE);
        filesHolder.animate()
                .setDuration(Constants.ANIM_DURATION)
                .translationX(filesHolder.getWidth())
                .alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        filesHolder.setVisibility(View.GONE);
                        appbarScroll.setText(null);
                    }
                });
        deviceHolder.animate()
                .translationX(0)
                .setDuration(Constants.ANIM_DURATION)
                .alpha(1.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        deviceHolder.setVisibility(View.VISIBLE);
                        findFiles.reset();
                        fragmentInterface.showSearch(true);
            }
        });
    }

    public void showDevice(String did, int position) {
        if (did == null) {
            Log.i(TAG, "showDevice: Invalid device selected");
            return;
        }
        animateShowFiles();
        appbarScroll.expand();
        appbarScroll.setText(rootView.getContext().getResources().getString(R.string.files_list_header_text));
        ((PullRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout)).setRefreshing(true);
        // Just in case monkeys decide to tap around while the list is refreshing
        if (devices.size() > position)
            findFiles.start(did);
    }

    @DebugLog
    private class ReadCache extends AsyncTask<Void, Void, List> {
        final File cacheFile;

        ReadCache(File cacheFile) {
            this.cacheFile = cacheFile;
        }

        @Override
        public List doInBackground(Void... v) {
            return CacheList.read(cacheFile);
        }

        @Override
        protected void onPostExecute(List output) {
            if (output != null) {
                devices = output;
                devicesWereEmpty = false;
                displayDevices();
            } else {
                deviceRefreshLayout.setRefreshing(true);
                refresh = true;
                findFirstDevice();
            }
        }
    }

    public interface AppbarScroll {
        void expand();
        void collapse();
        void setText(String text);
        String getText();
    }

    public interface FragmentInterface {
        void reattach();
        void onSuperBack();
        void showSearch(boolean show);
    }

}