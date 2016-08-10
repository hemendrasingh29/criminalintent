package com.example.zendynamix.criminalintent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.example.zendynamix.criminalintent.utils.PictureUtils;

import java.io.File;


/**
 * Created by zendynamix on 7/2/2016.
 */
public class LargeImageFragment extends DialogFragment {
    private static final String LOG_TAG = "data in latgfragmnt";
    private static final String IMAGE_DATA = "imageData";
    ImageView imageLarge;

    public static LargeImageFragment newInstance(File file) {
        Bundle args = new Bundle();
        args.putSerializable(IMAGE_DATA, file);
        LargeImageFragment largeImageFragment = new LargeImageFragment();
        largeImageFragment.setArguments(args);
        return largeImageFragment;
    }


    public Dialog onCreateDialog(Bundle savedInstanceState) {


        File file = (File) getArguments().getSerializable(IMAGE_DATA);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.large_image,null);
        Bitmap bitmap = PictureUtils.getscaledBitmap(file.getPath(), getActivity());
        imageLarge = (ImageView) view.findViewById(R.id.image_view_large);
        imageLarge.setImageBitmap(bitmap);
        if(bitmap==null){

            return new  AlertDialog.Builder(getActivity()).setIcon(R.mipmap.ic_launcherimg).setTitle(R.string.image_message)
                    .setPositiveButton(android.R.string.ok, null).create();
        } else {
            return new

                    AlertDialog.Builder(getActivity()).setView(view).setTitle(R.string.image_title)
                    .setPositiveButton(android.R.string.ok, null).create();
        }

    }

    @Override
    public void onResume() {
        super.onResume();

    }
}
