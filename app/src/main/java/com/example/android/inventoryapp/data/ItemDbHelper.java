package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Amardeep on 7/31/2017.
 */

public class ItemDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME="Stock.db";
    private static final int DATABASE_VERSION =1;
    public ItemDbHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
     String CREATE_SQL_ENTRIES=
             "CREATE TABLE " + ItemContract.ItemEntry.TABLE_NAME + " ("+
                     ItemContract.ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     ItemContract.ItemEntry.COLUMN_ITEM_NAME + " TEXT NOT NULL, " +
                     ItemContract.ItemEntry.COLUMN_ITEM_PRICE + " INTEGER NOT NULL, " +
                     ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY + " INTEGER NOT NULL DEFAULT 0, " +
                     ItemContract.ItemEntry.COLUMN_ITEM_EMAIL + " TEXT NOT NULL, " +
                     ItemContract.ItemEntry.COLUMN_ITEM_PHOTO + " TEXT NOT NULL);";
        sqLiteDatabase.execSQL(CREATE_SQL_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}
