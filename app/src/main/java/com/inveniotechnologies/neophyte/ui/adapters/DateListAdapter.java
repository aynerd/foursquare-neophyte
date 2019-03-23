package com.inveniotechnologies.neophyte.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.inveniotechnologies.neophyte.ui.listitems.DateListItem;
import com.inveniotechnologies.neophyte.R;

import java.util.List;

/**
 * Created by bolorundurowb on 23-Aug-16.
 */
public class DateListAdapter extends RecyclerView.Adapter<DateListAdapter.DateListViewHolder>{
    private List<DateListItem> datesList;

    public DateListAdapter(List<DateListItem> dateList) {
        this.datesList = dateList;
    }

    @Override
    public DateListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View dateView = LayoutInflater.from(parent.getContext()).inflate(R.layout.date_list_item, parent, false);
        return  new DateListViewHolder(dateView);
    }

    @Override
    public void onBindViewHolder(DateListViewHolder holder, int position) {
        DateListItem item = datesList.get(position);
        holder.date.setText(item.getDate());
        holder.dayRep.setText(item.getDayRepresentation());
    }

    @Override
    public int getItemCount(){
        return  datesList.size();
    }

    public class DateListViewHolder extends RecyclerView.ViewHolder {
        public TextView date;
        public TextView dayRep;

        public DateListViewHolder(View view) {
            super(view);
            date = (TextView) view.findViewById(R.id.lbl_date);
            dayRep = (TextView) view.findViewById(R.id.lbl_day_string);
        }
    }
}

