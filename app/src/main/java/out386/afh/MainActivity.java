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

public class MainActivity extends Activity {
    String json = "";
	RequestQueue queue;
    //List<String> fid = new ArrayList<>();
    List<AfhFiles> filesD = new ArrayList<>();
    TextView mTextView;
    AfhAdapter adapter;
    PullRefreshLayout pullRefreshLayout;
    String savedID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mTextView = (TextView) findViewById(R.id.tv);
        Button button = (Button) findViewById(R.id.mainButton);

		queue = Volley.newRequestQueue(this);
        ListView list = (ListView) findViewById(R.id.list);
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
                savedID = text.getText().toString();
                start(text.getText().toString());
            }
        });

    }

    public void start(String did) {
        //RequestQueue queue = Volley.newRequestQueue(this);
        String url = String.format(new Vars().getDidEndpoint(), did);

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        json = response;
                        pullRefreshLayout.setRefreshing(true);
						List<String> fid = null;
                        try {
                            fid = parse();
                        } catch (Exception e) {
                            pullRefreshLayout.setRefreshing(false);
                           // mTextView.setText(String.format(getString(R.string.json_parse_error), e.toString()));
                        }
						if(fid != null)
                            queryDirs(fid);


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pullRefreshLayout.setRefreshing(false);
                mTextView.setText(getString(R.string.generic_error) + error.toString());
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(120000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
// Add the request to the RequestQueue.
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
        Collections.sort(filesD, new Comparator<AfhFiles>() {
            @Override
            public int compare(AfhFiles f1, AfhFiles f2) {
                return (f1.filename.compareTo(f2.filename));
            }
        });
        adapter.notifyDataSetChanged();

    }

    public void queryDirs(List <String> did) {

        for (String url : did) {
            final String link = url;
           // RequestQueue queue = Volley.newRequestQueue(this);

// Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            pullRefreshLayout.setRefreshing(true);
                            try {
                                parseFiles(response);
                            } catch (Exception e) {
                                pullRefreshLayout.setRefreshing(false);
                               // mTextView.setText(mTextView.getText().toString() + "\n\n\n" + getResources().getString(R.string.json_parse_error) + link + " " + e.toString());
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pullRefreshLayout.setRefreshing(false);
                    mTextView.setText(mTextView.getText().toString() + "\n\n\n" + link + "  :   " + error.toString());
                }
            });

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(1200000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(stringRequest);

        }

    }

    public void parseFiles(String Json) throws Exception {
        JSONObject fileJson = new JSONObject(Json);

        JSONObject data = fileJson.getJSONObject("DATA");
        //int i = 0;
        JSONArray files = data.getJSONArray("files");
        for (int i = 0; i < files.length(); i++) {
            //fid[i] = "androidfilehost.com/api/?action=folder&flid=" + data.getJSONObject(i).getString("flid");
            String name = files.getJSONObject(i).getString("name");
            String url = files.getJSONObject(i).getString("url");
            filesD.add(new AfhFiles(name, url));
        }
		JSONArray folders = data.getJSONArray("folders");
		List<String> foldersD = new ArrayList<>();
		for (int i = 0; i < folders.length(); i++) {
			foldersD.add("https://www.androidfilehost.com/api/?action=folder&flid=" + folders.getJSONObject(i).getString("flid"));
			//mTextView.setText(mTextView.getText() + "\n\n\n" + "Checking url : " + foldersD.get(i));
		}
		//foldersD.add("https://www.androidfilehost.com/api/?action=folder&flid=51856");
		if(foldersD.size() > 0)
			queryDirs(foldersD);
        print();
    }
}