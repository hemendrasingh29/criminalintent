package com.example.zendynamix.criminalintent.crimedatabase;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.zendynamix.criminalintent.CrimeData;
import com.example.zendynamix.criminalintent.crimedatabase.CrimeDbSchema.CrimeTable;

import java.util.Date;
import java.util.UUID;

/**
 * Created by zendynamix on 6/28/2016.
 */
public class CrimeCursorWrapper extends CursorWrapper {
    public static final String LOG_TAG=CrimeCursorWrapper.class.getSimpleName();

    public  CrimeCursorWrapper(Cursor cursor){
        super(cursor);
    }

    public CrimeData getCrime(){
        String uuidString=getString(getColumnIndex(CrimeTable.Cols.UUID));
        String title=getString(getColumnIndex(CrimeTable.Cols.TITLE));
        long date=getLong(getColumnIndex(CrimeTable.Cols.DATE));
        int isSolved=getInt(getColumnIndex(CrimeTable.Cols.SOLVED));
        String suspect=getString(getColumnIndex(CrimeTable.Cols.SUSPECT));

        CrimeData crimeData=new CrimeData(UUID.fromString(uuidString));
        crimeData.setTitle(title);
        crimeData.setDate(new Date(date));
        crimeData.setSolved(isSolved!=0);
        crimeData.setSuspect(suspect);
        return crimeData;
    }
}
