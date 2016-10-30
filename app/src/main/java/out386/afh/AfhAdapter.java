package out386.afh;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
import android.text.*;
import android.text.method.*;

public class AfhAdapter extends ArrayAdapter<AfhFiles>
{
    public Context context;
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
            //TextView link = (TextView) v.findViewById(R.id.rurl);
            name.setText(Html.fromHtml("<a href=" + p.url + ">" + p.filename + "</a>"));
            //link.setText(p.url);
			name.setMovementMethod(LinkMovementMethod.getInstance());
        }
        return v;
    }
}