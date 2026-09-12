package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SmartPantry.db";
    private static final int DATABASE_VERSION = 1;

    // Pantry table
    public static final String TABLE_PANTRY = "pantry";
    public static final String COL_PANTRY_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_QUANTITY = "quantity";
    public static final String COL_UNIT = "unit";
    public static final String COL_EXPIRY = "expiry";

    // Recipes table
    public static final String TABLE_RECIPES = "recipes";
    public static final String COL_RECIPE_ID = "id";
    public static final String COL_RECIPE_NAME = "name";
    public static final String COL_INGREDIENTS = "ingredients"; // stored as comma-separated
    public static final String COL_STEPS = "steps";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create pantry table
        String createPantry = "CREATE TABLE " + TABLE_PANTRY + " (" +
                COL_PANTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT, " +
                COL_QUANTITY + " REAL, " +
                COL_UNIT + " TEXT, " +
                COL_EXPIRY + " TEXT)";
        db.execSQL(createPantry);

        // Create recipes table
        String createRecipes = "CREATE TABLE " + TABLE_RECIPES + " (" +
                COL_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_RECIPE_NAME + " TEXT, " +
                COL_INGREDIENTS + " TEXT, " +
                COL_STEPS + " TEXT)";
        db.execSQL(createRecipes);

        // Pre-load recipes
        insertDefaultRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        onCreate(db);
    }

    // ========== PANTRY CRUD ==========

    public long addPantryItem(String name, double quantity, String unit, String expiry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_QUANTITY, quantity);
        values.put(COL_UNIT, unit);
        values.put(COL_EXPIRY, expiry);
        long id = db.insert(TABLE_PANTRY, null, values);
        db.close();
        return id;
    }

    public Cursor getAllPantryItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_PANTRY + " ORDER BY " + COL_NAME, null);
    }

    public int updatePantryItem(int id, String name, double quantity, String unit, String expiry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_QUANTITY, quantity);
        values.put(COL_UNIT, unit);
        values.put(COL_EXPIRY, expiry);
        int rows = db.update(TABLE_PANTRY, values, COL_PANTRY_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    public void deletePantryItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PANTRY, COL_PANTRY_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    // ========== RECIPES ==========

    public Cursor getAllRecipes() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_RECIPES, null);
    }

    public Cursor getRecipeById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_RECIPES + " WHERE " + COL_RECIPE_ID + "=?",
                new String[]{String.valueOf(id)});
    }

    // Strict matching: only return recipes where ALL ingredients are in the pantry
    public List<String> getSuggestedRecipes() {
        List<String> suggested = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Get all pantry ingredient names (lowercase for simple matching)
        Cursor pantryCursor = db.rawQuery("SELECT " + COL_NAME + " FROM " + TABLE_PANTRY, null);
        List<String> pantryItems = new ArrayList<>();
        if (pantryCursor.moveToFirst()) {
            do {
                pantryItems.add(pantryCursor.getString(0).toLowerCase().trim());
            } while (pantryCursor.moveToNext());
        }
        pantryCursor.close();

        // Check each recipe
        Cursor recipeCursor = db.rawQuery("SELECT " + COL_RECIPE_NAME + ", " + COL_INGREDIENTS + " FROM " + TABLE_RECIPES, null);
        if (recipeCursor.moveToFirst()) {
            do {
                String recipeName = recipeCursor.getString(0);
                String ingredientsStr = recipeCursor.getString(1);
                String[] required = ingredientsStr.split(",");

                boolean canMake = true;
                for (String ing : required) {
                    String cleanIng = ing.trim().toLowerCase();
                    if (!pantryItems.contains(cleanIng)) {
                        canMake = false;
                        break;
                    }
                }

                if (canMake) {
                    suggested.add(recipeName);
                }
            } while (recipeCursor.moveToNext());
        }
        recipeCursor.close();
        db.close();

        return suggested;
    }

    // Pre-load 15 simple recipes
    private void insertDefaultRecipes(SQLiteDatabase db) {
        String[][] recipes = {
                {"Tomato Scramble", "egg,tomato,salt", "Beat eggs. Fry tomato. Add eggs and salt. Cook until done."},
                {"Simple Pasta", "pasta,tomato,salt", "Boil pasta. Heat tomato. Mix together with salt."},
                {"Cheese Toast", "bread,cheese", "Put cheese on bread. Toast until cheese melts."},
                {"Egg Fried Rice", "rice,egg,salt", "Fry egg. Add rice and salt. Stir well."},
                {"Banana Smoothie", "banana,milk", "Blend banana and milk until smooth."},
                {"Peanut Butter Sandwich", "bread,peanut butter", "Spread peanut butter on bread. Close sandwich."},
                {"Boiled Egg", "egg,salt", "Boil egg for 8 minutes. Add salt."},
                {"Milk Cereal", "cereal,milk", "Pour cereal into bowl. Add milk."},
                {"Tomato Salad", "tomato,salt", "Chop tomato. Add salt. Mix."},
                {"Butter Toast", "bread,butter", "Spread butter on bread. Toast lightly."},
                {"Plain Rice", "rice,salt", "Boil rice with salt until soft."},
                {"Scrambled Eggs", "egg,salt,butter", "Melt butter. Add beaten eggs and salt. Scramble."},
                {"Cheese Omelette", "egg,cheese,salt", "Beat eggs. Cook with cheese and salt."},
                {"Fruit Bowl", "banana,apple", "Chop banana and apple. Mix in a bowl."},
                {"Simple Yogurt", "yogurt,banana", "Slice banana. Mix with yogurt."}
        };

        for (String[] r : recipes) {
            ContentValues values = new ContentValues();
            values.put(COL_RECIPE_NAME, r[0]);
            values.put(COL_INGREDIENTS, r[1]);
            values.put(COL_STEPS, r[2]);
            db.insert(TABLE_RECIPES, null, values);
        }
    }
}