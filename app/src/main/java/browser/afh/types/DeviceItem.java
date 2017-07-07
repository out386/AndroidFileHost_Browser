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

package browser.afh.types;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mikepenz.fastadapter.items.GenericAbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

import java.io.Serializable;
import java.util.List;

import browser.afh.R;
import browser.afh.tools.Utils;

public class DeviceItem extends GenericAbstractItem<AfhDevices.Device, DeviceItem, DeviceItem.ViewHolder> implements Serializable {
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

    public DeviceItem(AfhDevices.Device device) {
        super(device);
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
    public void bindView(ViewHolder viewHolder, List<Object> payloads) {
        super.bindView(viewHolder, payloads);
        viewHolder.mName.setText(getModel().manufacturer);
        viewHolder.dName.setText(getModel().device_name);

        if (viewHolder.dImage != null) {
            Drawable placeholder = ContextCompat.getDrawable(viewHolder.dImage.getContext(), R.drawable.ic_device_placeholder);
            DrawableCompat.setTint(placeholder, Utils.getPrefsColour(1, viewHolder.dImage.getContext()));
            Context context = viewHolder.dImage.getContext();
            Glide
                    .with(context)
                    .load(getModel().image)
                    .placeholder(placeholder)
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
            this.mName = view.findViewById(R.id.mName);
            this.dName = view.findViewById(R.id.dName);
            this.dImage = view.findViewById(R.id.deviceImage);
        }
    }
}
