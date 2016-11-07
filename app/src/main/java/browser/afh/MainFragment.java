package browser.afh;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;

/**
 * Created by Js on 11/6/2016.
 */

public class MainFragment extends Fragment {
    View rootView;

    RequestQueue queue;




    final String TAG = "TAG";
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.main_fragment, container, false);

        final int NETWORK_THREAD_POOL_SIZE = 30;
        Cache cache = new DiskBasedCache(getActivity().getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        queue = new RequestQueue(cache, network, NETWORK_THREAD_POOL_SIZE);

        new FindDevices(rootView, queue).findFirstDevice();
        return rootView;
    }
}
