package com.androidbasicsnd.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.androidbasicsnd.android.inventoryapp.data.ProductContract.ProductEntry;

/**
 * Database helper for the Inventory App. Manages the database creation and the version.
 */
public class ProductDBHelper extends SQLiteOpenHelper {

    /**
     * Name of the database file.
     */
    private static final String DATABASE_NAME = "inventory.db";

    /**
     * Database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@Link ProductDBHelper}.
     *
     * @param context of the application
     */
    public ProductDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This method is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the products table.

        String SQL_CREATE_PRODUCTS_TABLE = "CREATE TABLE " + ProductEntry.TABLE_NAME + " ("
                + ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + ProductEntry.COLUMN_PRICE + " INTEGER NOT NULL, "
                + ProductEntry.COLUMN_QUANTITY + " INTEGER NOT NULL, "
                + ProductEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, "
                + ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER + " TEXT NOT NULL);";

        // execute the SQL statement
        db.execSQL(SQL_CREATE_PRODUCTS_TABLE);
    }

    /**
     * This method is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there is nothing to be done here.
    }
}
