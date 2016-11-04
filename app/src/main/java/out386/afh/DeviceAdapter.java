package out386.afh;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Js on 11/4/2016.
 */

public class DeviceAdapter extends ArrayAdapter<Device> {
    private Context context;

    public DeviceAdapter(Context context, int resource, List<Device> items) {
        super(context, resource, items);
        this.context = context;
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null)
            v = LayoutInflater.from(getContext()).inflate(R.layout.device_items, null);
        Device p = getItem(position);
        if (p != null) {
            TextView manufacturer = (TextView) v.findViewById(R.id.mName);
            TextView dev = (TextView) v.findViewById(R.id.dName);
            manufacturer.setText(p.manufacturer);
            dev.setText(p.device_name);
        }
        return v;
    }
}