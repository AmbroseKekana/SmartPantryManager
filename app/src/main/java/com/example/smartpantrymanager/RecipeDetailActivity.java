package com.example.smartpantrymanager;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailActivity extends AppCompatActivity {

    private TextView tvRecipeName, tvIngredients, tvSteps;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        tvRecipeName = findViewById(R.id.tvRecipeName);
        tvIngredients = findViewById(R.id.tvIngredients);
        tvSteps = findViewById(R.id.tvSteps);
        dbHelper = new DatabaseHelper(this);

        String recipeName = getIntent().getStringExtra("recipe_name");

        if (recipeName != null) {
            loadRecipe(recipeName);
        }
    }

    private void loadRecipe(String name) {
        Cursor cursor = dbHelper.getAllRecipes();

        if (cursor.moveToFirst()) {
            do {
                String currentName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_RECIPE_NAME));
                if (currentName.equals(name)) {
                    String ingredients = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_INGREDIENTS));
                    String steps = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_STEPS));

                    tvRecipeName.setText(currentName);
                    tvIngredients.setText(ingredients.replace(",", "\n• "));
                    tvSteps.setText(steps);
                    break;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
    }
}