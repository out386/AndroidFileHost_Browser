package out386.afh;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.baoyz.widget.PullRefreshLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.msfjarvis.afh.Vars;
import com.android.volley.toolbox.*;
import com.android.volley.*;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CompoundButton;

public class MainActivity extends Activity {
    String json = "";
	RequestQueue queue;
    ScrollView sv;
    List<AfhFiles> filesD = new ArrayList<>();
    List<Device> devices = new ArrayList<>();
    TextView mTextView;
    AfhAdapter adapter;
    DeviceAdapter devAdapter;
    PullRefreshLayout pullRefreshLayout;
    String savedID;
	boolean sortByDate;
    final String TAG = "TAG";
    int pages[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mTextView = (TextView) findViewById(R.id.tv);
		sv = (ScrollView) findViewById(R.id.tvSv);
        ListView fileList = (ListView) findViewById(R.id.list);
        final ListView deviceList = (ListView) findViewById(R.id.deviceList);
		final int NETWORK_THREAD_POOL_SIZE = 30;
		Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
		Network network = new BasicNetwork(new HurlStack());
		queue = new RequestQueue(cache, network, NETWORK_THREAD_POOL_SIZE);
		CheckBox sortCB = (CheckBox) findViewById(R.id.sortCB);
		
		
		sortCB.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked)
					sortByDate = true;
			}
		});
		
        pullRefreshLayout = (PullRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                start(savedID);
            }
        });

        adapter = new AfhAdapter(this, R.layout.afh_items, filesD);
        devAdapter = new DeviceAdapter(this, R.layout.device_items, devices);
        deviceList.setAdapter(devAdapter);
		fileList.setAdapter(adapter);

        deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                LinearLayout lay = (LinearLayout) findViewById(R.id.mainLinearLayout);
                RelativeLayout rl = (RelativeLayout) findViewById(R.id.listLayout);
                lay.setVisibility(View.GONE);
                rl.setVisibility(View.VISIBLE);
                start(devices.get(i).did);
            }
        });

        findFirstDevice();
    }


    public void findFirstDevice() {
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
                Log.i(TAG, "onErrorResponse: " + error.toString());
                findFirstDevice();
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.start();
        queue.add(stringRequest);
    }
    public void findSubsequentDevices(final int pageNumber) {
        final String url = "https://www.androidfilehost.com/api/?action=devices&page=" + pageNumber + "&limit=100";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject deviceListJson = new JSONObject(response);
                            Log.i(TAG, "onResponseSubs: " + url);
                            parseDevices(deviceListJson.getJSONArray("DATA"));
                        } catch (Exception e) {
                            Log.i(TAG, "onResponse: " + e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "onErrorResponseSubs: " + error.toString());
                findSubsequentDevices(pageNumber);
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }
    public void start(final String did) {
        String url = String.format(new Vars().getDidEndpoint(), did);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        json = response;
                        pullRefreshLayout.setRefreshing(true);
						List<String> fid = null;
                        try {
                            sv.setVisibility(View.GONE);
                            fid = parse();
                        } catch (Exception e) {
                            pullRefreshLayout.setRefreshing(false);
							sv.setVisibility(View.VISIBLE);
                            mTextView.setText(getString(R.string.json_parse_error) + e.toString());
                        }
						sv.setVisibility(View.GONE);
						if(fid != null)
                            queryDirs(fid);


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
				sv.setVisibility(View.VISIBLE);
                pullRefreshLayout.setRefreshing(false);
                mTextView.setText(error.toString());
                start(did);
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }


    public List<String> parse() throws Exception {
        JSONObject afhJson;
        afhJson = new JSONObject(json);
        mTextView.setText("");
		List<String> fid = new ArrayList<>();
        JSONArray data = afhJson.getJSONArray("DATA");
        for (int i = 0; i < data.length(); i++) {
            fid.add(String.format(new Vars().getFlidEndpoint(), data.getJSONObject(i).getString(getString(R.string.flid_key))));
        }
		return fid;
    }

    public void print() {
        pullRefreshLayout.setRefreshing(false);
		if(sortByDate) {
            Collections.sort(filesD, Comparators.byUploadDate);
		} else {
			Collections.sort(filesD, Comparators.byFileName);
		}
        adapter.notifyDataSetChanged();

    }

    public void queryDirs(List <String> did) {

        for (String url : did) {
            final String link = url;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            pullRefreshLayout.setRefreshing(true);
                            try {
                                parseFiles(response);
                            } catch (Exception e) {
                                pullRefreshLayout.setRefreshing(false);
								sv.setVisibility(View.VISIBLE);
                               mTextView.setText(mTextView.getText().toString() + "\n\n\n" + getResources().getString(R.string.json_parse_error) + link + " " + e.toString());
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pullRefreshLayout.setRefreshing(false);
					sv.setVisibility(View.VISIBLE);
                    mTextView.setText(mTextView.getText().toString() + "\n\n\n" + link + "  :   " + error.toString());
                }
            });

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(stringRequest);

        }

    }

    public void parseFiles(String Json) throws Exception {
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



    public void processFindDevices(String json) throws Exception{
        JSONObject deviceListJson = new JSONObject(json);
        String message = deviceListJson.getString("MESSAGE");
        pages = findDevicePageNumbers(message);
        parseDevices(deviceListJson.getJSONArray("DATA"));
        Log.i(TAG, "processFindDevices: " + pages[3]);
        for(int currentPage = 2; currentPage <= pages[3]; currentPage++)
            findSubsequentDevices(currentPage);

    }
    public int[] findDevicePageNumbers(String message) {
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(message);
        int ar[] = new int[4];
        int i = 0;
        while (! m.hitEnd()) {
            if (m.find() && i < 4)
                ar[i++] = Integer.parseInt(m.group());
        }
        return ar;
    }
    public void parseDevices(JSONArray data) throws Exception {
        if(data != null)
            for(int i = 0; i < data.length(); i++) {
                JSONObject dev = data.getJSONObject(i);
                Device device = new Device(dev.getString("did"), dev.getString("manufacturer"), dev.getString("device_name"));
                devices.add(device);
            }
        Collections.sort(devices, Comparators.byManufacturer);
        Log.i(TAG, "parseDevices: " + devices.size());
        devAdapter.notifyDataSetChanged();
    }
}