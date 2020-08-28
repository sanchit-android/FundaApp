package com.sanchit.funda.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import com.sanchit.funda.R;

public class SortingOptionDialog extends DialogFragment {

    private final String currentSortOption;

    SortingDialogListener listener;
    String selection;

    public SortingOptionDialog(String currentSortOption) {
        this.currentSortOption = currentSortOption;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (SortingDialogListener) context;
        //selection = getContext().getResources().getStringArray(R.array.sort_options)[currentSortOption];
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String[] sortOptions = getResources().getStringArray(R.array.sort_options);

        int index = sortOptions.length - 1;
        if (currentSortOption != null) {
            for (int i = 0; i < sortOptions.length; i++) {
                if (currentSortOption.equals(sortOptions[i])) {
                    index = i;
                    break;
                }
            }
        }

        builder.setTitle(R.string.Sort_Funds)
                .setSingleChoiceItems(R.array.sort_options, index, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selection = sortOptions[which];
                    }
                })
                .setPositiveButton(R.string.Sort, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDialogSortClick(SortingOptionDialog.this, selection);
                    }
                })
                .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public interface SortingDialogListener {

        public void onDialogSortClick(DialogFragment dialog, String sortOption);

        public void onDialogCancelClick(DialogFragment dialog);
    }
}
