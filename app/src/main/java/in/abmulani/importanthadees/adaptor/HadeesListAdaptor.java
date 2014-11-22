package in.abmulani.importanthadees.adaptor;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import in.abmulani.importanthadees.R;
import in.abmulani.importanthadees.database.HadeesTable;

/**
 * Created by AABID on 16-11-2014.
 */
public class HadeesListAdaptor extends BaseAdapter {

    private final List<HadeesTable> hadeesList;
    private final AbsListView.LayoutParams layoutParams;
    private LayoutInflater inflater;

    public HadeesListAdaptor(Activity context, List<HadeesTable> tables, int screenHeight) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.hadeesList = tables;
        layoutParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (screenHeight * 0.6));
    }

    @Override
    public int getCount() {
        return hadeesList.size();
    }

    @Override
    public HadeesTable getItem(int position) {
        return hadeesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = inflater.inflate(R.layout.inflater_list_item, null);
            ViewHolder viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            view = convertView;
        }
        final ViewHolder holder = (ViewHolder) view.getTag();
        HadeesTable table = getItem(position);
        holder.headerTextView.setText(table.getTitle());
        holder.descTextView.setText(table.getDescription());
        holder.parentLayout.setLayoutParams(layoutParams);
        if (table.isRead()) {
            holder.isNewTextView.setVisibility(View.GONE);
        } else {
            holder.isNewTextView.setVisibility(View.VISIBLE);
        }
        return view;
    }

    public static class ViewHolder {
        @InjectView(R.id.textView_title)
        TextView headerTextView;

        @InjectView(R.id.textView_desc)
        TextView descTextView;

        @InjectView(R.id.layout_parent)
        RelativeLayout parentLayout;

        @InjectView(R.id.textView_is_new)
        TextView isNewTextView;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

    }

}

