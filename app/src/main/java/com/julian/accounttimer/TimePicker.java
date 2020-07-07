package com.julian.accounttimer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class TimePicker extends DialogFragment {

    private EditText etTime;
    private DialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (DialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement DialogListener.");
        }
    }

    @Override
    public android.app.Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setCancelable(false);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.time_picker, null);

        etTime = view.findViewById(R.id.etTime);

        builder.setView(view)
                .setTitle("Set Time")
                .setNegativeButton("Cancel", (dialog, which) -> startActivity(new Intent(getActivity(), ListActivity.class)))
                .setPositiveButton("ok",  (dialog, which) -> listener.timerNum(Integer.parseInt(etTime.getText().toString())));

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);

        etTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!etTime.getText().toString().isEmpty()){
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }else dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s){}
        });

        dialog.setOnShowListener(dialog1 -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false));

        return dialog;
    }

    public interface DialogListener {
        void timerNum(int time);
    }
}
