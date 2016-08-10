package com.example.zendynamix.criminalintent;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.zendynamix.criminalintent.utils.PictureUtils;

import java.io.File;
import java.util.Date;
import java.util.UUID;

import static android.content.Intent.ACTION_DIAL;
import static android.content.Intent.ACTION_PICK;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

/**
 * Created by zendynamix on 6/21/2016.
 */
public class CriminalFragment extends Fragment {
    private static final String LOG_TAG = CriminalFragment.class.getSimpleName();
    private static final String ARGUMENTS_CRIME_ID = "crime_id";
    private static final int REQUEST_CONTECT = 1;
    private static final String DIALOG_DATE = "dialog_date";
    private static final String IMAGE_DIALOG = "imageDialog";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_PHONE = 2;
    private static final int REQUEST_PHOTO = 3;
    private CrimeData crimeData;
    private EditText titleField;
    private Button dateButton;
    private CheckBox solvedCheckBox;
    private Button sendReportButton;
    private Button suspectButton;
    private Button callSuspectButton;
    private ImageView photoView;
    private ImageButton photoCameraButton;
    private File photoFile;
    private Callbacks callbacks;


    // interface for hosting activity
    public interface Callbacks{
        void  onCrimeUpdated(CrimeData crimeData);
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        callbacks=(Callbacks)activity;
    }

    public static CriminalFragment newInstance(UUID crimeID) {
        Bundle args = new Bundle();
        args.putSerializable(ARGUMENTS_CRIME_ID, crimeID);
        CriminalFragment criminalFragment = new CriminalFragment();
        criminalFragment.setArguments(args);
        return criminalFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crime_id = (UUID) getArguments().getSerializable(ARGUMENTS_CRIME_ID);
        crimeData = CrimeLab.get(getActivity()).getCrime(crime_id);
        photoFile = CrimeLab.get(getActivity()).getPhotoFile(crimeData);
        setHasOptionsMenu(true);
    }


    private  void updateCrime(){
        CrimeLab.get(getActivity()).updateCrime(crimeData);
        callbacks.onCrimeUpdated(crimeData);
    }

