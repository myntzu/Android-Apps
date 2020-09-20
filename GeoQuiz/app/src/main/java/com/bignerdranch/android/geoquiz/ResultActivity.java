//this was supposed to be the result summary page but I couldnt
//manage to complete this in time T-T

package com.bignerdranch.android.geoquiz;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        TextView resultSummary = (TextView) findViewById(R.id.resultSummary);
        TextView totalScore = (TextView) findViewById(R.id.totalScore);

        int score = 0;
    }
}
