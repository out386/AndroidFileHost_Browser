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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.annotations.SerializedName;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

import java.io.Serializable;
import java.util.List;

import browser.afh.R;

public class DeviceData extends AbstractItem<DeviceData, DeviceData.ViewHolder> implements Serializable {
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();
    @SerializedName("did")
    public final String did;
    @SerializedName("manufacturer")
    public final String manufacturer;
    @SerializedName("device_name")
    public final String device_name;
    @SerializedName("image")
    public String image;
    public DeviceData(String did, String manufacturer, String device_name, String image) {
        this.did = did;
        this.manufacturer = manufacturer;
        this.device_name = device_name;
        this.image = image;
    }

    @Override
    public int getType() {
        return R.id.mName;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.device_items_no_image;
    }


    @Override
    public void bindView(ViewHolder viewHolder, List payloads) {
        super.bindView(viewHolder, payloads);
        viewHolder.mName.setText(manufacturer);
        viewHolder.dName.setText(device_name);

        if (viewHolder.dImage != null) {
            Context context = viewHolder.dImage.getContext();
            Glide
                    .with(context)
                    .load(image)
                    .placeholder(R.drawable.ic_device_placeholder)
                    .crossFade()
                    .fitCenter()
                    .into(viewHolder.dImage);
        }
    }

    @Override
    public void unbindView(ViewHolder holder) {
        super.unbindView(holder);
        holder.dName.setText(null);
        if (holder.dImage != null)
            holder.dImage.setImageDrawable(null);
    }

    private static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }
    @Override
    public ViewHolderFactory<? extends ViewHolder> getFactory() {
        return FACTORY;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView mName;
        final TextView dName;
        final ImageView dImage;

        ViewHolder(View view) {
            super(view);
            this.mName = (TextView) view.findViewById(R.id.mName);
            this.dName = (TextView) view.findViewById(R.id.dName);
            this.dImage = (ImageView) view.findViewById(R.id.deviceImage);
        }
    }
}
