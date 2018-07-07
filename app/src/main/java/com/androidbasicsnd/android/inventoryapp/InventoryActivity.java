package com.androidbasicsnd.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.androidbasicsnd.android.inventoryapp.data.ProductContract.ProductEntry;

/**
 * Displays list of products that were entered and stored in the app.
 */
public class InventoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PRODUCT_LOADER = 0;

    ProductCursorAdapter cursorAdapter;

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

        // Find the ListView which will be populated with the product data
        ListView productListView = (ListView) findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        productListView.setEmptyView(emptyView);

        cursorAdapter = new ProductCursorAdapter(this, null);
        productListView.setAdapter(cursorAdapter);

        // Setup item click listener
        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(InventoryActivity.this, EditorActivity.class);

                final Uri currentProductUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);

                intent.setData(currentProductUri);

                startActivity(intent);
            }
        });

        // Kick off the loader
        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * Helper method to insert hardcoded product data into the database. For debugging purposes only.
     */
    private void insertProduct() {

        // Create a ContentValues object where column names are the keys,
        // and Keyboards's product attributes are the values.

        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Keyboard");
        values.put(ProductEntry.COLUMN_PRICE, 1200);
        values.put(ProductEntry.COLUMN_QUANTITY, 2);
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, "Amazon");
        values.put(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER, "Confidential");

        Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertProduct();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllProducts();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRICE,
                ProductEntry.COLUMN_QUANTITY,
                ProductEntry.COLUMN_SUPPLIER_NAME,
                ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER};

        return new CursorLoader(this,
                ProductEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

    private void deleteAllProducts() {
        int rowsDeleted = getContentResolver().delete(ProductEntry.CONTENT_URI, null, null);
        Log.v("InventoryActivity", rowsDeleted + " rows deleted from inventory database");
    }

    /**
     * A method that decreases the quantity of a product with 1.
     *
     * @param id the id of the product
     * @param quantity the quantity of the product
     */
    public void saleProduct(int id, int quantity) {
        quantity--;
        if (quantity > -1) {
            updateProduct(id, quantity);
        }
    }

    private void updateProduct(int id, int quantity) {
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_QUANTITY, quantity);
        Uri updateUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);
        getContentResolver().update(updateUri, values, null, null);
    }

}
