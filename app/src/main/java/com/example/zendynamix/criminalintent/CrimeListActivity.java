package com.example.zendynamix.criminalintent;

import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by zendynamix on 6/22/2016.
 */
public class CrimeListActivity extends SingleFragmentActivity implements CrimeListFragment.Callbacks,CriminalFragment.Callbacks{

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment() ;
    }

    @Override
    protected int getLayoutResId(){
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onCrimeSelected(CrimeData crimeData) {
        if(findViewById(R.id.detail_fragment_container)==null){
            Intent intent=CrimePagerActivity.newIntent(this,crimeData.getId());
            startActivity(intent);
        }else {
            Fragment newDetailFragment=CriminalFragment.newInstance(crimeData.getId());
            getSupportFragmentManager().beginTransaction().replace(R.id.detail_fragment_container,newDetailFragment).commit();
        }
    }
    public void onCrimeUpdated(CrimeData crimeData){
        CrimeListFragment crimeListFragment=(CrimeListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        crimeListFragment.updateUI();

    }
}
