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

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.annotations.SerializedName;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.io.Serializable;
import java.util.List;

import browser.afh.R;
import browser.afh.tools.Constants;

public class DeviceData extends AbstractItem<DeviceData, DeviceData.ViewHolder> implements Serializable {
    @SerializedName("did")
    public String did;
    @SerializedName("manufacturer")
    public String manufacturer;
    @SerializedName("device_name")
    public String device_name;
    @SerializedName("image")
    public String image;
    public DeviceData(String did, String manufacturer, String device_name) {
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
        Context context = viewHolder.dImage.getContext();
        super.bindView(viewHolder, payloads);
        viewHolder.mName.setText(manufacturer);
        viewHolder.dName.setText(device_name);
        Glide
                .with(context)
                .load(image)
                .placeholder(R.drawable.device_image_placeholder)
                .crossFade()
                .into(viewHolder.dImage);
    }

    @Override
    public void unbindView(ViewHolder holder) {
        super.unbindView(holder);
        holder.mName.setText(null);
        holder.dName.setText(null);
        holder.dImage.setImageDrawable(null);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView mName;
        protected TextView dName;
        protected ImageView dImage;

        public ViewHolder(View view) {
            super(view);
            this.mName = (TextView) view.findViewById(R.id.mName);
            this.dName = (TextView) view.findViewById(R.id.dName);
            this.dImage = (ImageView) view.findViewById(R.id.deviceImage);
        }
    }
}
