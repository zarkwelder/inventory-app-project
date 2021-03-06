package com.example.android.inventoryapp.data;

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
import com.example.android.inventoryapp.R;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by Zark on 2/3/2017.
 *
 * This is an adapter for a list which uses the inventory database as it's data source.
 */

public class InventoryCursorAdapter extends CursorAdapter {

    /** Used to convert the price int from the database into readable price format */
    private static final int PRICE_CONVERSION_FACTOR = 100;
    /** Used to decrement an item's stock level when the "sell" button is clicked */
    private static final int SELL_ONE_ITEM = 1;
    /** Used to logging errors */
    private static final String LOG_TAG = InventoryCursorAdapter.class.getSimpleName();

    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    private class ViewHolder {
        TextView nameTextView;
        TextView priceTextView;
        TextView stockTextView;
        ImageView thumbnailView;

        ViewHolder(View view) {
            nameTextView = (TextView)view.findViewById(R.id.name);
            priceTextView = (TextView)view.findViewById(R.id.price);
            stockTextView = (TextView)view.findViewById(R.id.stock);
            thumbnailView = (ImageView)view.findViewById(R.id.thumbnail_image);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {

        ViewHolder holder = new ViewHolder(view);

        // Extract relevant properties from the database cursor
        String nameString = cursor.getString(cursor.getColumnIndexOrThrow(
                InventoryContract.InventoryEntry.COLUMN_NAME_NAME));
        int priceInt = cursor.getInt(cursor.getColumnIndexOrThrow(
                InventoryContract.InventoryEntry.COLUMN_NAME_PRICE));
        int stockLevel = cursor.getInt(cursor.getColumnIndexOrThrow(
                InventoryContract.InventoryEntry.COLUMN_NAME_STOCK));
        String imageString = cursor.getString(cursor.getColumnIndexOrThrow(
                InventoryContract.InventoryEntry.COLUMN_NAME_IMAGE));

        // Populate the fields using the extracted data
        holder.nameTextView.setText(nameString);
        holder.stockTextView.setText(Integer.toString(stockLevel));

        // Convert the price int stored in the database into the currency format for display
        // The int was multiplied by 100 when it was entered to store it as cents,
        // so we undo this operation here
        double priceDouble = (double) priceInt / PRICE_CONVERSION_FACTOR;
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
        holder.priceTextView.setText(currencyFormatter.format(priceDouble));

        // Set the thumbnail image
        if (imageString != null) {
            //older.thumbnailView.setImageURI(Uri.parse(imageString));
            Glide.with(context).load(Uri.parse(imageString)).into(holder.thumbnailView);
        }

        // Create a button to "sell" an item (decrease the stock level)
        // First, get the item's position
        int CURRENT_ROW_ID = cursor.getInt(cursor.getColumnIndexOrThrow(
                InventoryContract.InventoryEntry._ID));
        // Get the current item's URI
        final Uri CURRENT_ROW_URI = ContentUris.withAppendedId(
                InventoryContract.InventoryEntry.CONTENT_URI, ((long) CURRENT_ROW_ID));
        // This int is 1 less than the current stock level
        final int newStockLevel = stockLevel - SELL_ONE_ITEM;

        // Get a reference to the "sell" button and set the click listener
        Button sellButton = (Button)view.findViewById(R.id.button_sell);
        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Reduce the stock level, but not less than 0
                if (newStockLevel >= 0) {
                    ContentValues newStockValue = new ContentValues();
                    newStockValue.put(InventoryContract.InventoryEntry.COLUMN_NAME_STOCK,
                            newStockLevel);

                    view.getContext().getContentResolver().update(CURRENT_ROW_URI,
                            newStockValue,
                            null,
                            null);
                }

            }
        });
    }
}
