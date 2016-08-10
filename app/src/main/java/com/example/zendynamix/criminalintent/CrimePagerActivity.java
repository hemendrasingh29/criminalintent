package com.example.zendynamix.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;

/**
 * Created by zendynamix on 6/23/2016.
 */
public class CrimePagerActivity extends AppCompatActivity implements CriminalFragment.Callbacks {
    private static final String LOG_TAG=CrimePagerActivity.class.getSimpleName();
    private static final String EXTRA_CRIME_ID = "com.example.zendynamix.criminalintent.crime_id";
    private List<CrimeData> crimeDatas;
    private ViewPager viewPager;

    public static Intent newIntent(Context packageContext, UUID crimeId) {

        Intent intent = new Intent(packageContext, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);

        return intent;
    }
    @Override
    public void onCrimeUpdated(CrimeData crimeData){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);
        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        viewPager = (ViewPager) findViewById(R.id.activity_crime_pager_view_pager);
        crimeDatas = CrimeLab.get(this).getCrimes();

        FragmentManager fragmentManager = getSupportFragmentManager();

        viewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                CrimeData crimeData = crimeDatas.get(position);
                return CriminalFragment.newInstance(crimeData.getId());
            }

            @Override
            public int getCount() {

                return crimeDatas.size();

            }
        });

        for (int i = 0; i < crimeDatas.size(); i++) {
            if (crimeDatas.get(i).getId().equals(crimeId)) {
                viewPager.setCurrentItem(i);
                break;
            }
        }


    }
}
