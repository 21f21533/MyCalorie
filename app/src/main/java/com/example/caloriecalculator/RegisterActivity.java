package com.example.caloriecalculator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    EditText name, email, password;
    Button registerBtn;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        TextView loginLink = findViewById(R.id.loginLink);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        registerBtn = findViewById(R.id.registerBtn);
        dbHelper = new DBHelper(this);

        registerBtn.setOnClickListener(v -> {
            String userName = name.getText().toString();
            String userEmail = email.getText().toString();
            String userPassword = password.getText().toString();

            if (userName.isEmpty() || userEmail.isEmpty() || userPassword.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                boolean inserted = dbHelper.insertUser(userName, userEmail, userPassword);
                if (inserted) {
                    Toast.makeText(this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                } else {
                    Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
        loginLink.setOnClickListener(v -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));
    }
}
