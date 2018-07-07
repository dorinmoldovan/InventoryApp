package com.androidbasicsnd.android.inventoryapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.androidbasicsnd.android.inventoryapp.data.ProductContract.ProductEntry;

/**
 * Allows the user to create a new product or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /** Identifier for the product data loader */
    private static final int EXISTING_PRODUCT_LOADER = 0;

    /** Content URI for the existing product (null if it's a new product) */
    private Uri currentProductUri;

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

    /** Boolean flag that keeps track of whether the product has been edited (true) or not (false) */
    private boolean productHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the productHasChanged boolean to true.
     */
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            productHasChanged = true;
            return false;
        }
    };

    /**
     * Create an object of type EditorActivity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new product or editing an existing one.
        Intent intent = getIntent();
        currentProductUri = intent.getData();

        // If the intent DOES NOT contain a product content URI, then we know that we are
        // creating a new product.
        if (currentProductUri == null) {
            // This is a new product, so change the app bar to say "Add a Product"
            setTitle(getString(R.string.editor_activity_title_new_product));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a product that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing product, so change app bar to say "Edit Product"
            setTitle(getString(R.string.editor_activity_title_edit_product));

            // Initialize a loader to read the product data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

        nameEditText = (EditText) findViewById(R.id.edit_product_name);
        priceEditText = (EditText) findViewById(R.id.edit_product_price);
        quantityEditText = (EditText) findViewById(R.id.edit_product_quantity);
        supplierNameEditText = (EditText) findViewById(R.id.edit_supplier_name);
        supplierPhoneNumberEditText = (EditText) findViewById(R.id.edit_supplier_phone_number);

        nameEditText.setOnTouchListener(touchListener);
        priceEditText.setOnTouchListener(touchListener);
        quantityEditText.setOnTouchListener(touchListener);
        supplierNameEditText.setOnTouchListener(touchListener);
        supplierPhoneNumberEditText.setOnTouchListener(touchListener);

    }

    /**
     * Get user input from editor and save product into database.
     */
    private void saveProduct() {
        // Read from input fields and use trim to eliminate leading or trailing white space
        String name = nameEditText.getText().toString().trim();
        String priceString = priceEditText.getText().toString().trim();
        String quantityString = quantityEditText.getText().toString().trim();
        String supplierName = supplierNameEditText.getText().toString().trim();
        String supplierPhoneNumer = supplierPhoneNumberEditText.getText().toString().trim();

        // Check if this is supposed to be a new product
        // and check if all the fields in the editor are blank
        if (currentProductUri == null && TextUtils.isEmpty(name)
                && TextUtils.isEmpty(priceString) && TextUtils.isEmpty(quantityString)
                && TextUtils.isEmpty(supplierName) && TextUtils.isEmpty(supplierPhoneNumer)) {
            // Since no fields were modified, we can return early without creating a new product.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, name);
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, supplierName);
        values.put(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhoneNumer);

        // If the price is not provided by the user, don't try to parse the string into an
        // integer value. Use 0 by default.
        int price = 0;
        if (!TextUtils.isEmpty(priceString)) {
            price = Integer.parseInt(priceString);
        }
        values.put(ProductEntry.COLUMN_PRICE, price);

        // If the quantity is not provided by the user, don't try to parse the string into an
        // integer value. Use 0 by default.
        int quantity = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }
        values.put(ProductEntry.COLUMN_QUANTITY, quantity);

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(priceString) || TextUtils.isEmpty(quantityString)
                || TextUtils.isEmpty(supplierName) ||TextUtils.isEmpty(supplierPhoneNumer)) {
            Toast.makeText(this, getString(R.string.productsNotNullOrEmpty),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Determine if this is a new or existing product by checking if currentProductUri is null or not
        if (currentProductUri == null) {
            // This is a NEW product, so insert a new product into the provider,
            // returning the content URI for the new product.
            Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING product, so update the product with content URI: currentProductUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because currentProductUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(currentProductUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
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

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new product, hide the "Delete" menu item.
        if (currentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save product to database
                saveProduct();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the product hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!productHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the product hasn't changed, continue with handling back button press
        if (!productHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Since the editor shows all product attributes, define a projection that contains
        // all columns from the product table
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRICE,
                ProductEntry.COLUMN_QUANTITY,
                ProductEntry.COLUMN_SUPPLIER_NAME,
                ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,
                currentProductUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of product attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_QUANTITY);
            int supplierNameIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneNumberIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameIndex);
            String supplierPhoneNumber = cursor.getString(supplierPhoneNumberIndex);

            // Update the views on the screen with the values from the database
            nameEditText.setText(name);
            priceEditText.setText(Integer.toString(price));
            quantityEditText.setText(Integer.toString(quantity));
            supplierNameEditText.setText(supplierName);
            supplierPhoneNumberEditText.setText(supplierPhoneNumber);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nameEditText.setText("");
        priceEditText.setText("");
        quantityEditText.setText("");
        supplierNameEditText.setText("");
        supplierPhoneNumberEditText.setText("");
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product.
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the product in the database.
     */
    private void deleteProduct() {
        // Only perform the delete if this is an existing product.
        if (currentProductUri != null) {
            // Call the ContentResolver to delete the product at the given content URI.
            // Pass in null for the selection and selection args because the currentProductUri
            // content URI already identifies the product that we want.
            int rowsDeleted = getContentResolver().delete(currentProductUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
            // Close the activity
            finish();
        }
    }

    public void increaseQuantity(View view) {
        String quantityString = quantityEditText.getText().toString().trim();
        int quantity = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }
        quantity++;
        String quantityValue = String.valueOf(quantity);
        quantityEditText.setText(quantityValue);
    }

    public void decreaseQuantity(View view) {
        String quantityString = quantityEditText.getText().toString().trim();
        int quantity = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }
        quantity--;
        if (quantity < 0) {
            quantity = 0;
        }
        String quantityValue = String.valueOf(quantity);
        quantityEditText.setText(quantityValue);
    }

    public void deleteProduct(View view) {
        showDeleteConfirmationDialog();
    }

    public void callSupplier(View view) {
        String supplierPhoneNumer = supplierPhoneNumberEditText.getText().toString().trim();
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + supplierPhoneNumer));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(intent);
    }
}
