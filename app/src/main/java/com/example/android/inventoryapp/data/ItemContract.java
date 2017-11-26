package com.example.android.inventoryapp.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Amardeep on 7/31/2017.
 */

public final class ItemContract {
    public static final String CONTENT_AUTHORITY="com.example.android.inventoryapp";
    public static final Uri BASE_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_ITEM = "items";
    public ItemContract(){}

    public static abstract class ItemEntry implements BaseColumns{
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI,PATH_ITEM);
        public static final String TABLE_NAME = "items";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_ITEM_NAME = "name";
        public static final String COLUMN_ITEM_PRICE="price";
        public static final String COLUMN_ITEM_QUANTITY="quantity";
        public static final String COLUMN_ITEM_EMAIL ="email";
        public static final String COLUMN_ITEM_PHOTO="photo";
    }
}