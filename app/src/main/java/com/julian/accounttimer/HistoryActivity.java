package com.julian.accounttimer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    Button btnLogout;
    Button btnHome;
    Button btnHistory;

    ListView listView;

    ArrayAdapter myList;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Users");

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user;

    ArrayList<String> listOfMessages = new ArrayList<>();
    ArrayList<String> listOfNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        btnLogout = findViewById(R.id.btnLogoutHistoryActivity);
        btnHome = findViewById(R.id.btnHomeHistoryActivity);
        listView = findViewById(R.id.lvListHistoryActivity);
        btnHistory = findViewById(R.id.btnHistoryListActivity);

        user = mAuth.getCurrentUser();

        System.out.println(user.getDisplayName());

        saveData();

        btnHistory.setOnClickListener(v -> startActivity(new Intent(this, HistoryActivity.class)));

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(this, ListActivity.class);
            startActivity(intent);
        });

        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, ListActivity.class);
            startActivity(intent);
        });

        listView.setOnItemClickListener(this);
    }

    public void saveData() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot snapshot = dataSnapshot.child(user.getUid()).child("History");

                Iterable<DataSnapshot> iterable = snapshot.getChildren();

                ArrayList<String> clone;
                clone = listOfMessages;

                listOfMessages.clear();
                listOfNames.clear();

                for (DataSnapshot messages : iterable) {
                    if(!messages.getValue().toString().contains(clone.toString())) {
                        listOfMessages.add(messages.getValue().toString());
                        listOfNames.add(messages.getKey());
                    }
                }

                myList = new ArrayAdapter<>(HistoryActivity.this, android.R.layout.simple_list_item_1, listOfMessages);

                listView.setAdapter(myList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Delete?");
        adb.setMessage("Are you sure you want to delete " + listOfMessages.get(position) + "?");
        adb.setNegativeButton("Cancel", null);
        adb.setPositiveButton("Ok", (dialog, which) -> {
            myRef.child(user.getUid()).child("History").child(listOfNames.get(position)).removeValue();
            myList.notifyDataSetChanged();
        });
        adb.show();
    }
}
