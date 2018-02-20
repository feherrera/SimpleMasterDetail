package com.feherrera.listaconsqlite.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.feherrera.listaconsqlite.Fragments.PossessionsFragment;
import com.feherrera.listaconsqlite.R;

/**
 * Created by Felipe on 12-02-2018.
 */

public class PossessionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_possessions);

        Bundle extras = getIntent().getExtras();

        if (savedInstanceState == null){
            initFragment(extras);
        }
    }

    /**
     * Called on onCreate method. Initialize the fragments view
     * @param extras Extras from intent
     */
    private void initFragment(Bundle extras){
        PossessionsFragment possessionsFragment = new PossessionsFragment();
        possessionsFragment.setArguments(extras);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frame_item, possessionsFragment)
                .commit();
    }
}
