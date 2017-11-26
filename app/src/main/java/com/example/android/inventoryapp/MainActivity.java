package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.inventoryapp.data.ItemContract;
import com.example.android.inventoryapp.data.ItemCursorAdapter;
import com.example.android.inventoryapp.data.ItemDbHelper;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final int ITEM_LOADER_ID=0;
    private ItemDbHelper mDbHelper;
    private ItemCursorAdapter mCursorAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView = (ListView)findViewById(R.id.list_view);
        mCursorAdapter = new ItemCursorAdapter(this,null);
        listView.setAdapter(mCursorAdapter);
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int postion, long id) {
              Intent intent = new Intent(MainActivity.this,EditorActivity.class);
                Uri currenturi = ContentUris.withAppendedId(ItemContract.ItemEntry.CONTENT_URI,id);
                intent.setData(currenturi);
                startActivity(intent);
            }
        });
        getSupportLoaderManager().initLoader(ITEM_LOADER_ID,null,this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.dummy_data:
                insertdummydata();
                    return true;
            case R.id.delete_all:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void plusData(View v){
        Intent intent = new Intent(MainActivity.this,EditorActivity.class);
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] Projection = {ItemContract.ItemEntry._ID,
                ItemContract.ItemEntry.COLUMN_ITEM_NAME,
                ItemContract.ItemEntry.COLUMN_ITEM_PRICE,
                ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY,
                ItemContract.ItemEntry.COLUMN_ITEM_EMAIL,
                ItemContract.ItemEntry.COLUMN_ITEM_PHOTO};
        return new CursorLoader(this, ItemContract.ItemEntry.CONTENT_URI,
                Projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
    public void insertdummydata(){
        Uri mNewUr;
        ContentValues values = new ContentValues();
        values.put(ItemContract.ItemEntry.COLUMN_ITEM_NAME,"CHOCOLATE");
        values.put(ItemContract.ItemEntry.COLUMN_ITEM_PRICE,50);
        values.put(ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY,20);
        values.put(ItemContract.ItemEntry.COLUMN_ITEM_EMAIL,"example@gmail.com");
        values.put(ItemContract.ItemEntry.COLUMN_ITEM_PHOTO,"android.resource://com.example.android.inventoryapp/drawable/choco");
        mNewUr = getContentResolver().insert(ItemContract.ItemEntry.CONTENT_URI,values);
    }
    public void deleteAllItem(){
        int rowdeleted = getContentResolver().delete(ItemContract.ItemEntry.CONTENT_URI,null,null);
    }
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteAllItem();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
