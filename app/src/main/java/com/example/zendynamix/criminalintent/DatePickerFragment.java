package com.example.zendynamix.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


  //Created by zendynamix on 6/23/2016.

public class DatePickerFragment extends DialogFragment {
    private static final String LOG_TAG=DatePickerFragment.class.getSimpleName();
    private static final String ARG_DATE = "date";
    private DatePicker datePicker;
    public static final String EXTRA_DATE= "com.example.zendynamix.criminalintent.date";

    public static DatePickerFragment newInstance(Date date) {
        Bundle arg = new Bundle();
        arg.putSerializable(ARG_DATE, date);
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setArguments(arg);
        return datePickerFragment;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Date date = (Date) getArguments().getSerializable(ARG_DATE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date, null);
        datePicker = (DatePicker) view.findViewById(R.id.dialog_date_date_picker);
        datePicker.init(year, month, day, null);

        return new AlertDialog.Builder(getActivity()).setView(view)
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int year1=datePicker.getYear();
                        int month1=datePicker.getMonth();
                        int day1=datePicker.getDayOfMonth();
                        Date date1= new GregorianCalendar(year1,month1,day1).getTime();
                        sendResult(Activity.RESULT_OK,date1);
                    }
                })
                .create();
    }
    // creating result to send to criminal activityFragment
    private  void sendResult(int resultCode,Date date1){
        if(getTargetFragment()==null){
            return;
        }
        Intent intent=new Intent();
        intent.putExtra(EXTRA_DATE,date1);
        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,intent);
    }


}
