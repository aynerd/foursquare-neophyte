package com.inveniotechnologies.neophyte;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.service.notification.NotificationListenerService;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.inveniotechnologies.neophyte.Extras.DividerItemDecoration;
import com.inveniotechnologies.neophyte.ListAdapters.DateListAdapter;
import com.inveniotechnologies.neophyte.ListItems.DateListItem;
import com.inveniotechnologies.neophyte.Models.Record;
import com.inveniotechnologies.neophyte.Models.Release;
import com.inveniotechnologies.neophyte.REST.ApiClient;
import com.inveniotechnologies.neophyte.REST.ApiInterface;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.R.attr.configChanges;
import static android.R.attr.id;

public class Home extends AppCompatActivity {
    NotificationCompat.Builder notificationBuilder;
    NotificationManager notificationManager;
    //
    private RecyclerView lst_dates;
    private List<DateListItem> datesList = new ArrayList<>();
    private DateListAdapter datesAdapter;
    //
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        /* Enable disk persistence */
        if (savedInstanceState == null) {
            database = FirebaseDatabase.getInstance();
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        } else {
            database = FirebaseDatabase.getInstance();
        }
        /* Check for updates */
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Release> call = apiInterface.getReleases();
        call.enqueue(new Callback<Release>() {
            @Override
            public void onResponse(Call<Release> call, Response<Release> response) {
                final Release release = response.body();
                String tagName = release.getTagName();
                final String versionName = BuildConfig.VERSION_NAME;
                if (!tagName.equals(versionName)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                    builder.setMessage("There is a new version.\nName: " + release.getName() + "\nVersion: " + release.getTagName() + "\nDo you want to download it?").setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (release.getAssets().size() > 0) {
                                final String updateUrl = release.getAssets().get(0).getDownloadUrl();
                                notificationBuilder = new NotificationCompat.Builder(Home.this);
                                notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                notificationBuilder.setContentTitle("Foursquare Update")
                                        .setContentText("Download in progress")
                                        .setSmallIcon(R.mipmap.ic_launcher);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            int count;
                                            URL url = new URL(updateUrl);
                                            URLConnection urlConnection = url.openConnection();
                                            urlConnection.connect();
                                            //
                                            int fileLength = urlConnection.getContentLength();
                                            Log.d("Updater:", "File length " + fileLength);
                                            //
                                            InputStream inputStream = new BufferedInputStream(url.openStream());
                                            final File folder = new File(Environment.getExternalStorageDirectory() + "/FoursquareNewcomers/Updates");
                                            if (!folder.exists())
                                                folder.mkdir();
                                            final File file = new File(folder.getAbsolutePath() + "/" + versionName + ".apk");
                                            if (!file.exists())
                                                file.createNewFile();
                                            OutputStream outputStream = new FileOutputStream(file);

                                            byte data[] = new byte[1024];
                                            long total = 0;
                                            while ((count = inputStream.read(data)) != -1) {
                                                total += count;
                                                notificationBuilder.setProgress(100, (int) ((total * 100) / fileLength), false);
                                                notificationManager.notify(id, notificationBuilder.build());
                                                outputStream.write(data, 0, count);
                                            }
                                            outputStream.flush();
                                            outputStream.close();
                                            inputStream.close();
                                            //
                                            notificationBuilder.setContentText("Download complete")
                                                    .setProgress(0, 0, false);
                                            notificationManager.notify(id, notificationBuilder.build());
                                        } catch (Exception e) {
                                            Log.d("Updater:", "Error occurred.");
                                        }
                                    }
                                }).start();
                            }
                        }
                    }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("Updater:", "Declined to update.");
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.setTitle("Update App");
                    alertDialog.show();
                }
            }

            @Override
            public void onFailure(Call<Release> call, Throwable t) {
                Log.d("Updater Error:", t.toString());
            }
        });
        //
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NewRecord.class);
                startActivity(intent);
            }
        });

        lst_dates = (RecyclerView) findViewById(R.id.lst_dates);
        //
        datesAdapter = new DateListAdapter(datesList);
        //
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        lst_dates.setLayoutManager(layoutManager);
        lst_dates.setItemAnimator(new DefaultItemAnimator());
        lst_dates.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        lst_dates.setAdapter(datesAdapter);
        //
