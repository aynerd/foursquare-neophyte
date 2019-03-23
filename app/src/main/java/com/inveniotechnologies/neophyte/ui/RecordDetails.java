package com.inveniotechnologies.neophyte.ui;

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
import com.inveniotechnologies.neophyte.R;
import com.inveniotechnologies.neophyte.network.models.Record;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecordDetails extends AppCompatActivity {
    @BindView(R.id.lbl_title)
    TextView lbl_title;

    @BindView(R.id.lbl_full_name)
    TextView lbl_full_name;

    @BindView(R.id.lbl_age_group)
    TextView lbl_age_group;

    @BindView(R.id.lbl_comments)
    TextView lbl_comments;

    @BindView(R.id.lbl_home_address)
    TextView lbl_home_address;

    @BindView(R.id.lbl_home_tel)
    TextView lbl_home_tel;

    @BindView(R.id.lbl_mobile)
    TextView lbl_mobile;

    @BindView(R.id.lbl_office_tel)
    TextView lbl_office_tel;

    @BindView(R.id.lbl_email_address)
    TextView lbl_email;

    @BindView(R.id.lbl_how_you_found_us)
    TextView lbl_how_you_found_us;

    @BindView(R.id.lbl_birthday)
    TextView lbl_birthday;

    @BindView(R.id.lbl_spiritual_rebirth_date)
    TextView lbl_spiritual_rebirth_date;

    @BindView(R.id.lbl_commit_life)
    TextView lbl_commit_life;

    @BindView(R.id.lbl_renew_commitment)
    TextView lbl_renew_commitment;

    @BindView(R.id.lbl_be_baptized)
    TextView lbl_be_baptized;

    @BindView(R.id.lbl_talk_pastorate)
    TextView lbl_talk_pastorate;

    @BindView(R.id.lbl_become_member)
    TextView lbl_become_member;

    @BindView(R.id.lbl_discover_maturity)
    TextView lbl_discover_maturity;

    @BindView(R.id.lbl_discover_ministry)
    TextView lbl_discover_ministry;

    @BindView(R.id.btn_close)
    Button btn_close;
    //
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // bind UI elements using butter knife
        ButterKnife.bind(this);

        // get the date and user id from the intent
        Intent intent = getIntent();
        String date = intent.getStringExtra("date");
        String Uid = intent.getStringExtra("Uid");

        // set the close button listener
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // pull data from the database
        database = FirebaseDatabase.getInstance();
        DatabaseReference membersRef = database.getReference("members");
        DatabaseReference dateRef = membersRef.child(date);
        DatabaseReference idRef = dateRef.child(Uid);
        idRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Record record = dataSnapshot.getValue(Record.class);
                writeRecordToUi(record);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(
                        RecordDetails.this,
                        "An error occurred and the details could not be retrieved."
                                + "This activity has to close.",
                        Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void writeRecordToUi(Record record) {
        if (record != null) {
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
            lbl_how_you_found_us.setText(record.getInvitedBy());
            lbl_spiritual_rebirth_date.setText(record.getDateOfSpiritualRebirth());
            //
            String[] decisions = record.getDecisions().split(";");
            for (String decision : decisions) {
                if (decision.contains("pastorate")) {
                    lbl_talk_pastorate.setVisibility(View.VISIBLE);
                } else if (decision.contains("maturity")) {
                    lbl_discover_maturity.setVisibility(View.VISIBLE);
                } else if (decision.contains("ministry")) {
                    lbl_discover_ministry.setVisibility(View.VISIBLE);
                } else if (decision.contains("member")) {
                    lbl_become_member.setVisibility(View.VISIBLE);
                } else if (decision.contains("commitment")) {
                    lbl_renew_commitment.setVisibility(View.VISIBLE);
                } else if (decision.contains("life")) {
                    lbl_commit_life.setVisibility(View.VISIBLE);
                } else if (decision.contains("baptized")) {
                    lbl_be_baptized.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}
