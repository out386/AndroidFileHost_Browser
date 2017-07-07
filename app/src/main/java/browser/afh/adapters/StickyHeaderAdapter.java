package browser.afh.adapters;

import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikepenz.fastadapter.AbstractAdapter;
import com.mikepenz.fastadapter.IItem;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.turingtechnologies.materialscrollbar.INameableAdapter;

import java.util.List;
import java.util.Locale;

import browser.afh.R;
import browser.afh.tools.Utils;
import browser.afh.types.AfhDevices;
import browser.afh.types.DeviceItem;

/**
 * Created by mikepenz on 30.12.15.
 * This is a FastAdapter adapter implementation for the awesome Sticky-Headers lib by timehop
 * https://github.com/timehop/sticky-headers-recyclerview
 */

public class StickyHeaderAdapter extends AbstractAdapter implements StickyRecyclerHeadersAdapter, INameableAdapter {
    @Override
    public long getHeaderId(int position) {
        IItem item = getItem(position);

        //in our sample we want a separate header per first letter of our items
        //this if is not necessary for your code, we only use it as this sticky header is reused for different item implementations
        if (item instanceof DeviceItem) {
            AfhDevices.Device data = ((DeviceItem) item).getModel();
            if (data.manufacturer != null)
                return Character.toUpperCase((data.manufacturer.charAt(0)));
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        //we create the view for the header
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_header, parent, false);
        TextView tv = view.findViewById(R.id.header_textview);
        View v = view.findViewById(R.id.header_line);
        tv.setTextColor(Utils.getPrefsColour(2, view.getContext()));
        v.setBackground(new ColorDrawable(Utils.getPrefsColour(2, view.getContext())));
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        TextView textView = holder.itemView.findViewById(R.id.header_textview);

        IItem item = getItem(position);
        if (item instanceof DeviceItem) {
            AfhDevices.Device data = ((DeviceItem) item).getModel();
            if (data.manufacturer != null) {
                //based on the position we set the headers text
                textView.setText(String.valueOf(data.manufacturer.charAt(0)).toUpperCase(Locale.getDefault()));
            }
        }
    }

    @Override
    public int getOrder() {
        return -100;
    }

    @Override
    public int getAdapterItemCount() {
        return 0;
    }

    @Override
    public List<IItem> getAdapterItems() {
        return null;
    }

    @Override
    public IItem getAdapterItem(int position) {
        return null;
    }

    @Override
    public int getAdapterPosition(IItem item) {
        return -1;
    }

    @Override
    public int getAdapterPosition(long identifier) {
        return -1;
    }

    @Override
    public int getGlobalPosition(int position) {
        return -1;
    }

    // For the fast scroller
    @Override
    public Character getCharacterForElement(int element) {
        IItem item = getItem(element);
        if (item != null) {
            AfhDevices.Device data = ((DeviceItem) item).getModel();
            if (data.manufacturer != null && data.manufacturer.length() > 0)
                return (data.manufacturer.charAt(0));
        }
        return 0;
    }

}
