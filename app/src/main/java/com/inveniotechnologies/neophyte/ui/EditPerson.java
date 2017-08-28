package com.inveniotechnologies.neophyte.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.inveniotechnologies.neophyte.R;
import com.inveniotechnologies.neophyte.network.models.Record;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditPerson extends AppCompatActivity implements View.OnClickListener {
    String date;
    String Uid;

    @BindView(R.id.txt_full_name)
    EditText txt_full_name;

    @BindView(R.id.txt_home_address)
    EditText txt_home_address;

    @BindView(R.id.txt_home_tel)
    EditText txt_home_tel;

    @BindView(R.id.txt_office_tel)
    EditText txt_office_tel;

    @BindView(R.id.txt_mobile)
    EditText txt_mobile;

    @BindView(R.id.txt_email)
    EditText txt_email;

    @BindView(R.id.txt_how_you_found_us)
    EditText txt_how_you_found_us;

    @BindView(R.id.txt_comments)
    EditText txt_comments;

    @BindView(R.id.txt_month)
    EditText txt_month;

    @BindView(R.id.txt_day)
    EditText txt_day;

    @BindView(R.id.cmb_title)
    AppCompatSpinner cmb_title;

    @BindView(R.id.cmb_age_group)
    AppCompatSpinner cmb_age_group;

    @BindView(R.id.chk_commit_life)
    AppCompatCheckBox chk_commit_life;

    @BindView(R.id.chk_renew_commitment)
    AppCompatCheckBox chk_renew_commitment;

    @BindView(R.id.chk_be_baptized)
    AppCompatCheckBox chk_be_baptized;

    @BindView(R.id.chk_talk_pastorate)
    AppCompatCheckBox chk_talk_pastorate;

    @BindView(R.id.chk_become_member)
    AppCompatCheckBox chk_become_member;

    @BindView(R.id.chk_discover_maturity)
    AppCompatCheckBox chk_discover_maturity;

    @BindView(R.id.chk_discover_ministry)
    AppCompatCheckBox chk_discover_ministry;

    @BindView(R.id.btn_update_record)
    Button btn_update_record;

    @BindView(R.id.btn_spiritual_rebirth_date)
    Button btn_select_spiritual_rebirth;

    @BindView(R.id.btn_select_save_date)
    Button btn_select_save_date;

    @BindView(R.id.mScrollView)
    ScrollView scrollViewer;

    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_person);

        ButterKnife.bind(this);

        retrieveIntentData();

        btn_select_save_date.setOnClickListener(this);
        btn_select_spiritual_rebirth.setOnClickListener(this);
        btn_update_record.setOnClickListener(this);

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
                        EditPerson.this,
                        "An error occurred and the app has to close.",
                        Toast.LENGTH_SHORT
                ).show();
                finish();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_spiritual_rebirth_date:
                changeSRB();
                break;
            case R.id.btn_select_save_date:
                changeSaveDate();
                break;
            case R.id.btn_update_record:
                updateRecord();
                break;
        }
    }

    private void writeRecordToUi(Record record) {
        if (record != null) {
            btn_select_save_date.setText(date);
            if (record.getBirthDay() != null && !record.getBirthDay().isEmpty()) {
                String[] components = record.getBirthDay().split(";");
                if (components.length == 2) {
                    txt_day.setText(components[0]);
                    txt_month.setText(components[1]);
                }
                // cater to the old date system
                else if (components.length == 1 && components[0].contains("-")) {
                    String birthday = components[0];
                    components = birthday.split("-");
                    // cater to the year first or last mode
                    if (components[0].length() == 4) {
                        txt_day.setText(components[2]);
                    } else {
                        txt_day.setText(components[0]);
                    }
                    txt_month.setText(components[1]);
                }
            }
            txt_full_name.setText(record.getFullName());
            txt_comments.setText(record.getComments());
            txt_email.setText(record.getEmail());
            txt_home_address.setText(record.getHomeAddress());
            txt_home_tel.setText(record.getHomeTel());
            txt_mobile.setText(record.getMobile());
            txt_office_tel.setText(record.getOfficeTel());
            txt_how_you_found_us.setText(record.getInvitedBy());
            if (record.getDateOfSpiritualRebirth() == null ||
                    record.getDateOfSpiritualRebirth().isEmpty()) {
                btn_select_spiritual_rebirth.setText("Select Date");
            } else {
                btn_select_spiritual_rebirth.setText(record.getDateOfSpiritualRebirth());
            }

            String[] decisions = record.getDecisions().split(";");
            for (int i = 0; i < decisions.length; i++) {
                String decision = decisions[i];
                if (decision.contains("pastorate")) {
                    chk_talk_pastorate.setChecked(true);
                } else if (decision.contains("maturity")) {
                    chk_discover_maturity.setChecked(true);
                } else if (decision.contains("ministry")) {
                    chk_discover_ministry.setChecked(true);
                } else if (decision.contains("member")) {
                    chk_become_member.setChecked(true);
                } else if (decision.contains("commitment")) {
                    chk_renew_commitment.setChecked(true);
                } else if (decision.contains("life")) {
                    chk_commit_life.setChecked(true);
                } else if (decision.contains("baptized")) {
                    chk_be_baptized.setChecked(true);
                }
            }

            writeAgeGroupToUi(record.getAgeGroup());
            writeTitleToUi(record.getTitle());
        }
    }

    private void writeAgeGroupToUi(String ageGroup) {

    }

    private void writeTitleToUi(String title) {

    }

    private void updateRecord() {
        try {
            Record record = new Record();
            record.setTitle(cmb_title.getSelectedItem().toString());
            record.setAgeGroup(cmb_age_group.getSelectedItem().toString());
            record.setBirthDay(txt_day.getText().toString() + ";" + txt_month.getText().toString());
            record.setComments(txt_comments.getText().toString());
            record.setEmail(txt_email.getText().toString());
            record.setHomeAddress(txt_home_address.getText().toString());
            record.setHomeTel(txt_home_tel.getText().toString());
            record.setInvitedBy(txt_how_you_found_us.getText().toString());
            record.setMobile(txt_mobile.getText().toString());
            record.setOfficeTel(txt_office_tel.getText().toString());
            record.setFullName(txt_full_name.getText().toString());
            if(btn_select_spiritual_rebirth.getText().toString() == "Select Date") {
                record.setDateOfSpiritualRebirth("");
            } else {
                record.setDateOfSpiritualRebirth(btn_select_spiritual_rebirth.getText().toString());
            }
            //
            String decisions = "";
            if (chk_talk_pastorate.isChecked()) {
                decisions += chk_talk_pastorate.getText().toString() + " ; ";
            }
            if (chk_become_member.isChecked()) {
                decisions += chk_become_member.getText().toString() + " ; ";
            }
            if (chk_renew_commitment.isChecked()) {
                decisions += chk_renew_commitment.getText().toString() + " ; ";
            }
            if (chk_be_baptized.isChecked()) {
                decisions += chk_be_baptized.getText().toString() + " ; ";
            }
            if (chk_commit_life.isChecked()) {
                decisions += chk_commit_life.getText().toString() + " ; ";
            }
            if (chk_discover_maturity.isChecked()) {
                decisions += chk_discover_maturity.getText().toString() + " ; ";
            }
            if (chk_discover_ministry.isChecked()) {
                decisions += chk_discover_ministry.getText().toString() + " ; ";
            }

            record.setDecisions(decisions);

            DatabaseReference membersRef = database.getReference("members");
            DatabaseReference dateRef = membersRef.child(date);
            DatabaseReference uidRef = dateRef.child(Uid);
            uidRef.removeValue();

            DatabaseReference newDateRef = membersRef.child(
                    btn_select_save_date.getText().toString()
            );
            newDateRef.push().setValue(record);
            scrollViewer.fullScroll(ScrollView.FOCUS_UP);

            Toast.makeText(this, "Record successfully updated!", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Toast.makeText(this, "An error occurred: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void changeSRB() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        btn_select_spiritual_rebirth.setText(
                                year
                                        + "-"
                                        + String.format("%02d", (monthOfYear + 1))
                                        + "-"
                                        + String.format("%02d", dayOfMonth)
                        );
                    }
                },
                year,
                month,
                day
        );
        datePickerDialog.show();
    }

    private void changeSaveDate() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        btn_select_save_date.setText(
                                year
                                        + "-"
                                        + String.format("%02d", (monthOfYear + 1))
                                        + "-"
                                        + String.format("%02d", dayOfMonth)
                        );
                    }
                },
                year,
                month,
                day
        );
        datePickerDialog.show();
    }

    private void retrieveIntentData() {
        Intent intent = getIntent();
        date = intent.getStringExtra("date");
        Uid = intent.getStringExtra("Uid");
    }
}
