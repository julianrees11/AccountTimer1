package com.julian.accounttimer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimerActivity extends AppCompatActivity implements TimePicker.DialogListener {
    private long  START_TIME_IN_MILLIS = 0;
    private Button btnStart, btnCancel, btnFinished;
    private TextView tvTimer, tvClient;

    ProgressBar progressBar;

    private CountDownTimer countTimer;

    private boolean timerRunning;

    private long timeLeftInMillis;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Users");

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();

    String client, workType, work;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        tvTimer = findViewById(R.id.tvTimer);
        btnStart = findViewById(R.id.btnStart);
        btnCancel = findViewById(R.id.btnCancel);
        progressBar = findViewById(R.id.progressBar);
        tvClient = findViewById(R.id.tvClient);
        btnFinished = findViewById(R.id.btnFinished);

        client = getIntent().getStringExtra("CLIENT");
        workType = getIntent().getStringExtra("WORKTYPE");
        work = getIntent().getStringExtra("WORK");

        tvClient.setText("Client: " + client);

        TimePicker timePicker = new TimePicker();
        timePicker.show(getSupportFragmentManager(), "dialog");

        btnStart.setOnClickListener(view -> {
            if (timerRunning) {
                TimerActivity.this.pauseTimer();
            } else {
                progressBar.setMax((int) START_TIME_IN_MILLIS / 1000);
                btnCancel.setEnabled(false);
                startTimer();
            }
        });

        btnCancel.setOnClickListener(view -> {
            AlertDialog.Builder adb = new AlertDialog.Builder(TimerActivity.this);
            adb.setTitle("Cancel?");
            adb.setMessage("Are you sure you want to cancel your timer?");
            adb.setNegativeButton("No", null);
            adb.setPositiveButton("Yes", (dialog, which) -> startActivity(new Intent(TimerActivity.this, ListActivity.class)));
            adb.show();
        });

        btnFinished.setOnClickListener(view -> {
            int millisToSec = (int) timeLeftInMillis / 1000;
            int timeSpent = (int) START_TIME_IN_MILLIS - (millisToSec * 1000);

            String currentDate = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).format(new Date());

            myRef.child(user.getUid()).child("History").push().child(client).child(workType).child(currentDate).child(work).setValue(timeSpent);

            startActivity(new Intent(this, ListActivity.class));
        });
    }

    private void startTimer() {
        countTimer = new CountDownTimer(timeLeftInMillis, 1000) {

            @Override
            public void onTick(long l) {
                if (timeLeftInMillis < 11000) {
                    tvTimer.setTextColor(Color.RED);
                }
                timeLeftInMillis = l;

                progressBar.setProgress((int) l / 1000);

                updateCountDownText();
            }

            @Override
            public void onFinish() {
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                mp.start();

                btnFinished.setOnClickListener(v -> stopAlarm(mp));


                timerRunning = false;
                btnStart.setText("Play");
                tvTimer.setTextColor(Color.GRAY);
                btnCancel.setVisibility(View.VISIBLE);
                updateCountDownText();
            }
        }.start();

        updateCountDownText();
        timerRunning = true;
        btnStart.setText("Pause");
    }

    private void pauseTimer() {
        countTimer.cancel();
        timerRunning = false;
        btnStart.setText("Play");
        btnCancel.setEnabled(true);
    }

    private void updateCountDownText() {
        int hours = (int) (timeLeftInMillis/1000) / 3600;
        int minutes = (int) ((timeLeftInMillis / 1000) % 3600) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(),"%02d:%02d:%02d", hours, minutes, seconds);

        tvTimer.setText(timeLeftFormatted);
    }

    private void stopAlarm(MediaPlayer mp) {
        mp.stop();
    }

    @Override
    public void timerNum(int time) {
        START_TIME_IN_MILLIS = (time * 60) * 1000;
        timeLeftInMillis = START_TIME_IN_MILLIS;
        updateCountDownText();
    }
} //end of class
