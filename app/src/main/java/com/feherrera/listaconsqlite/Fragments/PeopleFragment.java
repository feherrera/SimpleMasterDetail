package com.feherrera.listaconsqlite.Fragments;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.feherrera.listaconsqlite.Adapters.PeopleListAdapter;
import com.feherrera.listaconsqlite.DataBaseHelper;
import com.feherrera.listaconsqlite.R;
import com.feherrera.listaconsqlite.RecyclerItemClickListener;

/**
 * Created by Felipe on 06-02-2018.
 */

public class PeopleFragment extends Fragment {

    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton;
    public static final String TAG = "RecyclerViewFragment";
    PeopleListAdapter peopleListAdapter;
    DataBaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_people, container, false);
        rootView.setTag(TAG);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_lista);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        dbHelper = DataBaseHelper.getDataBaseHelper(getActivity());

        floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.fb);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildAddDialog();
            }
        });

        updateView();

        return rootView;
    }

    /**
     * Build and return the alert dialog that add a possession to current person
     * @return Functional alert dialog
     */
    private AlertDialog buildAddDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getContext().getResources().getString(R.string.dialog_addPerson_title));

        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.people_add_dialog, null));

        builder.setPositiveButton(R.string.button_add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                AlertDialog alertDialog = (AlertDialog) dialog;
                String personName = ((EditText) alertDialog.findViewById(R.id.name)).getText().toString();
                int personAge = Integer.parseInt(((EditText) alertDialog.findViewById(R.id.age)).getText().toString());

                dbHelper.addPerson(personName, personAge);
                updateView();
            }
        });
        builder.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        final EditText nameField = (EditText) alertDialog.findViewById(R.id.name);
        nameField.addTextChangedListener(new TextWatcher() {
            /**
             * Change availability of positive button based on text emptiness
             */
            private void handleText() {
                Button addButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                String name = nameField.getText()!=null ? nameField.getText().toString() : "";
                if(name.length() == 0) {
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

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

        return alertDialog;
    }

    /**
     * Update the view with last database data
     */
    public void updateView(){
        try{
            Cursor cursor = dbHelper.getAllPeople();
            if (cursor != null){
                peopleListAdapter = new PeopleListAdapter(getActivity().getApplicationContext(), cursor);
                recyclerView.setAdapter(peopleListAdapter);
                recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(
                        getActivity().getApplicationContext(),
                        new OnItemClickListener(),
                        peopleListAdapter
                ));
            }
        } catch (Exception e){
            Log.e("ListaConSQLite", "exception", e);
        }
    }

    /**
     * Interface that should be implemented by the activity to be notified of item selected
     */
    public interface CallBacks{

        /**
         * Method to be invoked when a person from the list is selected.
         * @param personID ID of the selected person
         */
        public void onItemSelected(long personID);

        /**
         * Method to be invoked when a person is deleted. It should clear/refresh detail's view.
         */
        public void refreshDetail();
    }

    /**
     * Class that handle with a click on RecyclerView's item.
     */
    private class OnItemClickListener implements RecyclerItemClickListener.OnItemClickListener{

        @Override
        public void onItemClick(View childView, int position) {
            TextView id = (TextView) childView.findViewById(R.id.people_id);

            CallBacks cb = ((CallBacks) getActivity());
            cb.onItemSelected(Long.parseLong(id.getText().toString()));
        }

        @Override
        public void onItemLongPress(View childView, int position) {
            final TextView ID = (TextView) childView.findViewById(R.id.people_id);
            long personID = Long.parseLong(ID.getText().toString());
            AlertDialog deleteDialog = buildDeleteDialog(personID);
            deleteDialog.show();
        }

        /**
         * Build and return the alert dialog that deletes a person
         * @param personID ID of the person to be deleted
         * @return Functional alert dialog
         */
        private AlertDialog buildDeleteDialog(final long personID){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            //It shouldn't be a title in a simple dialog like this (according to android style guides)
            //builder.setTitle(getContext().getResources().getString(R.string.dialog_delete_title));
            builder.setMessage(getContext().getResources().getString(R.string.dialog_delete_message));
            builder.setIcon(R.mipmap.ic_launcher);

            builder.setPositiveButton(R.string.button_delete, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {

                    dbHelper.deletePerson(personID);
                    updateView();
                    CallBacks cb = ((CallBacks) getActivity());
                    cb.refreshDetail();
                }
            });

            builder.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {

                }
            });
            return builder.create();
        }
    }
}
