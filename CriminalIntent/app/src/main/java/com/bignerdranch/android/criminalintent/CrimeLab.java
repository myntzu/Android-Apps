package com.bignerdranch.android.criminalintent;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bignerdranch.android.criminalintent.database.CrimeCursorWrapper;
import com.bignerdranch.android.criminalintent.database.CrimeDbSchema.CrimeTable;
import com.bignerdranch.android.criminalintent.database.CrimeBaseHelper;
import com.bignerdranch.android.criminalintent.database.CrimeDbSchema;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeLab {
    private static CrimeLab sCrimeLab;

  //  private List<Crime> mCrimes; --> no longer needed due to database!!
    private Context mContext; //to use CrimeBaseHelper
    private SQLiteDatabase mDatabase; //and create database

    public static CrimeLab get(Context context){
        if(sCrimeLab == null){
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    private CrimeLab(Context context){
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase(); //db
       // mCrimes = new ArrayList<>(); !!database replaced

//        //generate crimes
//        for(int i = 0; i < 100; i++){
//            Crime crime = new Crime();
//            crime.setTitle("Crime #" + i);
//            crime.setSolved(i % 2 == 0);
//            mCrimes.add(crime);
//        }
        //
        // no more needed!! because of below code segment!
    }

    //to respond to (+) in the app
    public void addCrime(Crime c){
        ContentValues values = getContentValues(c);
        mDatabase.insert(CrimeTable.NAME, null, values);

     //   mCrimes.add(c); !!! db replaced
    }

    //inserting&updating new rows in db
    public void updateCrime(Crime crime){
        String uuidString = crime.getId().toString();
        ContentValues values = getContentValues(crime);

        mDatabase.update(CrimeTable.NAME, values, CrimeTable.Cols.UUID + " = ?",
                new String[] { uuidString });
    }

    //to read from db

  //>>  private Cursor queryCrimes(String whereClause, String[] whereArgs){

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                CrimeTable.NAME,
                null, //columns - null selects all cols
                whereClause,
                whereArgs,
                null, //groupBy
                null, //having
                null //orderBy } basic SQL!
        );

        //return cursor;
        return new CrimeCursorWrapper(cursor); //to return crime list
    }

    public List<Crime> getCrimes(){
       // return mCrimes; !!! db replaced
       // return new ArrayList<>(); !!! cursor replaced

        //to return crime list!
        List<Crime> crimes = new ArrayList<>();

        CrimeCursorWrapper cursor = queryCrimes(null, null);

        try {
            //(1) to pull data out of a cursor, it is moved to the first
            //element | the read in rows*
            cursor.moveToFirst();
            //populate crime list
            while (!cursor.isAfterLast()) { //(3) once it hits here, stops moving next*
                crimes.add(cursor.getCrime());
                cursor.moveToNext(); //(2) moves to NEXT ROW
            }
        }finally{
            cursor.close(); //(4) close cursor (end of operation)
        }
        return crimes;
    }

    public Crime getCrime(UUID id){

//        for(Crime crime : mCrimes){
//            if(crime.getId().equals(id)){
//                return crime;
//            }
//        } !!!! database replacedddd
//
          // return null; **returning crime obj thru cursor:-
        CrimeCursorWrapper cursor = queryCrimes(
                CrimeTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
        );

        try{
            if (cursor.getCount() == 0){
                return null;
            }
            cursor.moveToFirst();
            return cursor.getCrime();
        }finally{
            cursor.close();
        }
    }

    // <--- latest recap ---->
    //* You can insert crimes, so the code that adds Crime to CrimeLab when you press the New
    //Crime action item now works.
    //◦ You can successfully query the database, so CrimePagerActivity can see all the Crimes in
    //CrimeLab, too.
    //◦ CrimeLab.getCrime(UUID) works, too, so each CrimeFragment displayed in CrimePagerActivity
    //is showing the real Crime.
    // <---------------------->

    //to write OR update data into SQLiteDb :
    private static ContentValues getContentValues(Crime crime){
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID, crime.getId().toString());
        values.put(CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        values.put(CrimeTable.Cols.SUSPECT, crime.getSuspect());

        return values;
    }


}