//        database = FirebaseDatabase.getInstance();
        //
        DatabaseReference membersRef = database.getReference("members");
        membersRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot != null){
                    String date = dataSnapshot.getKey();
                    //
                    DateListItem item = new DateListItem();
                    item.setDate(date);
                    //
                    datesList.add(item);
                    datesAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if(dataSnapshot != null) {
                    String date = dataSnapshot.getKey();
                    //
                    DateListItem item = new DateListItem();
                    for (int i = 0; i < datesList.size(); i++) {
                        if(datesList.get(i).getDate().equals(date)) {
                            item = datesList.get(i);
                            break;
                        }
                    }
                    //
                    datesList.remove(item);
                    datesAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Home.this, "Sorry, an error occurred while trying to read data.", Toast.LENGTH_LONG).show();
                finish();
            }
        });

        lst_dates.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), lst_dates, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                DateListItem item = datesList.get(position);
                //
                Intent intent = new Intent(getApplicationContext(), People.class);
                intent.putExtra("date", item.getDate());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {
                final DateListItem item = datesList.get(position);
                //
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
                        return  true;
                    }
                });
                popupMenu.show();
            }
        }));
    }

    private void createCSV(DateListItem item) {
        final File folder = new File(Environment.getExternalStorageDirectory() + "/FoursquareNewcomers");
        if (!folder.exists())
            folder.mkdir();
        final String filename = item.getDate() + ".csv";
        final StringBuilder csvBuilder = new StringBuilder();
        //
        DatabaseReference membersRef = database.getReference("members");
        DatabaseReference dateRef = membersRef.child(item.getDate());
        dateRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot != null) {
                    //
                    csvBuilder.append("Full Name");
                    csvBuilder.append('\t');

                    csvBuilder.append("Age Group");
                    csvBuilder.append('\t');

                    csvBuilder.append("Birthday");
                    csvBuilder.append('\t');

                    csvBuilder.append("Comments");
                    csvBuilder.append('\t');

                    csvBuilder.append("Decisions");
                    csvBuilder.append('\t');

                    csvBuilder.append("Email");
                    csvBuilder.append('\t');

                    csvBuilder.append("Home Address");
                    csvBuilder.append('\t');

                    csvBuilder.append("Home Tel");
                    csvBuilder.append('\t');

                    csvBuilder.append("Invited By");
                    csvBuilder.append('\t');

                    csvBuilder.append("Mobile");
                    csvBuilder.append('\t');

                    csvBuilder.append("Office Tel");
                    csvBuilder.append('\t');

                    csvBuilder.append("Title");

                    csvBuilder.append('\n');
                    //
                    for(DataSnapshot personShot : dataSnapshot.getChildren()) {
                        Record record = personShot.getValue(Record.class);
                        //
                        csvBuilder.append(record.getFullName());
                        csvBuilder.append('\t');

                        csvBuilder.append(record.getAgeGroup());
                        csvBuilder.append('\t');

                        csvBuilder.append(record.getBirthDay());
                        csvBuilder.append('\t');

                        csvBuilder.append(record.getComments());
                        csvBuilder.append('\t');

                        csvBuilder.append(record.getDecisions());
                        csvBuilder.append('\t');

                        csvBuilder.append(record.getEmail());
                        csvBuilder.append('\t');

                        csvBuilder.append(record.getHomeAddress());
                        csvBuilder.append('\t');

                        csvBuilder.append(record.getHomeTel());
                        csvBuilder.append('\t');

                        csvBuilder.append(record.getInvitedBy());
                        csvBuilder.append('\t');

                        csvBuilder.append(record.getMobile());
                        csvBuilder.append('\t');

                        csvBuilder.append(record.getOfficeTel());
                        csvBuilder.append('\t');

                        csvBuilder.append(record.getTitle());
                        //
                        csvBuilder.append('\n');
                    }
                    FileOutputStream outputStream;
                    try {
                        File file = new File(folder, filename);
                        if(file.exists()) {
                            file.delete();
                        }
                        outputStream = new FileOutputStream(file);
                        outputStream.write(csvBuilder.toString().getBytes());
                        outputStream.close();
                        //
                        Toast.makeText(Home.this, "File successfully exported. " + filename, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(Home.this, "Sorry, could not write the file.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Home.this, "Sorry, an error occured and the data was not exported.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private Home.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final Home.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
}

