package com.julian.accounttimer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin, btnGoToRegister;

    static final String TAG = "EmailPassword";

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    boolean emailNotEmpty = false;
    boolean passNotEmpty = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etEmail= findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoToRegister = findViewById(R.id.btnGoToRegister);

        btnLogin.setEnabled(false);

        btnLogin.setOnClickListener(v -> {
            String email, password;

            email = etEmail.getText().toString().trim();
            password = etPassword.getText().toString().trim();
            //contains something + contains something
            if(!email.isEmpty() && !password.isEmpty()){
                signIn(email, password);
            }
        });

        btnGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        etEmail.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!etEmail.getText().toString().isEmpty()){
                    emailNotEmpty = true;

                    if (passNotEmpty && emailNotEmpty) {
                        btnLogin.setEnabled(true);
                    }
                } else {
                    emailNotEmpty = false;
                    btnLogin.setEnabled(false);
                }
            }

            @Override public void afterTextChanged(Editable s) {}
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!etPassword.getText().toString().isEmpty()){
                    passNotEmpty = true;

                    if (emailNotEmpty && passNotEmpty) {
                        btnLogin.setEnabled(true);
                    }
                } else {
                    passNotEmpty = false;
                    btnLogin.setEnabled(false);
                }
            }

            @Override public void afterTextChanged(Editable s) {}
        });
    }

    public void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success");
                    Intent intent = new Intent(MainActivity.this, ListActivity.class);
                    startActivity(intent);
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    Toast.makeText(MainActivity.this, "Authentication failed, " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            });
    }
}