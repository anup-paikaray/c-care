package com.main.c_care.assessment;

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
import android.widget.Toast;

import com.main.c_care.database.LocalDatabaseHelper;
import com.main.c_care.R;

public class Assessment extends Fragment implements questionAdapter.OnClickRecyclerListener {
    private static final String TAG = "AssessFrag";
    private LocalDatabaseHelper myDb;
    private int[] responses = new int[4];
    private String [] questions = {
            "Have you taken the medicine in time?",
            "Are you feeling any better than yesterday?",
            "Have any of your family member is feeling sick?",
            "Do you feel breathlessness?",
            "Do you feel you need emergency service?"
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDb = new LocalDatabaseHelper(getContext());
        for (int i = 0; i < responses.length; i++) {
            responses[i] = -1;
//            myDb.insertData(questions[i]);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_assessment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewQuestions(view);
    }

    private void viewQuestions(View view) {
        Cursor res = myDb.getQuestionData();
        if (res.getCount() == 0) {
            return;
        }

        String[] data = new String[res.getCount()];
        for (int i = 0; res.moveToNext(); i++)
            data[i] = res.getString(1);

        Log.d(TAG, "viewAllData: " + data);
        RecyclerView questionList = view.findViewById(R.id.question_list);
        questionList.setLayoutManager(new LinearLayoutManager(getContext()));
        questionList.setAdapter(new questionAdapter(data, this));
    }

    @Override
    public void onRecyclerClick(int position, int response) {
        Log.d(TAG, "onRecyclerClick: " + position + " " + response);
        responses[position] = response;
        for (int i = 0; i < responses.length; i++)
            if (responses[i] == -1)
                return;
        submitRespose();
    }

    private void submitRespose() {
        boolean isInserted = myDb.insertData(responses);
        if (isInserted)
            Toast.makeText(getContext(), "DATA INSERTED", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getContext(), "DATA NOT INSERTED", Toast.LENGTH_SHORT).show();
    }
}