package com.example.root.garminblecompteur.alertDialogRepo;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.TextView;

/**
 * Created by cyrilstern1 on 17/09/2017.
 */

public class AlertDialogCompose extends DialogFragment implements TextView.OnEditorActionListener {
    private String message;
    private String buttonOk;
    private String buttonCancel;

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }
    NoticeDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.message = getArguments().getString("message");
        this.buttonOk = getArguments().getString("buttonOk");
        this.buttonCancel = getArguments().getString("buttonCancel");

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setPositiveButton(buttonOk, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogPositiveClick(AlertDialogCompose.this);

                        // FIRE ZE MISSILES!
                    }
                })
                .setNegativeButton(buttonCancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        mListener.onDialogNegativeClick(AlertDialogCompose.this);

                    }
                });
        // Create the AlertDialog object and return it
        Dialog dialog = builder.create();
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }
    public static AlertDialogCompose newInstance(String message, String buttonOk, String buttonCancel){
        AlertDialogCompose myFragment = new AlertDialogCompose();
        Bundle args = new Bundle();
        args.putString("message", message);
        args.putString("buttonOk", buttonOk);
        args.putString("buttonCancel", buttonCancel);
        myFragment.setArguments(args);
        return myFragment;

    }

    @Override
    public void show(android.app.FragmentManager manager, String tag) {
        super.show(manager, tag);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        return false;
    }
}
