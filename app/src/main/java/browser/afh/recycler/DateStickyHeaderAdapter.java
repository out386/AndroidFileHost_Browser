package browser.afh.recycler;

import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikepenz.fastadapter.AbstractAdapter;
import com.mikepenz.fastadapter.IItem;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.turingtechnologies.materialscrollbar.IDateableAdapter;
import com.turingtechnologies.materialscrollbar.INameableAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import browser.afh.R;
import browser.afh.tools.Utils;
import browser.afh.types.Files;

/**
 * Created by mikepenz on 30.12.15.
 * This is a FastAdapter adapter implementation for the awesome Sticky-Headers lib by timehop
 * https://github.com/timehop/sticky-headers-recyclerview
 */

public class DateStickyHeaderAdapter extends AbstractAdapter implements StickyRecyclerHeadersAdapter, IDateableAdapter, INameableAdapter {
    private SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yy", Locale.getDefault());
    @Override
    public long getHeaderId(int position) {
        IItem item = getItem(position);
        if (item instanceof FileItem) {
            Files data = ((FileItem) item).getModel();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(data.upload_date_long);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            return cal.getTimeInMillis();
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
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
        if (item instanceof FileItem) {
            Files data = ((FileItem) item).getModel();
            Date date = new Date(data.upload_date_long);
            textView.setText(sdf.format(date));
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
    public Date getDateForElement(int element) {
        IItem item = getItem(element);
        if (item != null) {
            if (item instanceof FileItem) {
                Files data = ((FileItem) item).getModel();
                return new Date(data.upload_date_long);
            }
        }
        return new Date();
    }

    @Override
    public Character getCharacterForElement(int element) {
        IItem item = getItem(element);
        if (item != null) {
            if (item instanceof FileItem) {
                Files data = ((FileItem) item).getModel();
                if (data.name != null && data.name.length() > 0)
                    return data.name.charAt(0);
            }
        }
        return 0;
    }
}
