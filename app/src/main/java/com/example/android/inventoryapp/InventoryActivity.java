package com.example.android.inventoryapp;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.inventoryapp.data.InventoryContract;
import com.example.android.inventoryapp.data.InventoryCursorAdapter;

import java.util.concurrent.ThreadLocalRandom;

/**
 * This project created by Andrew Osborne
 *
 * This project makes use of the Glide project
 * which can be found at: https://github.com/bumptech/glide
 */

public class InventoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    /** Used for log tags */
    private static final String LOG_TAG = InventoryActivity.class.getSimpleName();
    /** A unique ID used for the loader */
    private static final int UNIQUE_ID_FOR_LOADER = 0;
    /** The adapter used to display the inventory list */
    private InventoryCursorAdapter mCursorAdapter;
    /** Used for getting permissions */
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        verifyStoragePermissions(this);

        // Create a floating action button (fab) that the user clicks
        // to create a new inventory item
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch the EditorActivity when user clicks the fab
                Intent intent = new Intent(InventoryActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView that will be used for the inventory data
        ListView listView = (ListView)findViewById(R.id.list);

        // Create an empty view
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        // Now create an empty adapter that will be used to display the inventory table
        // and set it to the listView
        mCursorAdapter = new InventoryCursorAdapter(this, null);
        listView.setAdapter(mCursorAdapter);

        // Set an OnClick listener to allow a user click to open the detail/editor activity
        // for each item
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long idOfItem) {
                Intent intent = new Intent(InventoryActivity.this, EditorActivity.class);
                // idOfItem is the item's _id value. Build the URI from this number
                Uri currentItemUri = ContentUris.withAppendedId(
                        InventoryContract.InventoryEntry.CONTENT_URI,
                        idOfItem);
                // set this to the data the intent will operate on
                intent.setData(currentItemUri);
                startActivity(intent);
            }
        });

        // Prepare the loader
        getSupportLoaderManager().initLoader(UNIQUE_ID_FOR_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_inventory, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_insert_data:
                insertData();
                return true;
            case R.id.action_delete_table:
                deleteTable();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // TODO: Delete this method before publishing.
    /**
     * Generate a fake database item and insert it into the database table
     *
     * Testing purposes only!
     */
    private void insertData() {
        // *************************************delete this section*********************************
        // Generate a random number between 1 and 5
        int randomNumber = 1 + (int)(Math.random() * ((5-1) + 1));
        ContentValues values = new ContentValues();

        switch (randomNumber) {
            case 1:
                values.put(InventoryContract.InventoryEntry.COLUMN_NAME_NAME, "Peaches");
                values.put(InventoryContract.InventoryEntry.COLUMN_NAME_STOCK, 1);
                values.put(InventoryContract.InventoryEntry.COLUMN_NAME_PRICE, 150);
                break;
            case 2:
                values.put(InventoryContract.InventoryEntry.COLUMN_NAME_NAME, "Figs");
                values.put(InventoryContract.InventoryEntry.COLUMN_NAME_STOCK, 1);
                values.put(InventoryContract.InventoryEntry.COLUMN_NAME_PRICE, 250);
                break;
            case 3:
                values.put(InventoryContract.InventoryEntry.COLUMN_NAME_NAME, "Oranges");
                values.put(InventoryContract.InventoryEntry.COLUMN_NAME_STOCK, 1);
                values.put(InventoryContract.InventoryEntry.COLUMN_NAME_PRICE, 350);
                break;
            case 4:
                values.put(InventoryContract.InventoryEntry.COLUMN_NAME_NAME, "Bananas");
                values.put(InventoryContract.InventoryEntry.COLUMN_NAME_STOCK, 1);
                values.put(InventoryContract.InventoryEntry.COLUMN_NAME_PRICE, 45);
                break;
            default:
                values.put(InventoryContract.InventoryEntry.COLUMN_NAME_NAME, "Apples");
                values.put(InventoryContract.InventoryEntry.COLUMN_NAME_STOCK, 1);
                values.put(InventoryContract.InventoryEntry.COLUMN_NAME_PRICE, 550);
                break;
        }
        // Defines a new Uri object that will receive the result of insertion
        Uri mNewUri = getContentResolver().insert(
                InventoryContract.InventoryEntry.CONTENT_URI, values);
        //******************************************************************************************
    }

    // TODO: Delete this method before publishing
    /**
     * Delete the entire database table
     *
     * Testing purposes only!
     */
    private void deleteTable() {
        // Defines a new int that will receive the result of the deletion
        int rowsDeleted = getContentResolver().delete(
                InventoryContract.InventoryEntry.CONTENT_URI, null, null);
    }

    private static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission, so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Projection used to perform the query
        String[] projection = {
                InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.COLUMN_NAME_NAME,
                InventoryContract.InventoryEntry.COLUMN_NAME_PRICE,
                InventoryContract.InventoryEntry.COLUMN_NAME_STOCK,
                InventoryContract.InventoryEntry.COLUMN_NAME_IMAGE};

        return new CursorLoader(getApplicationContext(),
                InventoryContract.InventoryEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        // Swap the new cursor in. Note: The framework will take care of closing
        // the old cursor once we return
        mCursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last cursor provided to onLoadFinished() is going to be
        // closed. We need to make sure that we are no longer using it.
        mCursorAdapter.swapCursor(null);

    }
}
