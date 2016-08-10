package com.example.zendynamix.criminalintent;

import com.example.zendynamix.criminalintent.crimedatabase.CrimeCursorWrapper;

import java.util.Date;
import java.util.UUID;

/**
 * Created by zendynamix on 6/21/2016.
 */
public class CrimeData {
    public static final String LOG_TAG=CrimeCursorWrapper.class.getSimpleName();
    private UUID id;
    private String title;
    private Date date;
    private boolean solved;
    private  String suspect;

    public CrimeData() {

        this(UUID.randomUUID());
    }

    public CrimeData(UUID uuid) {
        id = uuid;
        date = new Date();
    }


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getSuspect() {
        return suspect;
    }

    public void setSuspect(String suspect) {
        this.suspect = suspect;
    }


public String getPhotoFileName(){
    return "IMG_"+getId().toString()+".jpg";
}
}
