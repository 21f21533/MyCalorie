package com.example.caloriecalculator;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity {

    EditText foodNameInput, calorieInput;
    Button addFoodBtn, logoutBtn;
    ListView foodListView;

    DBHelper dbHelper;
    ArrayList<String> foodList;
    ArrayAdapter<String> adapter;
    ArrayList<Integer> foodIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        dbHelper = new DBHelper(this);

        foodNameInput = findViewById(R.id.foodNameInput);
        calorieInput = findViewById(R.id.calorieInput);
        addFoodBtn = findViewById(R.id.addFoodBtn);
        logoutBtn = findViewById(R.id.logoutBtn);
        foodListView = findViewById(R.id.foodListView);

        foodList = new ArrayList<>();
        foodIds = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, foodList);
        foodListView.setAdapter(adapter);

        loadFoodItems();

        addFoodBtn.setOnClickListener(v -> {
            String foodName = foodNameInput.getText().toString().trim();
            String calorieStr = calorieInput.getText().toString().trim();

            if (foodName.isEmpty() || calorieStr.isEmpty()) {
                Toast.makeText(this, "Enter both food name and calories", Toast.LENGTH_SHORT).show();
                return;
            }

            int calories;
            try {
                calories = Integer.parseInt(calorieStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Calories must be a number", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success = dbHelper.insertFoodItem(foodName, calories);
            if (success) {
                Toast.makeText(this, "Food item added/updated", Toast.LENGTH_SHORT).show();
                foodNameInput.setText("");
                calorieInput.setText("");
                loadFoodItems();
            } else {
                Toast.makeText(this, "Error saving item", Toast.LENGTH_SHORT).show();
            }
        });

        foodListView.setOnItemClickListener((parent, view, position, id) -> {
            int foodId = foodIds.get(position);
            String selectedItem = foodList.get(position);

            AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
            builder.setTitle("Choose an action");
            builder.setItems(new CharSequence[]{"Edit", "Delete"}, (dialog, which) -> {
                if (which == 0) { // Edit
                    String[] parts = selectedItem.split(" - ");
                    if (parts.length == 2) {
                        String name = parts[0].trim();
                        String cal = parts[1].replace("kcal", "").trim();
                        foodNameInput.setText(name);
                        calorieInput.setText(cal);
                    }
                } else if (which == 1) { // Delete
                    boolean deleted = dbHelper.deleteFoodItemById(foodId);
                    if (deleted) {
                        Toast.makeText(AdminActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
                        loadFoodItems();
                    } else {
                        Toast.makeText(AdminActivity.this, "Failed to delete", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.show();
        });

        logoutBtn.setOnClickListener(v -> {
            Toast.makeText(this, "Admin logged out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AdminActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void loadFoodItems() {
        foodList.clear();
        foodIds.clear();
        Cursor cursor = dbHelper.getAllFoodItems();

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String food = cursor.getString(1);
                int cal = cursor.getInt(2);
                foodList.add(food + " - " + cal + " kcal");
                foodIds.add(id);
            } while (cursor.moveToNext());
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }
}
