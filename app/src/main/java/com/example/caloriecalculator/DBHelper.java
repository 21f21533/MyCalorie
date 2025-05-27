package com.example.caloriecalculator;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "CalorieCalc.db";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // User Table
        db.execSQL("CREATE TABLE users(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, email TEXT, password TEXT)");

        // Calories Entry Table
        db.execSQL("CREATE TABLE calories(id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, food_name TEXT, calorie_value INTEGER, date TEXT)");

        // Food Table
        db.execSQL("CREATE TABLE food_table(id INTEGER PRIMARY KEY AUTOINCREMENT, food_name TEXT UNIQUE, calories INTEGER)");

        // Insert 10 default food items
        db.execSQL("INSERT INTO food_table(food_name, calories) VALUES" +
                "('Rice', 100)," +
                "('Chicken', 165)," +
                "('Egg', 78)," +
                "('Apple', 52)," +
                "('Bread', 66)," +
                "('Banana', 89)," +
                "('Milk', 103)," +
                "('Cheese', 113)," +
                "('Orange', 62)," +
                "('Potato', 77)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS calories");
        db.execSQL("DROP TABLE IF EXISTS food_table");
        onCreate(db);
    }

    // User registration
    public boolean insertUser(String name, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("email", email);
        values.put("password", password);
        long result = db.insert("users", null, values);
        return result != -1;
    }

    // User login check
    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email=? AND password=?", new String[]{email, password});
        return cursor.getCount() > 0;
    }

    // Admin inserts food item
    public boolean insertFoodItem(String foodName, int calories) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("food_name", foodName);
        values.put("calories", calories);
        long result = db.insertWithOnConflict("food_table", null, values, SQLiteDatabase.CONFLICT_REPLACE);
        return result != -1;
    }

    // Get calories by food name
    public int getCaloriesByFoodName(String foodName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT calories FROM food_table WHERE food_name = ?", new String[]{foodName});
        if (cursor.moveToFirst()) {
            return cursor.getInt(0);
        }
        return -1; // Not found
    }

    // Save user food entry
    public boolean insertUserCalorieEntry(int userId, String foodName, int calories, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("food_name", foodName);
        values.put("calorie_value", calories);
        values.put("date", date);
        long result = db.insert("calories", null, values);
        return result != -1;
    }

    // Get all food items
    public Cursor getAllFoodItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM food_table", null);
    }

    // Delete food by ID
    public boolean deleteFoodItemById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete("food_table", "id=?", new String[]{String.valueOf(id)});
        return result > 0;
    }

}
