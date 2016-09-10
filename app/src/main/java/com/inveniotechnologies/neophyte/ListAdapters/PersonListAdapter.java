package com.inveniotechnologies.neophyte.ListAdapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.inveniotechnologies.neophyte.ListItems.PersonListItem;
import com.inveniotechnologies.neophyte.R;

import java.util.List;

/**
 * Created by bolorundurowb on 27-Aug-16.
 */
public class PersonListAdapter extends RecyclerView.Adapter<PersonListAdapter.PersonListViewHolder> {
    private List<PersonListItem> personsList;

    public class PersonListViewHolder extends RecyclerView.ViewHolder {
        public TextView fullname;
        public TextView mobile;

        public PersonListViewHolder(View view) {
            super(view);
            fullname = (TextView) view.findViewById(R.id.lbl_full_name);
            mobile = (TextView) view.findViewById(R.id.lbl_mobile);
        }
    }

    public PersonListAdapter(List<PersonListItem> personsList) {
        this.personsList = personsList;
    }

    @Override
    public PersonListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View personView = LayoutInflater.from(parent.getContext()).inflate(R.layout.person_list_item, parent, false);
        return new PersonListViewHolder(personView);
    }

    @Override
    public void onBindViewHolder(PersonListViewHolder holder, int position) {
        PersonListItem item = personsList.get(position);
        holder.fullname.setText(item.getFullName());
        holder.mobile.setText(item.getMobile());
    }

    @Override
    public int getItemCount() {
        return personsList.size();
    }
}
