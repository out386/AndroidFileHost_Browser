package browser.afh;

/*
 * Copyright (C) 2016 Ritayan Chakraborty (out386)
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