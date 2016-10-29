package out386.afh;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends Activity 
{
	String json="";
	String [] fid;
	List <AfhFiles> filesD = new ArrayList<>();
	TextView mTextView;
	AfhAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		
	mTextView =(TextView) findViewById(R.id.tv);
	Button button = (Button) findViewById(R.id.mainButton);
	
		ListView list = (ListView) findViewById(R.id.list);
		adapter = new AfhAdapter(this, R.layout.afh_items, filesD);
		list.setAdapter(adapter);
		
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view){
				LinearLayout lay = (LinearLayout) findViewById(R.id.mainLinearLayout);
				lay.setVisibility(View.GONE);
				mTextView.setVisibility(View.VISIBLE);
				EditText text = (EditText) findViewById(R.id.mainEditText);
				start(text.getText().toString());
			}
		});
	
		}
		public void start(String did) {
		RequestQueue queue = Volley.newRequestQueue(this);
		String url ="https://www.androidfilehost.com/api/?action=developers&did="+did+"&limit=100";

// Request a string response from the provided URL.
	    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
            new Response.Listener<String>() {
				@Override
				public void onResponse(String response) {
					// Display the first 500 characters of the response string.
					json = response;
					mTextView.setText(R.string.parsing);//Response is: "+ response.substring(0,500));
					
					
					try {
						parse();
					}
					catch(Exception e) {
						mTextView.setText(String.format(getString(R.string.json_parse_error), e.toString()));
					}
					queryDirs();
					
					
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					mTextView.setText(String.format(getString(R.string.generic_error), error.toString()));
				}
			});
			
		stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
// Add the request to the RequestQueue.
		queue.add(stringRequest);
	}
	
	
	public void parse() throws Exception {
		JSONObject afhJson = null;
		afhJson = new JSONObject(json);
		
		JSONArray data = afhJson.getJSONArray("DATA");
		fid = new String [data.length()];
		//int i = 0;
		for(int i = 0;i < data.length();i++) {
			fid[i] = String.format(getString(R.string.afh_api), data.getJSONObject(i).getString(getString(R.string.flid_key)));
		}
	}
	public void print() {
		//List<AfhFiles> devs = new ArrayList<>();
		mTextView.setText("");
		Collections.sort(filesD, new Comparator<AfhFiles>(){
			@Override
			public int compare(AfhFiles f1, AfhFiles f2) {
				return (f1.filename.compareTo(f2.filename));
			}
		});
		for(AfhFiles file : filesD){
		
			//devs.add(new AfhFiles(fid[i], fid[i]));
			mTextView.setText(mTextView.getText() + " " + file.filename);
		}
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setVisibility(View.VISIBLE);
		adapter.notifyDataSetChanged();
		
	}
	
	public void queryDirs() {
		
		for(String url : fid) {
		final String link = url;
		RequestQueue queue = Volley.newRequestQueue(this);

// Request a string response from the provided URL.
	    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
            new Response.Listener<String>() {
				@Override
				public void onResponse(String response) {
					mTextView.setText(String.format(String.valueOf(R.string.parsing), link));


					try {
						parseFiles(response);
					}
					catch(Exception e) {
						mTextView.setText(String.format(String.valueOf(R.string.json_parse_error), e.toString()));
					}
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					mTextView.setText(link +"  :   "+ error.toString());
				}
			});

		stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		queue.add(stringRequest);
		
		}
		
	}
	
	public void parseFiles(String Json) throws Exception {
		JSONObject fileJson = new JSONObject(Json);

		JSONObject data = fileJson.getJSONObject("DATA");
		//int i = 0;
		JSONArray folders = data.getJSONArray("folders");
		for(int i = 0;i < folders.length();i++) {
			//fid[i] = "androidfilehost.com/api/?action=folder&flid=" + data.getJSONObject(i).getString("flid");
		    String name = folders.getJSONObject(i).getString("name");
			String url = folders.getJSONObject(i).getString("url");
			filesD.add(new AfhFiles(name, url));
			}
			print();
	}
}
