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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class InfoViewer extends DialogFragment {

    private Spinner spnr1, spnr2, spnr3;

    private DialogListener listener;

    private ArrayList<String> listOfClients, listOfWorkTypes, listOfWork;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("Users");

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = mAuth.getCurrentUser();

    private ArrayAdapter clients, workType, work;

    private String client, worktype, Work;

    private boolean clientSelected = false;
    private boolean workTypeSelected = false;
    private boolean workSelected = false;

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
        View view = inflater.inflate(R.layout.info_viewer, null);

        builder.setView(view).setTitle("Choose Client")
                .setNegativeButton("Cancel", (dialog1, which) -> {
                    AlertDialog dialog = builder.create();
                    dialog.dismiss();
                })
                .setPositiveButton("Done", (dialog2, which) -> {
                    AlertDialog dialog = builder.create();
                    listener.clientInfo(client, worktype, Work);
                    dialog.dismiss();
                });

        AlertDialog dialog = builder.create();

        dialog.setCancelable(false);

        spnr1 = view.findViewById(R.id.spnrChooseClient);
        spnr2 = view.findViewById(R.id.spnrChooseWorkType);
        spnr3 = view.findViewById(R.id.spnrChooseWork);

        listOfClients = new ArrayList<>();
        listOfWorkTypes = new ArrayList<>();
        listOfWork = new ArrayList<>();

        clients = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, listOfClients);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot snapshot = dataSnapshot.child(user.getUid()).child("Clients");

                Iterable<DataSnapshot> iterable = snapshot.getChildren();

                listOfClients.clear();

                listOfClients.add("None");

                for (DataSnapshot messages : iterable) {
                    listOfClients.add(messages.getValue().toString());
                }


                clients.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                spnr1.setAdapter(clients);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        spnr1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String spnr1Selected = parent.getItemAtPosition(position).toString();

                if (!spnr1Selected.equals("None")) {
                    clientSelected = true;

                    if (clientSelected && workSelected && workTypeSelected) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    }

                    client = parent.getItemAtPosition(position).toString();
                }else {
                    clientSelected = false;
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }

            }

            @Override public void onNothingSelected(AdapterView<?> parent){}
        });

        listOfWorkTypes.add("None");
        listOfWorkTypes.add("Audit");
        listOfWorkTypes.add("Review");
        listOfWorkTypes.add("Notice to reader");
        workType = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, listOfWorkTypes);
        workType.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spnr2.setAdapter(workType);

        spnr2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String spnr2Selected = parent.getItemAtPosition(position).toString();

                if (!spnr2Selected.equals("None")) {
                    workTypeSelected = true;

                    if (clientSelected && workSelected && workTypeSelected) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    }

                    worktype = parent.getItemAtPosition(position).toString();

                    if (spnr2Selected.equals("Audit")){
                        spnr3.setEnabled(true);

                        listOfWork.clear();

                        listOfWork.add("Overall strategy and planning");
                        listOfWork.add("Supervision and review");
                        listOfWork.add("Audit team communication");
                        listOfWork.add("Client meetings");
                        listOfWork.add("Financial statement reports and communications");
                        listOfWork.add("Administration");
                        listOfWork.add("Travel and out of pocket");
                        listOfWork.add("Financial statement analysis");
                        listOfWork.add("Planning risk assessment procedures");
                        listOfWork.add("Assessing results");
                        listOfWork.add("Planning risk response procedures");
                        listOfWork.add("Overall response and fraud risk");
                        listOfWork.add("Cash and investment");
                        listOfWork.add("Account receivable");
                        listOfWork.add("Inventory");
                        listOfWork.add("Long-term liabilities");
                        listOfWork.add("Property, plant and equipment");
                        listOfWork.add("Intangibles and goodwill");
                        listOfWork.add("Bank debt and notes payable");
                        listOfWork.add("Accounts payable and accruals");
                        listOfWork.add("Income taxes");
                        listOfWork.add("Contingencies/subsequent events");
                        listOfWork.add("Share capital and retained earnings");
                        listOfWork.add("Related parties");
                        listOfWork.add("Sales");
                        listOfWork.add("Cost of sales");
                        listOfWork.add("Payroll");
                        listOfWork.add("Operating expenses");
                        listOfWork.add("Other income or expense");

                        work = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, listOfWork);
                        work.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                        spnr3.setAdapter(work);
                    }else if (spnr2Selected.equals("Review")){
                        spnr3.setEnabled(true);

                        listOfWork.clear();

                        listOfWork.add("Accepting and planning");
                        listOfWork.add("Performing (inquiries and analysis)");
                        listOfWork.add("Additional procedures");
                        listOfWork.add("Reporting");
                        listOfWork.add("Supervision");
                        listOfWork.add("File Review");
                        listOfWork.add("Client meeting");
                        listOfWork.add("Financial statements");

                        work = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, listOfWork);
                        work.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                        spnr3.setAdapter(work);
                    }else {
                        spnr3.setEnabled(true);

                        listOfWork.clear();

                        listOfWork.add("Asset section");
                        listOfWork.add("Liability section");
                        listOfWork.add("Income section");
                        listOfWork.add("Expense section");
                        listOfWork.add("Financial statement section");
                        listOfWork.add("Cooperate tax return section");
                        listOfWork.add("Review");
                        listOfWork.add("Meeting client");

                        work = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, listOfWork);
                        work.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                        spnr3.setAdapter(work);
                    }
                }else {
                    workTypeSelected = false;
                    spnr3.setAdapter(null);
                    spnr3.setEnabled(false);
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
            }

            @Override public void onNothingSelected(AdapterView<?> parent){}
        });

        spnr3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                workSelected = true;

                if (clientSelected && workSelected && workTypeSelected) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }

                Work = parent.getItemAtPosition(position).toString();
            }

            @Override public void onNothingSelected(AdapterView<?> parent){}
        });


        dialog.setOnShowListener(dialog3 -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            spnr3.setEnabled(false);
        });

        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    public interface DialogListener {
        void clientInfo(String client, String workType, String work);
    }
}
