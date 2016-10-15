package com.inveniotechnologies.neophyte;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.inveniotechnologies.neophyte.Models.Record;

public class Details extends AppCompatActivity {
    private TextView lbl_title;
    private TextView lbl_full_name;
    private TextView lbl_age_group;
    private TextView lbl_comments;
    private TextView lbl_home_address;
    private TextView lbl_home_tel;
    private TextView lbl_mobile;
    private TextView lbl_office_tel;
    private TextView lbl_email;
    private TextView lbl_invited_by;
    private TextView lbl_birthday;
    private TextView lbl_spiritual_rebirth_date;
    //
    private TextView lbl_commit_life;
    private TextView lbl_renew_commitment;
    private TextView lbl_be_baptized;
    private TextView lbl_talk_pastorate;
    private TextView lbl_become_member;
    private TextView lbl_discover_maturity;
    private TextView lbl_discover_ministry;
    //
    private Button btn_close;
    //
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        //
        Intent intent = getIntent();
        String date = intent.getStringExtra("date");
        String Uid = intent.getStringExtra("Uid");
        //
        lbl_full_name = (TextView) findViewById(R.id.lbl_full_name);
        lbl_home_address = (TextView) findViewById(R.id.lbl_home_address);
        lbl_home_tel = (TextView) findViewById(R.id.lbl_home_tel);
        lbl_office_tel = (TextView) findViewById(R.id.lbl_office_tel);
        lbl_mobile = (TextView) findViewById(R.id.lbl_mobile);
        lbl_email = (TextView) findViewById(R.id.lbl_email_address);
        lbl_invited_by = (TextView) findViewById(R.id.lbl_invited_by);
        lbl_comments = (TextView) findViewById(R.id.lbl_comments);
        lbl_age_group = (TextView) findViewById(R.id.lbl_age_group);
        lbl_title = (TextView) findViewById(R.id.lbl_title);
        lbl_birthday = (TextView) findViewById(R.id.lbl_birthday);
        lbl_spiritual_rebirth_date = (TextView) findViewById(R.id.lbl_spiritual_rebirth_date);
        //
        lbl_be_baptized = (TextView) findViewById(R.id.lbl_be_baptized);
        lbl_become_member = (TextView) findViewById(R.id.lbl_become_member);
        lbl_commit_life = (TextView) findViewById(R.id.lbl_commit_life);
        lbl_discover_maturity = (TextView) findViewById(R.id.lbl_discover_maturity);
        lbl_discover_ministry = (TextView) findViewById(R.id.lbl_discover_ministry);
        lbl_renew_commitment = (TextView) findViewById(R.id.lbl_renew_commitment);
        lbl_talk_pastorate = (TextView) findViewById(R.id.lbl_talk_pastorate);
        //
        btn_close = (Button) findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //
        database = FirebaseDatabase.getInstance();
        DatabaseReference membersRef = database.getReference("members");
        DatabaseReference dateRef = membersRef.child(date);
        DatabaseReference idRef = dateRef.child(Uid);
        idRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Record record = dataSnapshot.getValue(Record.class);
                if(record != null) {
                    lbl_birthday.setText(record.getBirthDay());
                    lbl_full_name.setText(record.getFullName());
                    lbl_comments.setText(record.getComments());
                    lbl_title.setText(record.getTitle());
                    lbl_age_group.setText(record.getAgeGroup());
                    lbl_email.setText(record.getEmail());
                    lbl_home_address.setText(record.getHomeAddress());
                    lbl_home_tel.setText(record.getHomeTel());
                    lbl_mobile.setText(record.getMobile());
                    lbl_office_tel.setText(record.getOfficeTel());
                    lbl_invited_by.setText(record.getInvitedBy());
                    lbl_spiritual_rebirth_date.setText(record.getDateOfSpiritualRebirth());
                    //
                    String[] decisions = record.getDecisions().split(";");
                    for(int i = 0; i < decisions.length; i++) {
                        String decision = decisions[i];
                        if(decision.contains("pastorate")) {
                            lbl_talk_pastorate.setVisibility(View.VISIBLE);
                        }
                        else if(decision.contains("maturity")) {
                            lbl_discover_maturity.setVisibility(View.VISIBLE);
                        }
                        else if(decision.contains("ministry")) {
                            lbl_discover_ministry.setVisibility(View.VISIBLE);
                        }
                        else if(decision.contains("member")) {
                            lbl_become_member.setVisibility(View.VISIBLE);
                        }
                        else if(decision.contains("commitment")) {
                            lbl_renew_commitment.setVisibility(View.VISIBLE);
                        }
                        else if(decision.contains("life")) {
                            lbl_commit_life.setVisibility(View.VISIBLE);
                        }
                        else if(decision.contains("baptized")) {
                            lbl_be_baptized.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Details.this, "An error occurred and the app has to close.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
