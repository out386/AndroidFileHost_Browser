package out386.afh;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
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
    TextView mTextView;
    AfhAdapter adapter;
    PullRefreshLayout pullRefreshLayout;
    String savedID;
	boolean sortByDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mTextView = (TextView) findViewById(R.id.tv);
		sv = (ScrollView) findViewById(R.id.tvSv);
        Button button = (Button) findViewById(R.id.mainButton);
        ListView list = (ListView) findViewById(R.id.list);
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
        list.setAdapter(adapter);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout lay = (LinearLayout) findViewById(R.id.mainLinearLayout);
                RelativeLayout rl = (RelativeLayout) findViewById(R.id.listLayout);
                lay.setVisibility(View.GONE);
                rl.setVisibility(View.VISIBLE);
                EditText text = (EditText) findViewById(R.id.mainEditText);
                start(text.getText().toString());
            }
        });

    }

    public void start(String did) {
        String url = String.format(new Vars().getDidEndpoint(), did);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        json = response;
                        pullRefreshLayout.setRefreshing(true);
						List<String> fid = null;
                        try {
                            fid = parse();
                        } catch (Exception e) {
                            pullRefreshLayout.setRefreshing(false);
							sv.setVisibility(View.VISIBLE);
                            mTextView.setText(String.format(getString(R.string.json_parse_error), e.toString()));
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
                mTextView.setText(getString(R.string.generic_error) + error.toString());
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000, 5, 1));
		queue.start();
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

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(600000, 5, 1));
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
}