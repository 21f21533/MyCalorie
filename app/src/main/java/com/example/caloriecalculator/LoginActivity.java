package com.example.caloriecalculator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button loginBtn;
    TextView registerLink;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        registerLink = findViewById(R.id.registerLink);
        dbHelper = new DBHelper(this);

        loginBtn.setOnClickListener(v -> {
            String userEmail = email.getText().toString();
            String userPassword = password.getText().toString();

            if (userEmail.equals("admin@admin.com") && userPassword.equals("admin123")) {
                Toast.makeText(this, "Admin Login", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, AdminActivity.class));
            } else if (dbHelper.checkUser(userEmail, userPassword)) {
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, CalorieInputActivity.class));
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        });

        registerLink.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }
}
