package com.example.smartpantrymanager;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private DatabaseHelper dbHelper;
    private List<String> recipeList;
    private RecipeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        recyclerView = findViewById(R.id.recyclerViewRecipes);
        tvEmpty = findViewById(R.id.tvEmpty);
        dbHelper = new DatabaseHelper(this);

        recipeList = new ArrayList<>();
        adapter = new RecipeAdapter(recipeList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadSuggestedRecipes();
    }

    private void loadSuggestedRecipes() {
        recipeList.clear();
        List<String> suggested = dbHelper.getSuggestedRecipes();
        recipeList.addAll(suggested);
        adapter.notifyDataSetChanged();

        if (recipeList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestedRecipes();
    }
}