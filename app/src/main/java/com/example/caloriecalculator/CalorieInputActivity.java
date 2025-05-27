package com.example.caloriecalculator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.*;

public class CalorieInputActivity extends AppCompatActivity {

    EditText foodName;
    TextView calorieDisplay;
    Button fetchBtn, saveBtn;
    ListView listView;
    TextView totalCaloriesText;

    DBHelper dbHelper;
    ArrayList<String> calorieList;
    ArrayAdapter<String> adapter;
    int totalCalories = 0;


    int userId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calorie_input);

        dbHelper = new DBHelper(this);

        foodName = findViewById(R.id.foodName);
        calorieDisplay = findViewById(R.id.calorieDisplay);
        fetchBtn = findViewById(R.id.fetchBtn);
        saveBtn = findViewById(R.id.saveBtn);
        listView = findViewById(R.id.foodListView);
        totalCaloriesText = findViewById(R.id.totalCaloriesText);

        calorieList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, calorieList);
        listView.setAdapter(adapter);

        fetchBtn.setOnClickListener(v -> {
            String name = foodName.getText().toString().trim();
            int calories = dbHelper.getCaloriesByFoodName(name);
            if (calories != -1) {
                calorieDisplay.setText(String.valueOf(calories));
            } else {
                calorieDisplay.setText("Not Found");
                Toast.makeText(this, "Food not found in database", Toast.LENGTH_SHORT).show();
            }
        });

        saveBtn.setOnClickListener(v -> {
            String name = foodName.getText().toString().trim();
            String calStr = calorieDisplay.getText().toString().trim();

            if (name.isEmpty() || calStr.equals("Not Found") || calStr.isEmpty()) {
                Toast.makeText(this, "Valid food and calories required", Toast.LENGTH_SHORT).show();
                return;
            }

            int calories = Integer.parseInt(calStr);
            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            boolean saved = dbHelper.insertUserCalorieEntry(userId, name, calories, today);
            if (saved) {
                calorieList.add(name + " - " + calories + " kcal");
                adapter.notifyDataSetChanged();
                totalCalories += calories;
                totalCaloriesText.setText("Total: " + totalCalories + " kcal");
                foodName.setText("");
                calorieDisplay.setText("");
                Toast.makeText(this, "Entry Saved", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Save failed", Toast.LENGTH_SHORT).show();
            }
        });
        Button logoutBtn = findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(v -> {
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(CalorieInputActivity.this, LoginActivity.class));
            finish();
        });

    }
}
