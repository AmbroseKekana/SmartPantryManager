package com.example.smartpantrymanager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddEditItemActivity extends AppCompatActivity {

    private EditText etName, etQuantity, etUnit, etExpiry;
    private Button btnSave;
    private Button btnDelete;
    private DatabaseHelper dbHelper;
    private int itemId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_item);

        etName = findViewById(R.id.etName);
        etQuantity = findViewById(R.id.etQuantity);
        etUnit = findViewById(R.id.etUnit);
        etExpiry = findViewById(R.id.etExpiry);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);

        dbHelper = new DatabaseHelper(this);

        if (getIntent().hasExtra("item_id")) {
            itemId = getIntent().getIntExtra("item_id", -1);
            etName.setText(getIntent().getStringExtra("name"));
            etQuantity.setText(String.valueOf(getIntent().getDoubleExtra("quantity", 0)));
            etUnit.setText(getIntent().getStringExtra("unit"));
            etExpiry.setText(getIntent().getStringExtra("expiry"));
        }

        if (itemId == -1) {
            btnDelete.setVisibility(View.GONE);
        } else {
            btnDelete.setVisibility(View.VISIBLE);
        }

        btnSave.setOnClickListener(v -> saveItem());

        btnDelete.setOnClickListener(v -> {
            dbHelper.deletePantryItem(itemId);
            Toast.makeText(this, "Item deleted", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void saveItem() {
        String name = etName.getText().toString().trim();
        String quantityStr = etQuantity.getText().toString().trim();
        String unit = etUnit.getText().toString().trim();
        String expiry = etExpiry.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError("Name is required");
            return;
        }
        if (quantityStr.isEmpty()) {
            etQuantity.setError("Quantity is required");
            return;
        }
        if (unit.isEmpty()) {
            etUnit.setError("Unit is required");
            return;
        }

        double quantity = Double.parseDouble(quantityStr);

        if (itemId == -1) {
            dbHelper.addPantryItem(name, quantity, unit, expiry);
            Toast.makeText(this, "Item added", Toast.LENGTH_SHORT).show();
        } else {
            dbHelper.updatePantryItem(itemId, name, quantity, unit, expiry);
            Toast.makeText(this, "Item updated", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}