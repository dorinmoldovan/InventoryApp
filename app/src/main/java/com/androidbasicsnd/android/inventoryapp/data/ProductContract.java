package com.androidbasicsnd.android.inventoryapp.data;

import android.provider.BaseColumns;

public final class ProductContract {

    /**
     * Private empty constructor that prevents the accidental instantiation of the class.
     */
    private ProductContract() {

    }

    /**
     * Each entry from the table represents a single product.
     */
    public static final class ProductEntry implements BaseColumns {

        /**
         * Name of the database table for products.
         */
        public final static String TABLE_NAME = "products";

        /**
         * Unique ID for each product.
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the product.
         *
         * Type: TEXT
         */
        public final static String COLUMN_PRODUCT_NAME = "product_name";

        /**
         * Price of the product.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_PRICE = "price";

        /**
         * Quantity of the product.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_QUANTITY = "quantity";

        /**
         * Name of the supplier.
         *
         * Type: TEXT
         */
        public final static String COLUMN_SUPPLIER_NAME = "supplier_name";

        /**
         * Phone number of the supplier.
         *
         * Type: TEXT
         */
        public final static String COLUMN_SUPPLIER_PHONE_NUMBER = "supplier_phone_number";

    }

}
