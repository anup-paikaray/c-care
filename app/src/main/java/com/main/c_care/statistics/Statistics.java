package com.main.c_care.statistics;

import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.main.c_care.DatabaseHelper;
import com.main.c_care.R;
import com.main.c_care.geofence.Map;

public class Statistics extends Fragment implements recyclerAdapter.OnClickRecyclerListener {
    private static final String TAG = "StatsFrag";
    DatabaseHelper myDb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDb = new DatabaseHelper(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewAllData(view);
    }

    private void viewAllData(View view) {
        Cursor res = myDb.getLocationData();
        if (res.getCount() == 0) {
            return;
        }

        String[][] data = new String[res.getCount()][3];
        for (int i = 0; res.moveToNext(); i++) {
            data[i][0] = res.getString(5);  //tag
            data[i][1] = res.getString(1);  //time-stamp
            data[i][2] = res.getString(6);  //count
            if (data[i][2] == null)
                data[i][2] = String.valueOf(0);
            Log.d(TAG, "viewAllData: " + data[i][0] + "\t" + data[i][1] + "\t" + data[i][2]);
        }

        Log.d(TAG, "viewAllData: " + data);

        RecyclerView statsList = view.findViewById(R.id.stats_list);
        statsList.setLayoutManager(new LinearLayoutManager(getContext()));
        statsList.setAdapter(new recyclerAdapter(data, this));
    }

    @Override
    public void onRecyclerClick(int position) {
        Log.d(TAG, "onRecyclerClick: " + position);
        Cursor res = myDb.getLocationData();
        if (res.getCount() == 0) {
            return;
        }
        for (int i = 0; res.moveToNext(); i++) {
            if (i == position) {
                Map map = new Map();
                Bundle bundle = new Bundle();
                bundle.putString("lat", res.getString(2));
                bundle.putString("lng", res.getString(3));
                map.setArguments(bundle);
                getParentFragmentManager().beginTransaction().replace(R.id.container, map).commit();
            }
        }
    }
}
