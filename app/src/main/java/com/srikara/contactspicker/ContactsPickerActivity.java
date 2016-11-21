package com.srikara.contactspicker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.srikara.contactspicker.adapters.ContactsAdapter;
import com.srikara.contactspicker.adapters.ContactsHolder;
import com.srikara.contactspicker.model.Contact;

import java.util.List;

public class ContactsPickerActivity extends AppCompatActivity implements ContactsHolder.ClickListener{

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private String TAG = ContactsPickerActivity.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private ContactsAdapter mAdapter;
    private ActionMode mActionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_picker);


        if (!checkPermissions()) {
            requestPermissions();
        }else{
            retrieveContacts();
        }

        initViews();
    }

    private void initViews() {
        mRecyclerView = (RecyclerView)findViewById(R.id.rv_contacts);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL);

        mRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void retrieveContacts(){

        if(checkPermissions()) {

            ContactsFetcher contactsFetcher = new ContactsFetcher(this);

            contactsFetcher.fetchAll(new ContactLoaderCallback() {
                @Override
                public void onLoadCompleted(List<Contact> contactList) {
                    Toast.makeText(ContactsPickerActivity.this, "Total contacts fetched: " + contactList.size(), Toast.LENGTH_SHORT).show();

                    mAdapter = new ContactsAdapter(ContactsPickerActivity.this, contactList, ContactsPickerActivity.this);
                    mRecyclerView.setAdapter(mAdapter);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_CONTACTS);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            Snackbar.make(
                    findViewById(R.id.main_activity_view),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(ContactsPickerActivity.this,
                                    new String[]{Manifest.permission.READ_CONTACTS},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    })
                    .show();
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(ContactsPickerActivity.this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted.
                retrieveContacts();
            } else {
                // Permission denied.

                // In this Activity we've chosen to notify the user that they
                // have rejected a core permission for the app since it makes the Activity useless.
                // We're communicating this message in a Snackbar since this is a sample app, but
                // core permissions would typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                Snackbar.make(
                        findViewById(R.id.main_activity_view),
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        }
    }

    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.toolbar_cab, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            Log.d(TAG, "" + mAdapter.getSelectedItems().toString());
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mAdapter.clearSelection();
            mActionMode = null;
        }
    };

    @Override
    public void onItemClicked (int position) {
        toggleSelection(position);
    }

    @Override
    public boolean onItemLongClicked (int position) {
        toggleSelection(position);

        return true;
    }

    private void toggleSelection(int position) {
        mAdapter.toggleSelection(position);

        if(mAdapter.getSelectedItemCount() > 0){
            if(mActionMode == null) {
                mActionMode = startSupportActionMode(actionModeCallback);
            }
        }else{
            if (mActionMode != null) {
                mActionMode.finish();
            }
        }
    }
}
