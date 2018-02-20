package com.feherrera.listaconsqlite.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.feherrera.listaconsqlite.R;
import com.feherrera.listaconsqlite.RecyclerItemClickListener;

/**
 * Created by Felipe on 06-02-2018.
 */

public class PeopleListAdapter extends RecyclerView.Adapter<PeopleListAdapter.ViewHolder>
        implements RecyclerItemClickListener.ClickableAdapter{

    private Context context;
    private CursorAdapter cursorAdapter;

    private int selectedPosition = RecyclerView.NO_POSITION;
    private View previouslySelected = null; //Used to avoid multi-select

    /**
     * RecyclerView Adapter
     * @param context context
     * @param cursor cursor
     */
    public PeopleListAdapter(Context context, Cursor cursor) {
        this.context = context;

        this.cursorAdapter = new CursorAdapter(this.context, cursor, 0) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.people_list_item, parent, false);
                return v;
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                TextView id = (TextView) view.findViewById(R.id.people_id);
                TextView title = (TextView) view.findViewById(R.id.people_title);
                TextView subtitle = (TextView) view.findViewById(R.id.people_subtitle);

                id.setText(cursor.getString(0));    // _id
                title.setText(cursor.getString(1)); // name
                subtitle.setText(context.getResources().getString(R.string.people_age) +
                        ": " + cursor.getString(2));// age
            }
        };
    }

    /**
     * Create new views (invoked by the layout manager)
     * @param parent
     * @param viewType
     * @return The created viewholder
     */
    @Override
    public PeopleListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.people_list_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    /**
     * Replace the contents of a view (invoked by the layout manager)
     * @param viewHolder Viewholder which contents will be replaced
     * @param position Dataset position
     */
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        this.cursorAdapter.getCursor().moveToPosition(position);
        this.cursorAdapter.bindView(viewHolder.itemView, this.context, this.cursorAdapter.getCursor());

        if (position == selectedPosition)
            viewHolder.itemView.setSelected(true);
        else
            viewHolder.itemView.setSelected(false);
    }

    /**
     * Return the size of dataset (invoked by the layout manager)
     */
    @Override
    public int getItemCount() {
        return this.cursorAdapter.getCount();
    }

    @Override
    public void onItemSelected(View childView, int position) {
        if (previouslySelected != null) previouslySelected.setSelected(false);
        selectedPosition = position;
        childView.setSelected(true);
        previouslySelected = childView;
    }

    @Override
    public void resetSelected() {
        selectedPosition = RecyclerView.NO_POSITION;
    }


    /**
     * Provide a reference to the views for each RecyclerView's item
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView id, title, subtitle;

        public ViewHolder(View v) {
            super(v);
            id = (TextView) v.findViewById(R.id.people_id);
            title = (TextView) v.findViewById(R.id.people_title);
            subtitle = (TextView) v.findViewById(R.id.people_subtitle);
        }
    }
}
