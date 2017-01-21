package com.inveniotechnologies.neophyte.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.inveniotechnologies.neophyte.ui.extras.DividerItemDecoration;
import com.inveniotechnologies.neophyte.ui.adapters.PersonListAdapter;
import com.inveniotechnologies.neophyte.ui.listitems.PersonListItem;
import com.inveniotechnologies.neophyte.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class People extends AppCompatActivity {
    private FirebaseDatabase database;

    @BindView(R.id.lst_people)
    private RecyclerView lst_people;
    //
    private List<PersonListItem> personsList = new ArrayList<>();
    private PersonListAdapter personsAdapter;
    //
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);
        //
        ButterKnife.bind(this);
        //
        Intent intent = getIntent();
        date = intent.getStringExtra("date");
        //
        personsAdapter = new PersonListAdapter(personsList);
        //
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        lst_people.setLayoutManager(layoutManager);
        lst_people.setItemAnimator(new DefaultItemAnimator());
        lst_people.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        lst_people.setAdapter(personsAdapter);
        //
        database = FirebaseDatabase.getInstance();
        DatabaseReference membersRef = database.getReference("members");
        DatabaseReference dateRef = membersRef.child(date);
        dateRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot != null) {
                    String key = dataSnapshot.getKey();
                    String fullName = dataSnapshot.child("fullName").getValue().toString();
                    String mobile = dataSnapshot.child("mobile").getValue().toString();
                    //
                    PersonListItem item = new PersonListItem();
                    item.setMobile(mobile);
                    item.setFullName(fullName);
                    item.setUID(key);
                    //
                    personsList.add(item);
                    personsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot != null) {
                    String key = dataSnapshot.getKey();
                    String fullName = dataSnapshot.child("fullName").getValue().toString();
                    String mobile = dataSnapshot.child("mobile").getValue().toString();
                    //
                    PersonListItem item = new PersonListItem();
                    for (int i = 0; i < personsList.size(); i++) {
                        if(personsList.get(i).getUID().equals(key)) {
                            item = personsList.get(i);
                            break;
                        }
                    }
                    item.setFullName(fullName);
                    item.setMobile(mobile);
                    //
                    personsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if(dataSnapshot != null) {
                    String key = dataSnapshot.getKey();
                    //
                    PersonListItem item = new PersonListItem();
                    for (int i = 0; i < personsList.size(); i++) {
                        if(personsList.get(i).getUID().equals(key)) {
                            item = personsList.get(i);
                            break;
                        }
                    }
                    //
                    personsList.remove(item);
                    personsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(
                        People.this,
                        "Sorry, an error occurred while trying to read data.",
                        Toast.LENGTH_LONG
                ).show();
                finish();
            }
        });

        lst_people.addOnItemTouchListener(
                new Home.RecyclerTouchListener(getApplicationContext(),
                        lst_people,
                        new Home.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                PersonListItem item = personsList.get(position);
                //
                Intent intent = new Intent(getApplicationContext(), Details.class);
                intent.putExtra("date", date);
                intent.putExtra("Uid", item.getUID());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {
                final PersonListItem item = personsList.get(position);
                //
                PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                People.this.getMenuInflater().inflate(R.menu.menu_person, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.menu_delete:
                                AlertDialog.Builder builder = new AlertDialog.Builder(People.this);
                                builder.setCancelable(true);
                                builder.setMessage("Are you sure you want to delete this person?");
                                builder.setPositiveButton(
                                        "Yes",
                                        new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        DatabaseReference membersRef = database
                                                .getReference("members");
                                        DatabaseReference dateRef = membersRef.child(date);
                                        DatabaseReference idRef = dateRef.child(item.getUID());
                                        idRef.removeValue(
                                                new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(
                                                    DatabaseError databaseError,
                                                    DatabaseReference databaseReference) {
                                                Toast.makeText(
                                                        People.this,
                                                        "Record has been deleted.",
                                                        Toast.LENGTH_SHORT
                                                ).show();
                                            }
                                        });
                                    }
                                });
                                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //Do nothing
                                    }
                                });
                                //
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                                break;
                            case  R.id.menu_edit:
                                Intent intent = new Intent(
                                        getApplicationContext(),
                                        EditPerson.class
                                );
                                intent.putExtra("date", date);
                                intent.putExtra("Uid", item.getUID());
                                startActivity(intent);
                                break;
                        }
                        return  true;
                    }
                });
                popupMenu.show();
            }
        }));
    }
}
