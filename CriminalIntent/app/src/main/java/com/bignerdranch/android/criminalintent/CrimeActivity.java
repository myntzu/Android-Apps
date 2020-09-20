package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.UUID;


public class CrimeActivity extends SingleFragmentActivity {

    //displays crime based on their crime ID when the activity (page) is called

    //to call the ARGUMENT declared @CrimeFragment: change public --> PRIVATE
    private static final String EXTRA_CRIME_ID = "com.bignerdranch.android.criminalintent.crime_id";

    public static Intent newIntent(Context packageContext, UUID crimeId){
        Intent intent = new Intent(packageContext, CrimeActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }

    //to use SingleFragmentActivity abstract class
    @Override
    protected Fragment createFragment(){
       //to call ARGS
        // return new CrimeFragment();
        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        return CrimeFragment.newInstance(crimeId); //returns intended ID
    }

}
