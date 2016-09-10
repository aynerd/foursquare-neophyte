package com.inveniotechnologies.neophyte;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.inveniotechnologies.neophyte.Extras.DividerItemDecoration;
import com.inveniotechnologies.neophyte.ListAdapters.PersonListAdapter;
import com.inveniotechnologies.neophyte.ListItems.DateListItem;
import com.inveniotechnologies.neophyte.ListItems.PersonListItem;

import java.util.ArrayList;
import java.util.List;

public class People extends AppCompatActivity {
    private FirebaseDatabase database;
    //
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
        Intent intent = getIntent();
        date = intent.getStringExtra("date");
        //
        personsAdapter = new PersonListAdapter(personsList);
        //
        lst_people = (RecyclerView) findViewById(R.id.lst_people);
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

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        lst_people.addOnItemTouchListener(new Home.RecyclerTouchListener(getApplicationContext(), lst_people, new Home.ClickListener() {
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

            }
        }));
    }
}
