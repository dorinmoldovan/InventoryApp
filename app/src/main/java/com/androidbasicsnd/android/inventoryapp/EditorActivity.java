package com.androidbasicsnd.android.inventoryapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.androidbasicsnd.android.inventoryapp.data.ProductContract.ProductEntry;
import com.androidbasicsnd.android.inventoryapp.data.ProductDBHelper;

/**
 * Allows the user to create a new product.
 */
public class EditorActivity extends AppCompatActivity {

    public static final String LOG_TAG = EditorActivity.class.getSimpleName();

    /** EditText field to enter the product's name. */
    private EditText nameEditText;

    /** EditText field to enter the product's price. */
    private EditText priceEditText;

    /** EditText field to enter the product's quantity. */
    private EditText quantityEditText;

    /** EditText field to enter the supplier name. */
    private EditText supplierNameEditText;

    /** EditText field to enter the supplier phone number. */
    private EditText supplierPhoneNumberEditText;

    /**
     * Create an object of type EditorActivity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        nameEditText = (EditText) findViewById(R.id.edit_product_name);
        priceEditText = (EditText) findViewById(R.id.edit_product_price);
        quantityEditText = (EditText) findViewById(R.id.edit_product_quantity);
        supplierNameEditText = (EditText) findViewById(R.id.edit_supplier_name);
        supplierPhoneNumberEditText = (EditText) findViewById(R.id.edit_supplier_phone_number);
    }

    private void insertData() {
        // Read from input fields and use trim to eliminate leading or trailing white space
        String name = nameEditText.getText().toString().trim();
        Integer price = Integer.parseInt(priceEditText.getText().toString().trim());
        Integer quantity = Integer.parseInt(quantityEditText.getText().toString().trim());
        String supplierName = supplierNameEditText.getText().toString().trim();
        String supplierPhoneNumer = supplierPhoneNumberEditText.getText().toString().trim();

        // Create database helper
        ProductDBHelper productDBHelper = new ProductDBHelper(this);

        // Gets the database in write mode
        SQLiteDatabase db = productDBHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys,
        // and product attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, name);
        values.put(ProductEntry.COLUMN_PRICE, price);
        values.put(ProductEntry.COLUMN_QUANTITY, quantity);
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, supplierName);
        values.put(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhoneNumer);

        // Insert a new row for product in the database, returning the ID of that new row.
        long newRowId = db.insert(ProductEntry.TABLE_NAME, null, values);

        // Show a toast message depending on whether or not the insertion was successful
        if (newRowId == -1) {
            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText(this, "Error with saving product", Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast with the row ID.
            Toast.makeText(this, "Product saved with row id: " + newRowId, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Inflate the menu options from the menu_editor.xml file. This adds menu items to the app bar.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Insert the product in the database
                insertData();
                finish();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (EditorActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
