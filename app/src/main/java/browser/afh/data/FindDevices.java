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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.baoyz.widget.PullRefreshLayout;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import com.turingtechnologies.materialscrollbar.AlphabetIndicator;
import com.turingtechnologies.materialscrollbar.TouchScrollBar;

import org.json.JSONArray;
import org.json.JSONObject;

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
import browser.afh.types.Device;

public class FindDevices {

    private final String TAG = Constants.TAG;
    private View rootView;
    private RequestQueue queue;
    private final PullRefreshLayout deviceRefreshLayout;
    private List<Device> devices = new ArrayList<>();
    private int currentPage = 1;
    private FastItemAdapter devAdapter;
    private int pages[];
    private FindFiles findFiles;
    private boolean refresh = false;

    public FindDevices(final View rootView, final RequestQueue queue) {
        this.rootView = rootView;
        this.queue = queue;
        deviceRefreshLayout = (PullRefreshLayout) rootView.findViewById(R.id.deviceRefresh);

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
                if (scroll == 0)
                    deviceRefreshLayout.setEnabled(true);
                else
                    deviceRefreshLayout.setEnabled(false);
            }
        });

        findFiles = new FindFiles(rootView, queue);

        deviceRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                devices.clear();
                currentPage = 1;
                refresh = true;
                findFirstDevice();
            }
        });

        devAdapter.withOnClickListener(new FastAdapter.OnClickListener<Device>() {
            @Override
            public boolean onClick(View v, IAdapter<Device> adapter, Device item, int position) {
                animate();
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
        if (!refresh) {
            File cacheFile = new File(rootView.getContext().getCacheDir().toString() + "/devicelist");
            new ReadCache(cacheFile).execute();
            return;
        }
        String url = "https://www.androidfilehost.com/api/?action=devices&page=1&limit=100";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.i(TAG, "onResponseJson: " + response);
                            processFindDevices(response);
                        } catch (Exception e) {
                            Log.i(TAG, "onResponse: " + e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.toString().contains("NoConnectionError")) {
                    deviceRefreshLayout.setRefreshing(false);
                    return;
                }
                Log.i(TAG, "onErrorResponse: " + error.toString());
                findFirstDevice();
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.start();
        queue.add(stringRequest);
    }

    private void findSubsequentDevices(final int pageNumber) {
        final String url = "https://www.androidfilehost.com/api/?action=devices&page=" + pageNumber + "&limit=100";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        currentPage++;
                        try {
                            JSONObject deviceListJson = new JSONObject(response);
                            Log.i(TAG, "onResponseSubs: " + url);
                            parseDevices(deviceListJson.getJSONArray("DATA"));
                        } catch (Exception e) {
                            currentPage--;
                            Log.i(TAG, "onResponse: " + e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.toString().contains("NoConnectionError")) {
                    deviceRefreshLayout.setRefreshing(false);
                    return;
                }
                Log.i(TAG, "onErrorResponseSubs: " + error.toString());
                findSubsequentDevices(pageNumber);
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }


    private void processFindDevices(String json) throws Exception {
        JSONObject deviceListJson = new JSONObject(json);
        String message = deviceListJson.getString("MESSAGE");
        pages = findDevicePageNumbers(message);
        parseDevices(deviceListJson.getJSONArray("DATA"));
        Log.i(TAG, "processFindDevices: " + pages[3]);
        for (int currentPage = 2; currentPage <= pages[3]; currentPage++)
            findSubsequentDevices(currentPage);

    }

    private void parseDevices(JSONArray data) throws Exception {
        if (data != null)
            for (int i = 0; i < data.length(); i++) {
                JSONObject dev = data.getJSONObject(i);
                Device device = new Device(dev.getString("did"), dev.getString("manufacturer"), dev.getString("device_name"));
                devices.add(device);
            }
        if (currentPage == pages[3]) {
            Collections.sort(devices, Comparators.byManufacturer);
            displayDevices();
        }
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
                                .setDuration(500)
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
        protected void onPostExecute(List output){
            if(output != null) {
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
}