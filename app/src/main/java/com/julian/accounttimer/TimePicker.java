package com.julian.accounttimer;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
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

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.time_picker, null);

        etTime = view.findViewById(R.id.etTime);

        builder.setView(view)
                .setTitle("Set Time")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("ok",  (dialog, which) ->{
                    listener.timerNum(Integer.parseInt(etTime.getText().toString()));
                });


        return builder.create();
    }

    public interface DialogListener {
        void timerNum(int time);
    }
}
