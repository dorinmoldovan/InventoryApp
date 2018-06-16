package com.androidbasicsnd.android.inventoryapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.androidbasicsnd.android.inventoryapp.data.ProductContract;
import com.androidbasicsnd.android.inventoryapp.data.ProductContract.ProductEntry;
import com.androidbasicsnd.android.inventoryapp.data.ProductDBHelper;

public class InventoryActivity extends AppCompatActivity {

    /** Database helper that provides access to the database */
    private ProductDBHelper productDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_app);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InventoryActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        productDBHelper = new ProductDBHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        queryData();
    }

    private void insertData() {

        SQLiteDatabase db = productDBHelper.getWritableDatabase();

        ContentValues valuesFirstProduct = new ContentValues();
        valuesFirstProduct.put(ProductEntry.COLUMN_PRODUCT_NAME, "Keyboard");
        valuesFirstProduct.put(ProductEntry.COLUMN_PRICE, 1200);
        valuesFirstProduct.put(ProductEntry.COLUMN_QUANTITY, 2);
        valuesFirstProduct.put(ProductEntry.COLUMN_SUPPLIER_NAME, "Amazon");
        valuesFirstProduct.put(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER, "Confidential");

        db.insert(ProductEntry.TABLE_NAME, null, valuesFirstProduct);

        ContentValues valuesSecondProduct = new ContentValues();
        valuesSecondProduct.put(ProductEntry.COLUMN_PRODUCT_NAME, "Mouse");
        valuesSecondProduct.put(ProductEntry.COLUMN_PRICE, 200);
        valuesSecondProduct.put(ProductEntry.COLUMN_QUANTITY, 5);
        valuesSecondProduct.put(ProductEntry.COLUMN_SUPPLIER_NAME, "Amazon");
        valuesSecondProduct.put(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER, "Confidential");

        db.insert(ProductEntry.TABLE_NAME, null, valuesSecondProduct);
    }

    private void queryData() {

        // Create and/or open a database to read from it.
        SQLiteDatabase db = productDBHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database are used after the
        // query.
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRICE,
                ProductEntry.COLUMN_QUANTITY,
                ProductEntry.COLUMN_SUPPLIER_NAME,
                ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER };

        // Perform a query on the pets table.
        Cursor cursor = db.query(
                ProductEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);

        TextView displayView = (TextView) findViewById(R.id.text_view_product);

        try {
            // Create a header in the Text View that looks like this.

            displayView.setText("The products table contains " + cursor.getCount() + " products.\n\n");
            displayView.append(ProductEntry._ID + " - " +
                    ProductEntry.COLUMN_PRODUCT_NAME + " - " +
                    ProductEntry.COLUMN_PRICE + " - " +
                    ProductEntry.COLUMN_QUANTITY + " - " +
                    ProductEntry.COLUMN_SUPPLIER_NAME  + " - " +
                    ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER + "\n");

            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(ProductEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneNumberColumnIndex =
                    cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {

                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                int currentPrice = cursor.getInt(priceColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                String currentSupplierName = cursor.getString(supplierNameColumnIndex);
                String currentSupplierPhoneNumber = cursor.getString(supplierPhoneNumberColumnIndex);

                displayView.append(("\n" + currentID + " - " +
                        currentName + " - " +
                        currentPrice + " - " +
                        currentQuantity + " - " +
                        currentSupplierName + " - " +
                        currentSupplierPhoneNumber));
            }
        } finally {
            // Close the cursor.
            cursor.close();
        }
    }

}
