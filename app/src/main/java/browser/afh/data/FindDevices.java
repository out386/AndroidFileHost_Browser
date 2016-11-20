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
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.android.volley.RequestQueue;
import com.baoyz.widget.PullRefreshLayout;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
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
import browser.afh.tools.Retrofit.ApiInterface;
import browser.afh.tools.Retrofit.RetroClient;
import browser.afh.types.Device;
import browser.afh.types.DeviceData;
import retrofit2.Call;
import retrofit2.Callback;

public class FindDevices {

    private final String TAG = Constants.TAG;
    private View rootView;
    private RequestQueue queue;
    private final PullRefreshLayout deviceRefreshLayout;
    private List<DeviceData> devices = new ArrayList<>();
    private int currentPage = 0;
    private FastItemAdapter devAdapter;
    private int pages[] = null;
    private FindFiles findFiles;
    private boolean refresh = false, morePagesRequested = false;
    private AppbarScroll appbarScroll;
    private String headerMessage;

    public FindDevices(final View rootView, final RequestQueue queue, final AppbarScroll appbarScroll) {
        this.rootView = rootView;
        this.queue = queue;
        this.appbarScroll = appbarScroll;
        deviceRefreshLayout = (PullRefreshLayout) rootView.findViewById(R.id.deviceRefresh);
        headerMessage = rootView.getContext().getResources().getString(R.string.device_list_header_text);

        devAdapter = new FastItemAdapter();
        final RecyclerView deviceRecyclerView = (RecyclerView) rootView.findViewById(R.id.deviceList);
        deviceRecyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        deviceRecyclerView.setItemAnimator(new DefaultItemAnimator());
        devAdapter.withSelectable(true);
        final StickyHeaderAdapter stickyHeaderAdapter = new StickyHeaderAdapter();
        deviceRecyclerView.setAdapter(stickyHeaderAdapter.wrap(devAdapter));
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
                    appbarScroll.setText(headerMessage);
                    appbarScroll.expand();
                    deviceRefreshLayout.setEnabled(true);
                } else {
                    deviceRefreshLayout.setEnabled(false);
                    if (scroll > 50) {
                        appbarScroll.collapse();
                        // Not needed now but will be used later. After a listener for the appbar is added
                        headerMessage = appbarScroll.getText();
                    }
                }
            }
        });

        findFiles = new FindFiles(rootView, queue);

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
                animate();
                appbarScroll.expand();
                appbarScroll.setText(rootView.getContext().getResources().getString(R.string.files_list_header_text));
                ((PullRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout)).setRefreshing(true);
                // Just in case monkeys decide to tap around while the list is refreshing
                // (List is cleared before refresh)
                if (devices.size() > position)
                    findFiles.start(devices.get(position).did);
                return true;
            }
        });

    }

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

    private void animate() {
        final CardView deviceHolder = (CardView) rootView.findViewById(R.id.deviceCardView);
        final CardView filesHolder = (CardView) rootView.findViewById(R.id.filesCardView);
        filesHolder.setVisibility(View.VISIBLE);
        filesHolder.setAlpha(0.0f);
        deviceHolder.animate()
                .setDuration(500)
                .translationX(-deviceHolder.getWidth())
                .alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        deviceHolder.setVisibility(View.GONE);
                        filesHolder.animate()
                                .setDuration(200)
                                .alpha(1.0f);
                    }
                });
    }

    private class ReadCache extends AsyncTask<Void, Void, List> {
        File cacheFile;

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
                queue.start();
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
}