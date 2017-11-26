package com.example.android.inventoryapp;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.inventoryapp.data.ItemContract;
import com.example.android.inventoryapp.data.ItemDbHelper;

import java.io.File;

import static com.example.android.inventoryapp.R.drawable.ic_insert_placeholder;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final int PICK_PHOTO_REQUEST = 20;
    public static final int EXTERNAL_STORAGE_REQUEST_PERMISSION_CODE = 21;
    private static final int EXISTING_ITEM_LOADER = 0;
    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mEmailEditText;
    private Button mMinus;
    private Button mPlus;
    private ImageView mSelectphoto;
    private ItemDbHelper mDbHelper;
    private int count=0;
    private Uri mCurrentItemuri;
    private boolean mItemHasChanged = false;
    private String mCurrentPhotoUri = "no images";
    private Button imgButton;
    private View.OnTouchListener mtouchlistener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged=true;
            return false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        mNameEditText =(EditText)findViewById(R.id.product_name);
        mPriceEditText = (EditText)findViewById(R.id.product_price);
        mQuantityEditText = (EditText)findViewById(R.id.product_quantity);
        mEmailEditText =(EditText)findViewById(R.id.product_phone);
        mMinus = (Button)findViewById(R.id.button_minus);
        mPlus = (Button)findViewById(R.id.button_plus);
        mSelectphoto = (ImageView)findViewById(R.id.select_photo);
        imgButton=(Button)findViewById(R.id.imageButton);
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPhotoProductUpdate(view);
            }
        });
        mNameEditText.setOnTouchListener(mtouchlistener);
        mEmailEditText.setOnTouchListener(mtouchlistener);
        mQuantityEditText.setOnTouchListener(mtouchlistener);
        mPriceEditText.setOnTouchListener(mtouchlistener);
        mMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subtractOneToQuantity();
            }
        });
        mPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sumOneToQuantity();
            }
        });
        Intent intent = getIntent();
        mCurrentItemuri=intent.getData();
        if(mCurrentItemuri==null){
            setTitle(R.string.Editor_name1);
        }else {
            setTitle(R.string.Editor_name2);
            getSupportLoaderManager().initLoader(EXISTING_ITEM_LOADER,null,this);
        }
    }
    public void onPhotoProductUpdate(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //We are on M or above so we need to ask for runtime permissions
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                invokeGetPhoto();
            } else {
                // we are here if we do not all ready have permissions
                String[] permisionRequest = {Manifest.permission.READ_EXTERNAL_STORAGE};
                requestPermissions(permisionRequest, EXTERNAL_STORAGE_REQUEST_PERMISSION_CODE);
            }
        } else {
            //We are on an older devices so we dont have to ask for runtime permissions
            invokeGetPhoto();
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == EXTERNAL_STORAGE_REQUEST_PERMISSION_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //We got a GO from the user
            invokeGetPhoto();
        } else {
            Toast.makeText(this, R.string.err_external_storage_permissions, Toast.LENGTH_LONG).show();
        }
    }

    private void invokeGetPhoto() {
        // invoke the image gallery using an implict intent.
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);

        // where do we want to find the data?
        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureDirectoryPath = pictureDirectory.getPath();
        // finally, get a URI representation
        Uri data = Uri.parse(pictureDirectoryPath);

        // set the data and type.  Get all image types.
        photoPickerIntent.setDataAndType(data, "image/*");

        // we will invoke this activity, and get something back from it.
        startActivityForResult(photoPickerIntent, PICK_PHOTO_REQUEST);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_PHOTO_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                Uri mProductPhotoUri = data.getData();
                mCurrentPhotoUri = mProductPhotoUri.toString();
                Glide.with(this).load(mProductPhotoUri)
                        .placeholder(ic_insert_placeholder)
                        .crossFade()
                        .fitCenter()
                        .into(mSelectphoto);
            }
        }
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mCurrentItemuri==null){
            MenuItem menudel = menu.findItem(R.id.editor_menu_delete);
            menudel.setVisible(false);
            MenuItem menuorder = menu.findItem(R.id.editor_menu_order_more);
            menuorder.setVisible(false);

        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.editor_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.editor_menu_order_more:
                OrderMore();
                return true;
            case R.id.editor_menu_delete:
                showDeleteConfirmationDialog();
                return true;
            case R.id.editor_menu_save:
                saveItem();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void saveItem(){
        if(((mCurrentItemuri == null) &&
                TextUtils.isEmpty(mNameEditText.getText().toString().trim())) ||
                TextUtils.isEmpty(mPriceEditText.getText().toString().trim()) ||
                TextUtils.isEmpty(mQuantityEditText.getText().toString().trim()) ||
                TextUtils.isEmpty(mEmailEditText.getText().toString().trim())||
                (mSelectphoto.getDrawable()==null)){
            Toast.makeText(getApplicationContext(),R.string.enter_items,Toast.LENGTH_SHORT).show();
        return;}
        ContentValues values = new ContentValues();
        values.put(ItemContract.ItemEntry.COLUMN_ITEM_NAME,mNameEditText.getText().toString().trim());
        values.put(ItemContract.ItemEntry.COLUMN_ITEM_PRICE,Integer.parseInt(mPriceEditText.getText().toString().trim()));
        values.put(ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY,Integer.parseInt(mQuantityEditText.getText().toString().trim()));
        values.put(ItemContract.ItemEntry.COLUMN_ITEM_EMAIL, mEmailEditText.getText().toString().trim());
        values.put(ItemContract.ItemEntry.COLUMN_ITEM_PHOTO, mCurrentPhotoUri);
        if (mCurrentItemuri==null){
            Uri muri;
            muri=getContentResolver().insert(ItemContract.ItemEntry.CONTENT_URI,values);
            if(muri==null){
                Toast.makeText(getApplicationContext(),R.string.itemProvider_insert_error,Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getApplicationContext(),R.string.itemProvider_insert_no_error,Toast.LENGTH_SHORT).show();
            }
        }else {
            int rowsaffected = getContentResolver().update(mCurrentItemuri,values,null,null);
            if(rowsaffected==0){
                Toast.makeText(getApplicationContext(),R.string.itemProvider_insert_error,Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getApplicationContext(),R.string.itemProvider_insert_no_error,Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
   public void deleteItem(){
       int rowdel;
       rowdel = getContentResolver().delete(mCurrentItemuri,null,null);
       if(rowdel==0){
           Toast.makeText(getApplicationContext(),R.string.error_del,Toast.LENGTH_SHORT).show();
       }else {
           Toast.makeText(getApplicationContext(),R.string.no_error_del,Toast.LENGTH_SHORT).show();
           finish();
       }
   }

    @Override
    public void onBackPressed() {
        if(!mItemHasChanged){
            super.onBackPressed();
            return;
        }else {
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);}
    }
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteItem();
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
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection ={ItemContract.ItemEntry._ID,
                ItemContract.ItemEntry.COLUMN_ITEM_NAME,
                ItemContract.ItemEntry.COLUMN_ITEM_PRICE,
                ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY,
                ItemContract.ItemEntry.COLUMN_ITEM_EMAIL,
                ItemContract.ItemEntry.COLUMN_ITEM_PHOTO};
        return new CursorLoader(this,mCurrentItemuri,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()){
            mNameEditText.setText(cursor.getString(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_NAME)));
            mPriceEditText.setText(Integer.toString(cursor.getInt(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_PRICE))));
            mQuantityEditText.setText(Integer.toString(cursor.getInt(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY))));
            mEmailEditText.setText(cursor.getString(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_EMAIL)));
            int photoindex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_PHOTO);
            mCurrentPhotoUri = cursor.getString(photoindex);
            Glide.with(this).load(mCurrentPhotoUri)
                    .placeholder(R.mipmap.ic_launcher)
                    .error(ic_insert_placeholder)
                    .crossFade()
                    .centerCrop()
                    .into(mSelectphoto);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
    private void subtractOneToQuantity() {
        String previousValueString = mQuantityEditText.getText().toString();
        int previousValue;
        if (previousValueString.isEmpty()) {
            return;
        } else if (previousValueString.equals("0")) {
            return;
        } else {
            previousValue = Integer.parseInt(previousValueString);
            mQuantityEditText.setText(String.valueOf(previousValue - 1));
        }
    }
    private void sumOneToQuantity() {
        String previousValueString = mQuantityEditText.getText().toString();
        int previousValue;
        if (previousValueString.isEmpty()) {
            previousValue = 0;
        } else {
            previousValue = Integer.parseInt(previousValueString);
        }
        mQuantityEditText.setText(String.valueOf(previousValue + 1));
    }
    private void OrderMore(){
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + mEmailEditText.getText().toString().trim()));
        intent.putExtra(Intent.EXTRA_SUBJECT,"New Order");
        String body = "Please send :" + mNameEditText.getText().toString().trim() + " As soon AS Possible";
        intent.putExtra(Intent.EXTRA_TEXT,body);
        startActivity(intent);
    }
}
