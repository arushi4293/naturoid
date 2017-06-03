package com.mapank.android.naturoid;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

public class PermissionDialogFragment extends DialogFragment {

    private static final String ARG_POSITIVE_BUTTON = "dialog.positiveButton";
    private static final String ARG_DESC = "dialog.desc";
    private static final String ARG_REQUEST_CODE = "dialog.requestCode";

    public static PermissionDialogFragment newInstance( int requestCode, int positiveButton, int desc ) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_POSITIVE_BUTTON, positiveButton);
        args.putSerializable(ARG_DESC, desc);
        args.putSerializable(ARG_REQUEST_CODE, requestCode);

        PermissionDialogFragment fragment = new PermissionDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public @NonNull Dialog onCreateDialog(Bundle savedInstanceState) {

        int positiveButton = getArguments().getInt(ARG_POSITIVE_BUTTON);
        int desc = getArguments().getInt(ARG_DESC);
        final int requestCode = getArguments().getInt(ARG_REQUEST_CODE);

        View v = View.inflate( getContext(), R.layout.dialog_permission, null);
        ((TextView) v.findViewById(R.id.textView_dialog)).setText( desc);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((FullImageActivity)getActivity()).onActivityResult(requestCode, Activity.RESULT_OK, null);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((FullImageActivity)getActivity()).onActivityResult(requestCode, Activity.RESULT_CANCELED, null);
                    }
                })
                .create();
    }
}
