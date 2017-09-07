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

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baoyz.widget.PullRefreshLayout;
import com.crashlytics.android.Crashlytics;
import com.google.gson.JsonSyntaxException;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.GenericItemAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import com.turingtechnologies.materialscrollbar.AlphabetIndicator;
import com.turingtechnologies.materialscrollbar.TouchScrollBar;

import java.net.UnknownHostException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import browser.afh.R;
import browser.afh.interfaces.DevicesSearchInterface;
import browser.afh.recycler.DeviceItem;
import browser.afh.recycler.StickyHeaderAdapter;
import browser.afh.tools.Comparators;
import browser.afh.tools.Constants;
import browser.afh.tools.Prefs;
import browser.afh.tools.Retrofit.ApiInterface;
import browser.afh.tools.Retrofit.RetroClient;
import browser.afh.tools.Utils;
import browser.afh.types.AfhDevices;
import hugo.weaving.DebugLog;
import retrofit2.Call;
import retrofit2.Callback;

public class FindDevices {

    private final String TAG = Constants.TAG;
    private View rootView;
    private PullRefreshLayout deviceRefreshLayout;
    private GenericItemAdapter<AfhDevices.Device, DeviceItem> devAdapter;
    private List<AfhDevices.Device> devices;
    private int currentPage = 0;
    private FastAdapter<DeviceItem> fastAdapter;
    private int pages[] = null;
    private boolean morePagesRequested = false;
    private FragmentInterface fragmentInterface;
    private AppbarScroll appbarScroll;
    private HSShortutInterface hsShortutInterface;
    private ApiInterface retro;
    private DevicesSearchInterface dsi;
    private final BroadcastReceiver searchReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String searchQuery;
            searchQuery = intent.getStringExtra(Constants.EXTRA_SEARCH_QUERY);
            devAdapter.filter(searchQuery);
            dsi.filter(searchQuery);
        }
    };

    @DebugLog
    public void initialize(final View rootView, Activity activity, DevicesSearchInterface dsi) {
        this.rootView = rootView;
        try {
            this.fragmentInterface = (FragmentInterface) activity;
            this.appbarScroll = (AppbarScroll) activity;
            this.hsShortutInterface = (HSShortutInterface) activity;
        } catch (ClassCastException e) {
            Log.e(TAG, "FindDevices: ", e);
        }

        this.dsi = dsi;
        devices = Collections.synchronizedList(new LinkedList<AfhDevices.Device>());
        deviceRefreshLayout = rootView.findViewById(R.id.deviceRefresh);

        fastAdapter = new FastAdapter<>();
        devAdapter = new GenericItemAdapter<>(DeviceItem.class, AfhDevices.Device.class);
        final RecyclerView deviceRecyclerView = rootView.findViewById(R.id.deviceList);
        final Prefs prefs = new Prefs(rootView.getContext());
        deviceRecyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));

        fastAdapter.withOnCreateViewHolderListener(new FastAdapter.OnCreateViewHolderListener() {
            // Required for the images toggle
            @Override
            public DeviceItem.ViewHolder onPreCreateViewHolder(ViewGroup parent, int type) {
                int deviceLayoutRes;
                if (prefs.get("device_image", true))
                    deviceLayoutRes = R.layout.device_items_image;
                else
                    deviceLayoutRes = R.layout.device_items_no_image;
                return fastAdapter.getTypeInstance(type).getViewHolder(
                        LayoutInflater.from(rootView.getContext()).inflate(deviceLayoutRes, parent, false)
                );
            }

            @Override
            public RecyclerView.ViewHolder onPostCreateViewHolder(RecyclerView.ViewHolder viewHolder) {
                return viewHolder;
            }
        });

        deviceRecyclerView.setItemAnimator(new DefaultItemAnimator());
        fastAdapter.withSelectable(true);
        fastAdapter.withPositionBasedStateManagement(false);

        final StickyHeaderAdapter stickyHeaderAdapter = new StickyHeaderAdapter();
        final StickyRecyclerHeadersDecoration decoration = new StickyRecyclerHeadersDecoration(stickyHeaderAdapter);
        deviceRecyclerView.addItemDecoration(decoration);
        TouchScrollBar materialScrollBar = new TouchScrollBar(rootView.getContext(), deviceRecyclerView, true);
        materialScrollBar.setHandleColour(Utils.getPrefsColour(2, rootView.getContext()));
        materialScrollBar.addIndicator(new AlphabetIndicator(rootView.getContext()), true);

        /* Needed to prevent PullRefreshLayout from refreshing every time someone
         * tries to scroll up. The fast scrollbar needs RecyclerView to be a child
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

        devAdapter.withFilterPredicate((DeviceItem item, CharSequence constraint) ->
                !(item.getModel().device_name.toUpperCase().contains(String.valueOf(constraint).toUpperCase())
                        || item.getModel().manufacturer.toUpperCase().startsWith(String.valueOf(constraint).toUpperCase()))
        );

        retro = RetroClient.getApi(rootView.getContext(), true);

        deviceRefreshLayout.setOnRefreshListener(() -> {
                    devices.clear();
                    devAdapter.clear();
                    currentPage = 0;
                    morePagesRequested = false;
                    pages = null;
                    retro = RetroClient.getApi(rootView.getContext(), false);
                    findFirstDevice();
                }
        );

        fastAdapter.withOnClickListener((View v, IAdapter<DeviceItem> adapter, DeviceItem item, int position) -> {
                    fragmentInterface.showDevice(item.getModel().did, position);
                    return true;
                }
        );

        fastAdapter.withOnLongClickListener((View v, IAdapter<DeviceItem> adapter, DeviceItem item, int position) -> {
                    AfhDevices.Device model = item.getModel();
                    new Prefs(rootView.getContext()).put(Constants.EXTRA_DEVICE_ID, model.did);
                    new Prefs(rootView.getContext()).put("device_name", model.manufacturer + " " + model.device_name);
                    if (model.did != null && model.device_name != null)
                        hsShortutInterface.setShortcut(model.did, model.manufacturer, model.device_name);

                    Snackbar.make(rootView,
                            String.format(
                                    rootView.getContext().getResources().getString(R.string.device_list_add_qs_text),
                                    model.device_name),
                            Snackbar.LENGTH_LONG).show();

                    return true;
                }
        );

        deviceRecyclerView.setAdapter(stickyHeaderAdapter.wrap(devAdapter.wrap(fastAdapter)));
    }

    @DebugLog
    public void resume(String searchQuery) {
        IntentFilter search = new IntentFilter();
        search.addAction(Constants.INTENT_SEARCH);
        LocalBroadcastManager.getInstance(rootView.getContext())
                .registerReceiver(searchReceiver, search);
        if (searchQuery != null && !"".equals(searchQuery)) {
            new Handler().postDelayed(() ->
                    devAdapter.filter(searchQuery),
                    250);
        }
    }

    @DebugLog
    public void unregisterReceiver() {
        LocalBroadcastManager.getInstance(rootView.getContext()).unregisterReceiver(searchReceiver);
    }

    @DebugLog
    public void findFirstDevice() {
        deviceRefreshLayout.setRefreshing(true);

        for (int page = 1; page <= Constants.MIN_PAGES; page++) {
            findDevices(page, retro);
        }
    }

    @DebugLog
    private void findDevices(final int pageNumber, final ApiInterface retro) {
        Log.i(TAG, "findDevices: Queueing page : " + pageNumber);
        Call<AfhDevices> call = retro.getDevices("devices", pageNumber, 100);
        call.enqueue(new Callback<AfhDevices>() {
            @Override
            public void onResponse(Call<AfhDevices> call, retrofit2.Response<AfhDevices> response) {
                currentPage++;
                if (response.isSuccessful()) {
                    String message = null;

                    // Should check if response is null here, but we want to get stack traces
                    // if something else goes wrong, as the server specs are still a mystery.
                    try {
                        message = response.body().message;
                    } catch (NullPointerException e) {
                        Crashlytics.logException(e);
                        Crashlytics.log("Page number : " + pageNumber);
                        // No point in proceeding if response.body is null
                        deviceRefreshLayout.setRefreshing(false);
                        return;
                    }

                    if (response.body().data == null) {
                        deviceRefreshLayout.setRefreshing(false);
                        return;
                    }

                    List<AfhDevices.Device> deviceDatas = response.body().data;
                    devices.addAll(deviceDatas);

                    if (pages == null) {
                        pages = findDevicePageNumbers(message);
                    } else {
                        if (currentPage >= pages[3]) {
                            synchronized (devices) {
                                Collections.sort(devices, Comparators.byManufacturer);
                            }
                            displayDevices();
                        } else {
                            if (!morePagesRequested) {
                                morePagesRequested = true;
                                for (int newPages = Constants.MIN_PAGES + 1; newPages <= pages[3]; newPages++)
                                    findDevices(newPages, retro);
                            }
                        }
                    }
                } else if (response.code() == 502) {
                    // Keeps happening for some devices, re-queuing probably won't help, though
                    call.clone().enqueue(this);
                }
            }

            @Override
            public void onFailure(Call<AfhDevices> call, Throwable t) {
                if (!(t instanceof UnknownHostException) && !(t instanceof JsonSyntaxException)) {
                    Log.i(TAG, "onErrorResponse: " + t.toString());
                    call.clone().enqueue(this);
                }
            }
        });
    }

    @DebugLog
    private synchronized void displayDevices() {
        devAdapter.addModel(devices);
        deviceRefreshLayout.setRefreshing(false);
        retro = RetroClient.getApi(rootView.getContext(), true);
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

    public interface AppbarScroll {
        void expand();

        void collapse();

        String getText();

        void setText(String text);
    }

    public interface FragmentInterface {
        void showDevice(String did, int position);
    }

    //Home screen shortcut for favourite device
    public interface HSShortutInterface {
        void setShortcut(String did, String manufacturer, String deviceName);
    }
}