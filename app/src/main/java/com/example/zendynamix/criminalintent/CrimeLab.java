package com.example.zendynamix.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.example.zendynamix.criminalintent.crimedatabase.CrimeBaseHelper;
import com.example.zendynamix.criminalintent.crimedatabase.CrimeCursorWrapper;
import com.example.zendynamix.criminalintent.crimedatabase.CrimeDbSchema.CrimeTable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.example.zendynamix.criminalintent.crimedatabase.CrimeDbSchema.CrimeTable.Cols;
import static com.example.zendynamix.criminalintent.crimedatabase.CrimeDbSchema.CrimeTable.NAME;

/**
 * Created by zendynamix on 6/22/2016.
 */
public class CrimeLab {
    private static final String LOG_TAG =CrimePagerActivity.class.getSimpleName();
    private static CrimeLab sCrimeLab;
    private Context mcontext;
    private SQLiteDatabase sqLiteDatabase;

    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
           synchronized(CrimeLab.class) {
                if (sCrimeLab == null) {
                    sCrimeLab = new CrimeLab(context);
                }
            }
       }
        return sCrimeLab;
    }

    private CrimeLab(Context context) {
        mcontext = context.getApplicationContext();
        sqLiteDatabase=new CrimeBaseHelper(mcontext).getWritableDatabase();
    }

    public  void addCrime(CrimeData c){
        ContentValues values=getContentValues(c);
        sqLiteDatabase.insert(CrimeTable.NAME,null,values);
    }
    public void deleteCrime(UUID uuid){
        sqLiteDatabase.delete(CrimeTable.NAME, CrimeTable.Cols.UUID + " = ?", new String[]{uuid.toString()});


    }
    public List<CrimeData> getCrimes()
    {
        List<CrimeData> crimeDatas= new ArrayList<>();
        CrimeCursorWrapper cursor= queryCrimes(null,null);
        try{
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                crimeDatas.add(cursor.getCrime());
                cursor.moveToNext();
            }
            }finally {cursor.close();
        }
        return crimeDatas;
    }

    public CrimeData getCrime(UUID uuid){

        CrimeCursorWrapper cursor = queryCrimes(Cols.UUID + " =?",new String[]{uuid.toString()});
        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getCrime();
        }finally {
            cursor.close();
        }
    }

    public File getPhotoFile(CrimeData crimeData){
        File exteralFileDir= mcontext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if(exteralFileDir==null){
            return null;
        }
        return new File(exteralFileDir,crimeData.getPhotoFileName());
    }
    public void updateCrime(CrimeData crimeData){
        String uuidString = crimeData.getId().toString();
        ContentValues values= getContentValues(crimeData);
        sqLiteDatabase.update(NAME,values, Cols.UUID + " = ?",new String[]{uuidString});
    }
    private static ContentValues getContentValues(CrimeData crimeData){
        ContentValues values=new ContentValues();
        values.put(Cols.UUID,crimeData.getId().toString());
        values.put(Cols.TITLE,crimeData.getTitle());
        values.put(Cols.DATE,crimeData.getDate().getTime());
        values.put(Cols.SOLVED,crimeData.isSolved()?1:0);
        values.put(Cols.SUSPECT,crimeData.getSuspect());
        return values;
    }
    private CrimeCursorWrapper queryCrimes(String whereClause,String[] whereArgs){
        Cursor cursor= sqLiteDatabase.query(
                NAME,null,whereClause,whereArgs,null,null,null);
        return  new CrimeCursorWrapper(cursor);
    }


}
