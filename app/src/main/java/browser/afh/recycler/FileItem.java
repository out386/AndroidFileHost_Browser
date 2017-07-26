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

package browser.afh.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.GenericAbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

import java.io.Serializable;
import java.util.List;

import browser.afh.R;
import browser.afh.types.Files;

public class FileItem extends GenericAbstractItem<Files, FileItem, FileItem.ViewHolder> implements Serializable {
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

    public FileItem(Files file) {
        super(file);
    }

    @Override
    public int getType() {
        return R.id.rname;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.afh_items;
    }


    @Override
    public void bindView(ViewHolder viewHolder, List<Object> payloads) {
        super.bindView(viewHolder, payloads);
        viewHolder.mName.setText(getModel().name);
    }

    @Override
    public void unbindView(ViewHolder holder) {
        super.unbindView(holder);
        holder.mName.setText(null);
    }

    @Override
    public ViewHolderFactory<? extends ViewHolder> getFactory() {
        return FACTORY;
    }

    private static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mName;

        ViewHolder(View view) {
            super(view);
            this.mName = view.findViewById(R.id.rname);
        }
    }
}
