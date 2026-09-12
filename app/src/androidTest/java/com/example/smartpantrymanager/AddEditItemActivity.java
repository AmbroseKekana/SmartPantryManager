package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddEditItemActivity extends AppCompatActivity {

    private EditText etName, etQuantity, etUnit, etExpiry;
    private Button btnSave;
    private DatabaseHelper dbHelper;
    private int itemId = -1; // -1 means new item

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_item);

        etName = findViewById(R.id.etName);
        etQuantity = findViewById(R.id.etQuantity);
        etUnit = findViewById(R.id.etUnit);
        etExpiry = findViewById(R.id.etExpiry);
        btnSave = findViewById(R.id.btnSave);

        dbHelper = new DatabaseHelper(this);

        // Check if we are editing an existing item
        if (getIntent().hasExtra("item_id")) {
            itemId = getIntent().getIntExtra("item_id", -1);
            etName.setText(getIntent().getStringExtra("name"));
            etQuantity.setText(String.valueOf(getIntent().getDoubleExtra("quantity", 0)));
            etUnit.setText(getIntent().getStringExtra("unit"));
            etExpiry.setText(getIntent().getStringExtra("expiry"));
        }

        btnSave.setOnClickListener(v -> saveItem());
    }

    private void saveItem() {
        String name = etName.getText().toString().trim();
        String quantityStr = etQuantity.getText().toString().trim();
        String unit = etUnit.getText().toString().trim();
        String expiry = etExpiry.getText().toString().trim();

        // Simple validation
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
            // Add new item
            dbHelper.addPantryItem(name, quantity, unit, expiry);
            Toast.makeText(this, "Item added", Toast.LENGTH_SHORT).show();
        } else {
            // Update existing item
            dbHelper.updatePantryItem(itemId, name, quantity, unit, expiry);
            Toast.makeText(this, "Item updated", Toast.LENGTH_SHORT).show();
        }

        finish(); // Go back to the list
    }
}
