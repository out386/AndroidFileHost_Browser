package browser.afh.types;

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

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.io.Serializable;
import java.util.List;

import browser.afh.R;

public class Device extends AbstractItem<Device, Device.ViewHolder> implements Serializable {
    public String did;
    public String manufacturer;
    public String device_name;
    public Device( String did, String manufacturer, String device_name) {
        this.did = did;
        this.manufacturer = manufacturer;
        this.device_name = device_name;
    }

    @Override
    public int getType() {
        return R.id.mName;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.device_items;
    }

    @Override
    public void bindView(ViewHolder viewHolder, List payloads) {
        super.bindView(viewHolder, payloads);
        viewHolder.mName.setText(manufacturer);
        viewHolder.dName.setText(device_name);
    }

    @Override
    public void unbindView(ViewHolder holder) {
        super.unbindView(holder);
        holder.mName.setText(null);
        holder.dName.setText(null);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView mName;
        protected TextView dName;

        public ViewHolder(View view) {
            super(view);
            this.mName = (TextView) view.findViewById(R.id.mName);
            this.dName = (TextView) view.findViewById(R.id.dName);
        }
    }
}