    private Cursor queryContacts(Uri uri, String[] fields, String whereClause, String[] args) {
        Cursor c = getActivity().getContentResolver().query(uri, fields, whereClause, args, null);
        if (c.getCount() == 0) {
            c.close();
            return null;
        }
        c.moveToFirst();
        return c;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            crimeData.setDate(date);
            updateCrime();
            updateDate();
        } else if (requestCode == REQUEST_CONTECT || requestCode == REQUEST_PHONE && data != null) {
            Uri contactUri = data.getData();
            int suspectID;
            String phoneNumber;
            // specify fields you want your query to return value for
            String[] queryFields = new String[]{ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts._ID};
            // perform your query the contactUri is like a where clause here
            Cursor c = queryContacts(contactUri, queryFields, null, null);
            try {

                if (c.getCount() == 0) {
                    return;
                }
                // pull first column of first row of data, this is your suspected name
                c.moveToFirst();
                String suspect = c.getString(0);
                suspectID = c.getInt(1);
                crimeData.setSuspect(suspect);
                updateCrime();
                suspectButton.setText(suspect);
            } finally {
                c.close();
            }
            if (requestCode == REQUEST_PHONE) {
                queryFields = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
                String whereClause = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =?";
                String[] arg = {Integer.toString(suspectID)};
                c = queryContacts(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, queryFields, whereClause, arg);
                if (c == null) {
                    return;
                }
                try {
                    phoneNumber = c.getString(0);
                } finally {
                    c.close();
                }

                Uri phoneUri = Uri.parse("tel:" + phoneNumber);
                Intent i = new Intent(ACTION_DIAL, phoneUri);
                startActivity(i);

            } else if (requestCode == REQUEST_PHOTO) {
                updatePhotoView();
                updateCrime();
            }
        }
    }

    private void updateDate() {
        dateButton.setText(crimeData.getDate().toString());
    }

    private String getCrimeReport() {
        String solvedString = null;
        if (crimeData.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }
        String dateFormat = "EEE,MMM,dd";
        String dateString = DateFormat.format(dateFormat, crimeData.getDate()).toString();
        String suspect = crimeData.getSuspect();
        // File image = CrimeLab.get(getActivity()).getPhotoFile(crimeData);
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }
        String report = getString(R.string.crime_report, crimeData.getTitle(), dateString, solvedString, suspect);
        return report;
    }
    private void updatePhotoView() {
        if (photoFile == null || !photoFile.exists()) {
            photoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getscaledBitmap(photoFile.getPath(), getActivity());
            photoView.setImageBitmap(bitmap);
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_crime, container, false);
        titleField = (EditText) rootView.findViewById(R.id.crime_title);
        titleField.setText(crimeData.getTitle());
        titleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //intentanly left blank
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                crimeData.setTitle(s.toString());
                updateCrime();

            }

            @Override
            public void afterTextChanged(Editable s) {
                //left blank
            }
        });
        dateButton = (Button) rootView.findViewById(R.id.crime_date);
        updateDate();
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(crimeData.getDate());
                dialog.setTargetFragment(CriminalFragment.this, REQUEST_DATE);
                dialog.show(fragmentManager, DIALOG_DATE);


            }

        });
        sendReportButton = (Button) rootView.findViewById(R.id.crime_report);
        sendReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText(getCrimeReport())
                        .setSubject(getString(R.string.crime_report_subject))
                        .setChooserTitle(R.string.send_report)
                        .startChooser();
            }
        });

        final Intent pickContacts = new Intent(ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        //pickContacts.addCategory(Intent.CATEGORY_HOME);
        suspectButton = (Button) rootView.findViewById(R.id.crime_suspect);
        suspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivityForResult(pickContacts, REQUEST_CONTECT);
            }
        });
        if (crimeData.getSuspect() != null) {
            suspectButton.setText(crimeData.getSuspect());

        }
        PackageManager packageManager = getActivity().getPackageManager();

        if (packageManager.resolveActivity(pickContacts, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            suspectButton.setEnabled(false);
        }

        final Intent pickContectsPhone = new Intent(ACTION_PICK, (ContactsContract.Contacts.CONTENT_URI));
        //pickContectsPhone.addCategory(Intent.CATEGORY_HOME);
        callSuspectButton = (Button) rootView.findViewById(R.id.call_suspect);
        callSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContectsPhone, REQUEST_PHONE);
            }
        });
        if (packageManager.resolveActivity(pickContectsPhone, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            callSuspectButton.setEnabled(false);
        }

        solvedCheckBox = (CheckBox) rootView.findViewById(R.id.crime_solved);
        solvedCheckBox.setChecked(crimeData.isSolved());
        solvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                crimeData.setSolved(isChecked);
                updateCrime();
            }
        });

        photoCameraButton = (ImageButton) rootView.findViewById(R.id.crime_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = photoFile != null && captureImage.resolveActivity(packageManager) != null;
        if (canTakePhoto) {
            Uri uri = Uri.fromFile(photoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        photoCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(captureImage, REQUEST_PHOTO);

            }
        });
        photoView = (ImageView) rootView.findViewById(R.id.crime_image);
        updatePhotoView();

        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (photoFile != null) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    LargeImageFragment dialogFragmet = LargeImageFragment.newInstance(photoFile);
                    dialogFragmet.show(fragmentManager, IMAGE_DIALOG);}
            }
        });


        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).updateCrime(crimeData);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.crime_fragment, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_item_delete_crime) {
            UUID crime_id = (UUID) getArguments().getSerializable(ARGUMENTS_CRIME_ID);
            if (crime_id != null) {
                CrimeLab.get(getActivity()).deleteCrime(crime_id);
                Toast toast = Toast.makeText(getActivity(), "Crime Deleted", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
                Intent intent = new Intent(getActivity(), CrimeListActivity.class);
                intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbacks=null;

    }

    @Override
    public void onResume() {
        super.onResume();
    }
}

