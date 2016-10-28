package out386.afh;

import android.*;
import android.content.*;
import android.view.*;
import android.widget.*;
import java.util.*;

public class AfhAdapter extends ArrayAdapter<AfhFiles>
{
	private Context context;
    public AfhAdapter(Context context, int resource,List<AfhFiles> items) {
        super(context,resource,items);
        this.context = context;
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        View v = convertView;
        if(v == null)
            v= LayoutInflater.from(getContext()).inflate(R.layout.afh_items,null);
        AfhFiles p = getItem(position);
        if(p != null) {
            TextView name = (TextView) v.findViewById(R.id.rname);
            TextView link = (TextView) v.findViewById(R.id.rurl);
            name.setText(p.filename);
            link.setText(p.url);
        }
        return v;
    }
}
