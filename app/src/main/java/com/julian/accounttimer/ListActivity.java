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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    TextView tvGreeting;

    EditText etAdd;

    Button btnLogout;
    Button btnProfile;
    Button btnHome;
    Button btnAdd;
    Button btnHistory;

    ListView listView;

    ArrayAdapter myList;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Users");

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user;

    ArrayList<String> listOfMessages = new ArrayList<>();

    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        tvGreeting = findViewById(R.id.tvGreeting);
        btnLogout = findViewById(R.id.btnLogout1);
        btnProfile = findViewById(R.id.btnProfile2);
        btnHome = findViewById(R.id.btnHome2);
        btnAdd = findViewById(R.id.btnAdd);
        etAdd = findViewById(R.id.etAdd);
        listView = findViewById(R.id.lvList);
        btnHistory = findViewById(R.id.btnHistory);

        user = mAuth.getCurrentUser();

        System.out.println(user.getDisplayName());

        tvGreeting.setText("Hi, " + user.getDisplayName());

        saveData();

        btnHistory.setOnClickListener(v -> startActivity(new Intent(this, HistoryActivity.class)));

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(ListActivity.this, ListActivity.class);
            startActivity(intent);
        });

        btnProfile.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(ListActivity.this);
            builder.setTitle("Choose a client");

            Intent intent = new Intent(ListActivity.this, TimerActivity.class);

            builder.setItems(listOfMessages.toArray(new String[0]), (dialog, which) -> {
                intent.putExtra("CLIENT", listOfMessages.get(which));
                System.out.println(listOfMessages.get(which));
                startActivity(intent);
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();


        });

        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(ListActivity.this, ListActivity.class);
            startActivity(intent);
        });

        btnAdd.setOnClickListener(v -> {
            if(!etAdd.getText().toString().isEmpty()){
                myRef.child(user.getUid()).child("Clients").child(etAdd.getText().toString()).setValue(etAdd.getText().toString());
                etAdd.setText("");
            }else Toast.makeText(ListActivity.this, "Please fill in the required field.", Toast.LENGTH_SHORT).show();
        });

        listView.setOnItemClickListener(this);
    }

    public void saveData() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot snapshot = dataSnapshot.child(user.getUid()).child("Clients");

                Iterable<DataSnapshot> iterable = snapshot.getChildren();

                ArrayList<String> clone;
                clone = listOfMessages;

                listOfMessages.clear();

                for (DataSnapshot messages : iterable) {
                    if(!messages.getValue().toString().contains(clone.toString())) {
                        listOfMessages.add(messages.getValue().toString());
                    }
                }


                myList = new ArrayAdapter<>(ListActivity.this,android.R.layout.simple_list_item_1, listOfMessages);

                listView.setAdapter(myList);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        AlertDialog.Builder adb = new AlertDialog.Builder(ListActivity.this);
        adb.setTitle("Delete?");
        adb.setMessage("Are you sure you want to delete " + listOfMessages.get(position) + "?");
        adb.setNegativeButton("Cancel", null);
        adb.setPositiveButton("Ok", (dialog, which) -> {
            myRef.child(user.getUid()).child("Clients").child(listOfMessages.get(position)).removeValue();
            listOfMessages.remove(position);
            myList.notifyDataSetChanged();
        });
        adb.show();
    }
}
