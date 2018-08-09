package com.inveniotechnologies.neophyte.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.inveniotechnologies.neophyte.R;
import com.inveniotechnologies.neophyte.network.models.Record;
import com.inveniotechnologies.neophyte.ui.adapters.DateListAdapter;
import com.inveniotechnologies.neophyte.ui.extras.ClickListener;
import com.inveniotechnologies.neophyte.ui.extras.DividerItemDecoration;
import com.inveniotechnologies.neophyte.ui.extras.RecyclerTouchListener;
import com.inveniotechnologies.neophyte.ui.listitems.DateListItem;
import com.inveniotechnologies.neophyte.ui.util.UpdateManager;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Home extends AppCompatActivity {
    private final String TAG = "Neophyte";

    @BindView(R.id.lst_dates)
    RecyclerView lst_dates;

    private List<DateListItem> datesList = new ArrayList<>();
    private DateListAdapter datesAdapter;

    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

        database = FirebaseDatabase.getInstance();
        /* Enable disk persistence */
        if (savedInstanceState == null) {
            try {
                database.setPersistenceEnabled(true);
            } catch (DatabaseException ex) {
                Log.e("Firebase", "An error occurs while persisting storage " + ex.getMessage());
            }
            checkForUpdates();
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddRecord.class);
                startActivity(intent);
            }
        });

        if (ActivityCompat.shouldShowRequestPermissionRationale(Home.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(Home.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        } else {
            ActivityCompat.requestPermissions(Home.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }

        lst_dates = findViewById(R.id.lst_dates);

        datesAdapter = new DateListAdapter(datesList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        lst_dates.setLayoutManager(layoutManager);
        lst_dates.setItemAnimator(new DefaultItemAnimator());
        lst_dates.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        lst_dates.setAdapter(datesAdapter);

        DatabaseReference membersRef = database.getReference("members");
        membersRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null) {
                    String date = dataSnapshot.getKey();
                    DateListItem item = new DateListItem();
                    item.setDate(date);
                    datesList.add(item);
                    datesAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) { }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    String date = dataSnapshot.getKey();

                    DateListItem item = new DateListItem();
                    for (int i = 0; i < datesList.size(); i++) {
                        if (datesList.get(i).getDate().equals(date)) {
                            item = datesList.get(i);
                            break;
                        }
                    }

                    datesList.remove(item);
                    datesAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) { }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                displayToast("Sorry, an error occurred while trying to read data.");
                finish();
            }
        });

        lst_dates.addOnItemTouchListener(new RecyclerTouchListener(
                getApplicationContext(),
                lst_dates,
                new ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        DateListItem item = datesList.get(position);

                        Intent intent = new Intent(getApplicationContext(), AllRecords.class);
                        intent.putExtra("date", item.getDate());
                        startActivity(intent);
                    }

                    @Override
                    public void onLongClick(View view, int position) {
                        final DateListItem item = datesList.get(position);

                        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                        Home.this.getMenuInflater().inflate(R.menu.menu_day, popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                switch (menuItem.getItemId()) {
                                    case R.id.menu_export:
                                        createCSV(item);
                                        break;
                                }
                                return true;
                            }
                        });
                        popupMenu.show();
                    }
                }));
    }

    private void checkForUpdates() {
        UpdateManager updateManager = new UpdateManager();
        updateManager.update(this);
    }

    private void createCSV(DateListItem item) {
        final File folder = new File(
                Environment.getExternalStorageDirectory()
                        + "/FoursquareNewcomers"
        );
        if (!folder.exists())
            folder.mkdir();
        final String filename = item.getDate() + ".csv";

        DatabaseReference membersRef = database.getReference("members");
        DatabaseReference dateRef = membersRef.child(item.getDate());
        dateRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    final StringBuilder csvBuilder = getCsvFromSnapshot(dataSnapshot);

                    writeCsvToFile(folder, filename, csvBuilder);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                displayToast("Sorry, an error occured and the data was not exported.");
            }
        });
    }

    private StringBuilder getCsvFromSnapshot(DataSnapshot dataSnapshot) {
        StringBuilder csvBuilder = new StringBuilder();

        csvBuilder.append("S/No");
        csvBuilder.append('\t');

        csvBuilder.append("Title");
        csvBuilder.append('\t');

        csvBuilder.append("Full Name");
        csvBuilder.append('\t');

        csvBuilder.append("Mobile");
        csvBuilder.append('\t');

        csvBuilder.append("Email");
        csvBuilder.append('\t');

        csvBuilder.append("Home Address");
        csvBuilder.append('\t');

        csvBuilder.append("Birthday");
        csvBuilder.append('\t');

        csvBuilder.append("Age Group");
        csvBuilder.append('\t');

        csvBuilder.append("Home Tel");
        csvBuilder.append('\t');

        csvBuilder.append("Office Tel");
        csvBuilder.append('\t');

        csvBuilder.append("Invited By");
        csvBuilder.append('\t');

        csvBuilder.append("Comments");
        csvBuilder.append('\t');

        csvBuilder.append("Decisions");

        csvBuilder.append('\n');

        int index = 1;
        for (DataSnapshot personShot : dataSnapshot.getChildren()) {
            Record record = personShot.getValue(Record.class);

            csvBuilder.append(index);
            csvBuilder.append('\t');

            csvBuilder.append(cleanString(record.getTitle()));
            csvBuilder.append('\t');

            csvBuilder.append(cleanString(record.getFullName()));
            csvBuilder.append('\t');

            csvBuilder.append(cleanString(record.getMobile()));
            csvBuilder.append('\t');

            csvBuilder.append(cleanString(record.getEmail()));
            csvBuilder.append('\t');

            csvBuilder.append(cleanString(record.getHomeAddress()));
            csvBuilder.append('\t');

            csvBuilder.append(cleanString(record.getBirthDay()));
            csvBuilder.append('\t');

            csvBuilder.append(cleanString(record.getAgeGroup()));
            csvBuilder.append('\t');

            csvBuilder.append(cleanString(record.getHomeTel()));
            csvBuilder.append('\t');

            csvBuilder.append(cleanString(record.getOfficeTel()));
            csvBuilder.append('\t');

            csvBuilder.append(cleanString(record.getInvitedBy()));
            csvBuilder.append('\t');

            csvBuilder.append(cleanString(record.getComments()));
            csvBuilder.append('\t');

            csvBuilder.append(cleanString(record.getDecisions()));
            csvBuilder.append('\t');

            csvBuilder.append('\n');

            index++;
        }
        return csvBuilder;
    }

    private void writeCsvToFile(File folder, String filename, StringBuilder csvBuilder) {
        if (!isWriteStoragePermissionGranted()) {
            displayToast("Access to storage is required for this app to work. Please grant and try again.");
            ActivityCompat.requestPermissions(Home.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
            return;
        }

        FileOutputStream outputStream;
        try {
            File file = new File(folder, filename);
            if (file.exists()) {
                file.delete();
            }
            outputStream = new FileOutputStream(file);
            outputStream.write(csvBuilder.toString().getBytes());
            outputStream.close();

            displayToast("File successfully exported. "
                    + filename);
        } catch (Exception e) {
            displayToast("Sorry, could not write the file.");
        }
    }

    private void displayToast(String message) {
        Toast.makeText(
                Home.this,
                message,
                Toast.LENGTH_SHORT
        ).show();
    }

    private String cleanString(String input) {
        return input
                .replace('\n', ' ')
                .replace('\t', ' ');
    }

    private boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Write permission granted.");
                return true;
            } else {

                Log.v(TAG, "Write permission revoked,");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        } else {
            Log.v(TAG, "SDK less than Marshmallow, permission granted by default.");
            return true;
        }
    }
}

