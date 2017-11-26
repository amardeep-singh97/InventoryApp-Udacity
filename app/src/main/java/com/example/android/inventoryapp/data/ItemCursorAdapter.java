package com.example.android.inventoryapp.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.inventoryapp.EditorActivity;
import com.example.android.inventoryapp.R;
import static com.example.android.inventoryapp.R.drawable.ic_insert_placeholder;
/**
 * Created by Amardeep on 7/31/2017.
 */

public class ItemCursorAdapter extends CursorAdapter {
    private EditorActivity activity;
    private ItemDbHelper mDbHelper;
    public ItemCursorAdapter(Context context, Cursor c) {
        super(context,c,0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item,viewGroup,false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        TextView tvname = (TextView) view.findViewById(R.id.show_product_name);
        TextView tvprice = (TextView) view.findViewById(R.id.show_product_price);
        final TextView tvquantity = (TextView) view.findViewById(R.id.show_product_quantity);
        ImageView tvimage = (ImageView) view.findViewById(R.id.show_product_image);
        Button sellButton = (Button) view.findViewById(R.id.sell_button);
        String name = cursor.getString(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_NAME));
        int image = cursor.getInt(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_PHOTO));
        int price = cursor.getInt(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_PRICE));
        final int quantity = cursor.getInt(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY));
        final int id = cursor.getInt(cursor.getColumnIndex(ItemContract.ItemEntry._ID));
        final Uri currenturi = ContentUris.withAppendedId(ItemContract.ItemEntry.CONTENT_URI,id);
        tvname.setText(name);
        tvimage.setImageResource(image);
        tvprice.setText(String.valueOf(price));
        tvquantity.setText(String.valueOf(quantity));
        Uri thumbUri = Uri.parse(cursor.getString(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_PHOTO)));
        Glide.with(context).load(thumbUri)
                .placeholder(R.mipmap.ic_launcher)
                .error(ic_insert_placeholder)
                .crossFade()
                .centerCrop()
                .into(tvimage);
        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentResolver resolver = view.getContext().getContentResolver();
                ContentValues values = new ContentValues();
                if (quantity > 0) {
                    int quantity = cursor.getInt(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY));
                    int quantityValue = quantity;
                    values.put(ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY, --quantityValue);
                    int rowupdated = resolver.update(currenturi, values, null, null);
                    context.getContentResolver().notifyChange(currenturi, null);
                }
            }
        });
    }
}
