package com.feherrera.listaconsqlite.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.feherrera.listaconsqlite.Fragments.PossessionsFragment;
import com.feherrera.listaconsqlite.Fragments.PeopleFragment;
import com.feherrera.listaconsqlite.R;

public class MainActivity extends AppCompatActivity implements PeopleFragment.CallBacks {

    public boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initFragments(savedInstanceState);
    }

    /**
     * Called on onCreate method. Initialize the fragments view
     * @param savedInstanceState
     */
    private void initFragments(Bundle savedInstanceState){
        if (findViewById(R.id.frame_item) != null){
            mTwoPane = true;
            if (savedInstanceState == null){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_item, new PossessionsFragment())
                        .commit();
            }else {
                mTwoPane = false;
            }
        }
    }

    @Override
    public void onItemSelected(long personID) {
        if (mTwoPane){
            Bundle bundle = new Bundle();
            bundle.putLong("personID", personID);
            PossessionsFragment possessionsFragment = new PossessionsFragment();
            possessionsFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_item, possessionsFragment)
                    .commit();
        }else{
            Intent intent = new Intent(this, PossessionsActivity.class);
            intent.putExtra("personID", personID);
            startActivity(intent);
        }
    }

    @Override
    public void refreshDetail() {
        if (mTwoPane){
            PossessionsFragment possessionsFragment = new PossessionsFragment();

            Bundle bundle =  new Bundle();
            bundle.putBoolean("restart", true);
            possessionsFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_item, possessionsFragment)
                    .commit();
        }
    }
}
