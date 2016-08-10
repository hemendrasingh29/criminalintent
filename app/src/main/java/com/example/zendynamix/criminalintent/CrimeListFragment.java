package com.example.zendynamix.criminalintent;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

/**
 * Created by zendynamix on 6/22/2016.
 */
public class CrimeListFragment extends Fragment {
    private static final int REQUEST_CRIME = 1;
    private static final String LOG_TAG = CrimeListFragment.class.getSimpleName();
    private static final String SAVED_SUBTILE_VISIBLE = "subtitle";
    private RecyclerView recyclerView;
    private CrimeAdapter crimeAdapter;
    private boolean subtitleVisible;
    private Callbacks callbacks;

    public interface Callbacks {
        void onCrimeSelected(CrimeData crimeData);
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        callbacks = (Callbacks) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (savedInstanceState != null) {
            subtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTILE_VISIBLE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();
        Button addCrimeButton = (Button) getActivity().findViewById(R.id.button_addCrime);
        addCrimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CrimeData crimeData = new CrimeData();
                CrimeLab.get(getActivity()).addCrime(crimeData);
                callbacks.onCrimeSelected(crimeData);

            }
        });

        return view;
    }

    public void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<CrimeData> crimeDatas = crimeLab.getCrimes();
        if (crimeAdapter == null) {
            crimeAdapter = new CrimeAdapter(crimeDatas);
            recyclerView.setAdapter(crimeAdapter);
        } else {
            crimeAdapter.setCrimeDataList(crimeDatas);
            crimeAdapter.notifyDataSetChanged();
        }
        updateSubtitle();
    }

    public class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CrimeData mCrimeData;
        private TextView crimetitleTextView;
        private TextView dateTextview;
        private CheckBox checkBox;


        private CrimeHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            checkBox = (CheckBox) itemView.findViewById(R.id.list_item_crime_solved_check_box);
            crimetitleTextView = (TextView) itemView.findViewById(R.id.list_item_crime_title_text_view);
            dateTextview = (TextView) itemView.findViewById(R.id.list_item_crime_date_text_view);
            addFirstcrime();
        }

        public void bindCrime(CrimeData crimeData) {
            mCrimeData = crimeData;
            checkBox.setChecked(mCrimeData.isSolved());
            crimetitleTextView.setText(mCrimeData.getTitle());
            dateTextview.setText(mCrimeData.getDate().toString());

        }

        @Override
        public void onClick(View v) {

            callbacks.onCrimeSelected(mCrimeData);
        }
    }


    public class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<CrimeData> crimeDataList;

        public CrimeAdapter(List<CrimeData> crimeDataList) {
            this.crimeDataList = crimeDataList;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_crime, parent, false);
            return new CrimeHolder(view);
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            CrimeData crimeData = crimeDataList.get(position);
            holder.bindCrime(crimeData);

        }

        @Override
        public int getItemCount() {
            return crimeDataList.size();
        }

        public void setCrimeDataList(List<CrimeData> crimeDatas) {
            crimeDataList = crimeDatas;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(SAVED_SUBTILE_VISIBLE, subtitleVisible);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);
        MenuItem subtitleMenuItem = menu.findItem(R.id.menu_item_show_subtitle);
        if (subtitleVisible) {
            subtitleMenuItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleMenuItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_crime:
                CrimeData crimeData = new CrimeData();
                CrimeLab.get(getActivity()).addCrime(crimeData);
                updateUI();
                callbacks.onCrimeSelected(crimeData);
                return true;
            case R.id.menu_item_show_subtitle:
                subtitleVisible = !subtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeSize = crimeLab.getCrimes().size();
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural, crimeSize, crimeSize);
        if (!subtitleVisible) {
            subtitle = null;
        }
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        appCompatActivity.getSupportActionBar().setSubtitle(subtitle);

    }

    private void addFirstcrime() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeSize = crimeLab.getCrimes().size();
        if (crimeSize != 0) {
            View view = getActivity().findViewById(R.id.button_addCrime);
            View view1 = getActivity().findViewById(R.id.display_no_crime);
            view.setVisibility(View.GONE);
            view1.setVisibility(View.GONE);
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
    }
}


