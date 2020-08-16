package com.julian.accounttimer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
import java.util.Collections;

public class HistoryActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    Button btnHome;
    Button btnHistory;

    ListView listView;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Users");

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user;

    ArrayList<String> list = new ArrayList<>();
    ArrayList<String> listOfPushes = new ArrayList<>();
    ArrayList<String> listOfClients = new ArrayList<>();
    ArrayList<TaskSaver> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        btnHome = findViewById(R.id.btnHomeHistoryActivity);
        listView = findViewById(R.id.lvListHistoryActivity);
        btnHistory = findViewById(R.id.btnHistoryListActivity);

        user = mAuth.getCurrentUser();

        System.out.println(user.getDisplayName());

        saveData();

        btnHistory.setOnClickListener(v -> startActivity(new Intent(this, HistoryActivity.class)));

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

                arrayList.clear();
                listOfClients.clear();
                listOfPushes.clear();

                for (DataSnapshot ds1 : snapshot.getChildren()){
                    list.clear();

                    list.add(ds1.getKey()); //Unique id (0)
                    listOfPushes.add(ds1.getKey());

                    for (DataSnapshot ds2 : snapshot.child(list.get(0)).getChildren()){
                        list.add(ds2.getKey()); //Client name (1)
                        listOfClients.add(ds2.getKey());

                        for (DataSnapshot ds3 : snapshot.child(list.get(0)).child(list.get(1)).getChildren()){
                            list.add(ds3.getKey()); //Engagement (2)

                            for (DataSnapshot ds4 : snapshot.child(list.get(0)).child(list.get(1)).child(list.get(2)).getChildren()){
                                list.add(ds4.getKey()); //Date (3)

                                for (DataSnapshot ds5 : snapshot.child(list.get(0)).child(list.get(1)).child(list.get(2)).child(list.get(3)).getChildren()) {
                                    list.add(ds5.getKey()); //Section (4)
                                    list.add(ds5.getValue().toString()); //Time spent (5)
                                }
                            }
                        }

                        TaskSaver taskSaver = new TaskSaver();

                        taskSaver.setClient(list.get(1));
                        taskSaver.setWorkType(list.get(2));
                        taskSaver.setDate(list.get(3));
                        taskSaver.setWork(list.get(4));
                        taskSaver.setTimeInMillis(Integer.parseInt(list.get(5)));

                        arrayList.add(taskSaver);
                    }

                    Collections.reverse(arrayList);

                    TaskSaverAdapter taskSaverAdapter = new TaskSaverAdapter(getApplicationContext(), R.layout.custom_lv, arrayList);
                    listView.setAdapter(taskSaverAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        Collections.reverse(listOfClients);
        Collections.reverse(listOfPushes);

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setMessage("Are you sure you want to delete " + listOfClients.get(position) + "?");
        adb.setPositiveButton("Yes", (dialog, which) -> myRef.child(user.getUid()).child("History").child(listOfPushes.get(position)).removeValue());
        adb.setNegativeButton("No", null);
        adb.create();
        adb.show();
    }
}
