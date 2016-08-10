package com.example.zendynamix.criminalintent.crimedatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.zendynamix.criminalintent.crimedatabase.CrimeDbSchema.CrimeTable;

/**
 * Created by zendynamix on 6/28/2016.
 */
public class CrimeBaseHelper extends SQLiteOpenHelper {

    public static final int VERSON=1;
    private static final String DATABASE_NAME="crimeBase.db";

    public CrimeBaseHelper(Context context)
    {
        super(context,DATABASE_NAME,null,VERSON);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+CrimeTable.NAME +"("+"_ID integer primary key autoincrement,"+CrimeTable.Cols.UUID+","+
        CrimeTable.Cols.TITLE +","+
        CrimeTable.Cols.DATE +","+
        CrimeTable.Cols.SOLVED +","+
        CrimeTable.Cols.SUSPECT +")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
