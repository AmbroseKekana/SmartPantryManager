package com.example.smartpantrymanager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PantryListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button btnAddItem;
    private Button btnSuggested;
    private DatabaseHelper dbHelper;
    private List<PantryItem> itemList;
    private PantryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry_list);

        recyclerView = findViewById(R.id.recyclerViewPantry);
        btnAddItem = findViewById(R.id.btnAddItem);
        btnSuggested = findViewById(R.id.btnSuggested);
        dbHelper = new DatabaseHelper(this);

        itemList = new ArrayList<>();
        adapter = new PantryAdapter(itemList, item -> {
            Intent intent = new Intent(PantryListActivity.this, AddEditItemActivity.class);
            intent.putExtra("item_id", item.getId());
            intent.putExtra("name", item.getName());
            intent.putExtra("quantity", item.getQuantity());
            intent.putExtra("unit", item.getUnit());
            intent.putExtra("expiry", item.getExpiry());
            startActivity(intent);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btnAddItem.setOnClickListener(v -> {
            Intent intent = new Intent(PantryListActivity.this, AddEditItemActivity.class);
            startActivity(intent);
        });

        btnSuggested.setOnClickListener(v -> {
            Intent intent = new Intent(PantryListActivity.this, SuggestedRecipesActivity.class);
            startActivity(intent);
        });

        loadPantryItems();
    }

    private void loadPantryItems() {
        itemList.clear();
        Cursor cursor = dbHelper.getAllPantryItems();

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PANTRY_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME));
                double quantity = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_QUANTITY));
                String unit = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_UNIT));
                String expiry = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXPIRY));

                itemList.add(new PantryItem(id, name, quantity, unit, expiry));
            } while (cursor.moveToNext());
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPantryItems();
    }
}