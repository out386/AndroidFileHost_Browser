package browser.afh.utils;

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

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.baoyz.widget.PullRefreshLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import browser.afh.R;
import browser.afh.adapters.AfhAdapter;
import browser.afh.tools.Comparators;
import browser.afh.tools.Constants;
import browser.afh.types.AfhFiles;

public class FindFiles {
    private final TextView mTextView;
    private String json = "";
    private final ScrollView sv;
    private List<AfhFiles> filesD = new ArrayList<>();
    private AfhAdapter adapter;
    private final PullRefreshLayout pullRefreshLayout;
    private String savedID;
    private boolean sortByDate;
    private final RequestQueue queue;
    private final Context context;
    private final String TAG = Constants.TAG;

    FindFiles(View rootView, RequestQueue queue) {
        Log.d(TAG,"Inside FindFiles");
        this.queue = queue;
        context = rootView.getContext();

        mTextView = (TextView) rootView.findViewById(R.id.tv);
        sv = (ScrollView) rootView.findViewById(R.id.tvSv);
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
                filesD = new ArrayList<>();
                start(savedID);
            }
        });
        adapter = new AfhAdapter(context, R.layout.afh_items, filesD);
        fileList.setAdapter(adapter);
    }

    void start(final String did) {
        Log.d(TAG,"Starting da thingz!");
        savedID = did;
        String url = String.format(Constants.DID, did);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG,"inside onReponse");
                        json = response;
                        List<String> fid = null;
                        try {
                            sv.setVisibility(View.GONE);
                            fid = parse();
                        } catch (Exception e) {
                            pullRefreshLayout.setRefreshing(false);
                            sv.setVisibility(View.VISIBLE);
                            mTextView.setText(String.format(context.getString(R.string.json_parse_error),  e.toString()));
                        }
                        sv.setVisibility(View.GONE);
                        if(fid != null)
                            queryDirs(fid);


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.toString().contains("NoConnectionError")) {
                    pullRefreshLayout.setRefreshing(false);
                    return;
                }
                sv.setVisibility(View.VISIBLE);
                mTextView.setText(error.toString());
                start(did);
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }

    private void queryDirs(List<String> did) {
        Log.d(TAG,"querying dirs");

        for (String url : did) {
            final String link = url;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                parseFiles(response);
                            } catch (Exception e) {
                                pullRefreshLayout.setRefreshing(false);
                                sv.setVisibility(View.VISIBLE);
                                mTextView.setText(mTextView.getText().toString() + "\n\n\n" + context.getString(R.string.json_parse_error) + link + " " + e.toString());
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pullRefreshLayout.setRefreshing(false);
                    if (error.toString().contains("NoConnectionError"))
                        return;
                    sv.setVisibility(View.VISIBLE);
                    mTextView.setText(mTextView.getText().toString() + "\n\n\n" + link + "  :   " + error.toString());
                }
            });

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(stringRequest);

        }

    }

    private List<String> parse() throws Exception {
        Log.d(TAG,"Jobless so parsing shit");
        JSONObject afhJson;
        afhJson = new JSONObject(json);
        mTextView.setText("");
        List<String> fid = new ArrayList<>();
        JSONArray data = afhJson.getJSONArray("DATA");
        for (int i = 0; i < data.length(); i++) {
            fid.add(String.format(Constants.FLID, data.getJSONObject(i).getString(context.getString(R.string.flid_key))));
        }
        return fid;
    }

    private void print() {
        Log.d(TAG,"Why the fuck we printing crap?");
        if(sortByDate) {
            Collections.sort(filesD, Comparators.byUploadDate);
        } else {
            Collections.sort(filesD, Comparators.byFileName);
        }
        Log.i(Constants.TAG, "New Files: Data changed : " + filesD.size() + " items");
        adapter.notifyDataSetChanged();

    }

    private void parseFiles(String Json) throws Exception {
        Log.d(TAG,"Bored so parse some files, k?");
        JSONObject fileJson = new JSONObject(Json);

        JSONObject data;
        if(fileJson.isNull("DATA"))
            return;

        // Data will be an Object, but if it is empty, it'll be an Array
        Object dataObj = fileJson.get("DATA");
        if(! (dataObj instanceof JSONObject))
            return;
        data = (JSONObject) dataObj;

        JSONArray files = null;
        if(! data.isNull("files"))
            files = data.getJSONArray("files");
        if(files != null) {
            for (int i = 0; i < files.length(); i++) {
                JSONObject file = files.getJSONObject(i);
                String name = file.getString("name");
                String url = file.getString("url");
                String upload_date = file.getString("upload_date");
                filesD.add(new AfhFiles(name, url, upload_date));
            }
        }
        JSONArray folders = null;
        if(! data.isNull("folders"))
            folders = data.getJSONArray("folders");

        if(folders != null) {
            List<String> foldersD = new ArrayList<>();
            for (int i = 0; i < folders.length(); i++) {
                foldersD.add("https://www.androidfilehost.com/api/?action=folder&flid=" + folders.getJSONObject(i).getString("flid"));
            }
            if(foldersD.size() > 0)
                queryDirs(foldersD);
        }
        print();
    }

}
