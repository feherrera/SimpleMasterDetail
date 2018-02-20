package com.feherrera.listaconsqlite.Fragments;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.feherrera.listaconsqlite.DataBaseHelper;
import com.feherrera.listaconsqlite.R;

/**
 * Created by Felipe on 12-02-2018.
 */

public class PossessionsFragment extends Fragment {

    ListView listView;
    TextView addItem;
    Cursor cursor;
    SimpleCursorAdapter cursorAdapter;
    DataBaseHelper dbHelper;
    long personID;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_possessions, container, false);

        listView = (ListView) rootView.findViewById(R.id.listview);
        addItem = (TextView) rootView.findViewById(R.id.button_additem);

        Bundle bundle = getArguments();

        if (bundle != null){
            //This happens when master's item is deleted. It shows an empty list.
            if (bundle.getBoolean("restart")){
                listView.setAdapter(null);
                return rootView;
            }
            else {
                personID = bundle.getLong("personID");
                initList();
            }
        }

        return rootView;
    }

    /**
     * Init the listView: update the view and set clickListeners
     */
    private void initList(){
        dbHelper = DataBaseHelper.getDataBaseHelper(getActivity());
        updateView();

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog addDialog = buildAddDialog();
                addDialog.show();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final TextView ID = (TextView) view.findViewById(R.id.possession_id);
                long possessionId = Long.parseLong(ID.getText().toString());
                AlertDialog deleteDialog = buildDeleteDialog(possessionId);
                deleteDialog.show();
                deleteDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            }
        });
    }

    /**
     * Build and return the alert dialog that add a possession to current person
     * @return Functional alert dialog
     */
    private AlertDialog buildAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getContext().getResources().getString(R.string.dialog_addPossession_title));

        final EditText name = new EditText(getActivity());
        name.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        name.setHint(getContext().getResources().getString(R.string.people_name));
        builder.setView(name);

        builder.setPositiveButton(R.string.button_add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dbHelper.addPossession(personID, name.getText().toString());
                updateView();
            }
        });
        builder.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        final AlertDialog alertDialog = builder.create();

        name.addTextChangedListener(new TextWatcher() {
            /**
             * Change availability of positive button based on text emptiness
             */
            private void handleText() {
                Button addButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                if(name.getText().length() == 0) {
                    addButton.setEnabled(false);
                } else {
                    addButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                handleText();
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Nothing to do
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Nothing to do
            }
        });

        return alertDialog;
    }

    /**
     * Build and return the alert dialog that deletes a possession
     * @param possessionId ID of the possession to be deleted
     * @return Functional alert dialog
     */
    private AlertDialog buildDeleteDialog(final long possessionId){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(getContext().getResources().getString(R.string.dialog_delete_message));
        builder.setIcon(R.mipmap.ic_launcher);

        builder.setPositiveButton(R.string.button_delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                dbHelper.deletePossession(possessionId);
                updateView();
            }
        });
        builder.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        return builder.create();
    }

    /**
     * Update the view with last database data
     */
    public void updateView(){
        cursor = dbHelper.getPossessions(personID);
        cursorAdapter = new SimpleCursorAdapter(getActivity().getApplicationContext()
                , R.layout.possession_list_item
                , cursor
                , cursor.getColumnNames()
                , new int[] { R.id.possession_id, R.id.possession_title }
                , CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        listView.setAdapter(cursorAdapter);
    }
}
