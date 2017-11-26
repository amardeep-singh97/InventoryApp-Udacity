package com.example.android.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.android.inventoryapp.R;

/**
 * Created by Amardeep on 7/31/2017.
 */

public class ItemProvider extends ContentProvider {
    private static final int ITEM = 100;
    private static final int ITEM_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(ItemContract.CONTENT_AUTHORITY,ItemContract.PATH_ITEM,ITEM);
        sUriMatcher.addURI(ItemContract.CONTENT_AUTHORITY,ItemContract.PATH_ITEM + "/#",ITEM_ID);
    }

    private ItemDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new ItemDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match){
            case ITEM:
                cursor = database.query(ItemContract.ItemEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                 break;
            case ITEM_ID:
                selection = ItemContract.ItemEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor=database.query(ItemContract.ItemEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
                default:
                    throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case ITEM:
                return insertItem(uri,contentValues);
            default:
                throw new IllegalArgumentException("cannot insert unknown URI"+uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsdeleted;
        final int match = sUriMatcher.match(uri);
        switch (match){
            case ITEM:
                rowsdeleted = database.delete(ItemContract.ItemEntry.TABLE_NAME,selection,selectionArgs);
                break;
            case ITEM_ID:
                selection = ItemContract.ItemEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsdeleted = database.delete(ItemContract.ItemEntry.TABLE_NAME,selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unable to delete");
        }
        if(rowsdeleted !=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsdeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;
        switch (match){
            case ITEM:
                rowsUpdated = updateItem(uri,contentValues,selection,selectionArgs);
                break;
            case ITEM_ID:
                selection = ItemContract.ItemEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsUpdated = updateItem(uri,contentValues,selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unable to update");
        }
        return rowsUpdated;
    }
    public Uri insertItem(Uri uri, ContentValues values){
      SQLiteDatabase database = mDbHelper.getWritableDatabase();
        String name= values.getAsString(ItemContract.ItemEntry.COLUMN_ITEM_NAME);
        if(name==null){
            throw new IllegalArgumentException("Valid Name required");
        }
        Integer price = values.getAsInteger(ItemContract.ItemEntry.COLUMN_ITEM_PRICE);
        if(price<=0){
            throw new IllegalArgumentException("Valid Price required");
        }
        Integer quantity = values.getAsInteger(ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY);
        if(quantity<=0){
            throw new IllegalArgumentException("Valid Quantity required");
        }
        String email = values.getAsString(ItemContract.ItemEntry.COLUMN_ITEM_EMAIL);
        if(email==null){
            throw new IllegalArgumentException("Valid email required");
        }
        String image = values.getAsString(ItemContract.ItemEntry.COLUMN_ITEM_PHOTO);
        if (image==null){
            throw new IllegalArgumentException("Image is required");
        }
        Long id =  database.insert(ItemContract.ItemEntry.TABLE_NAME,null,values);
        if (id==-1){
            Toast.makeText(getContext(), R.string.itemProvider_insert_error,Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getContext(),R.string.itemProvider_insert_no_error,Toast.LENGTH_SHORT).show();
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return ContentUris.withAppendedId(uri,id);
    }
    public int updateItem(Uri uri,ContentValues values,String selection, String[] selectionArgs){
        SQLiteDatabase database=mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(ItemContract.ItemEntry.TABLE_NAME,values,selection,selectionArgs);
        if(rowsUpdated !=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsUpdated;
    }
}
