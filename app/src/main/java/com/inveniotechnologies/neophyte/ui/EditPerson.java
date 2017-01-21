package com.inveniotechnologies.neophyte.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.TextWatcher;
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

public class EditPerson extends AppCompatActivity implements View.OnClickListener {
    //
    String date;
    String Uid;
    private EditText txt_full_name;
    private EditText txt_home_address;
    private EditText txt_home_tel;
    private EditText txt_office_tel;
    private EditText txt_mobile;
    private EditText txt_email;
    private EditText txt_how_you_found_us;
    private EditText txt_comments;
    //
    private AppCompatSpinner cmb_title;
    private AppCompatSpinner cmb_age_group;
    //
    private AppCompatCheckBox chk_commit_life;
    private AppCompatCheckBox chk_renew_commitment;
    private AppCompatCheckBox chk_be_baptized;
    private AppCompatCheckBox chk_talk_pastorate;
    private AppCompatCheckBox chk_become_member;
    private AppCompatCheckBox chk_discover_maturity;
    private AppCompatCheckBox chk_discover_ministry;
    //
    private Button btn_update_record;
    private Button btn_select_spiritual_rebirth;
    private Button btn_select_dob;
    private Button btn_select_save_date;
    //
    private ScrollView scrollViewer;
    //
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_person);
        //
        Intent intent = getIntent();
        date = intent.getStringExtra("date");
        Uid = intent.getStringExtra("Uid");
        //
        txt_full_name = (EditText) findViewById(R.id.txt_full_name);
        txt_home_address = (EditText) findViewById(R.id.txt_home_address);
        txt_home_tel = (EditText) findViewById(R.id.txt_home_tel);
        txt_office_tel = (EditText) findViewById(R.id.txt_office_tel);
        txt_mobile = (EditText) findViewById(R.id.txt_mobile);
        txt_email = (EditText) findViewById(R.id.txt_email);
        txt_how_you_found_us = (EditText) findViewById(R.id.txt_how_you_found_us);
        txt_comments = (EditText) findViewById(R.id.txt_comments);
        //
        cmb_age_group = (AppCompatSpinner) findViewById(R.id.cmb_age_group);
        cmb_title = (AppCompatSpinner) findViewById(R.id.cmb_title);
        //
        chk_be_baptized = (AppCompatCheckBox) findViewById(R.id.chk_be_baptized);
        chk_become_member = (AppCompatCheckBox) findViewById(R.id.chk_become_member);
        chk_commit_life = (AppCompatCheckBox) findViewById(R.id.chk_commit_life);
        chk_discover_maturity = (AppCompatCheckBox) findViewById(R.id.chk_discover_maturity);
        chk_discover_ministry = (AppCompatCheckBox) findViewById(R.id.chk_discover_ministry);
        chk_renew_commitment = (AppCompatCheckBox) findViewById(R.id.chk_renew_commitment);
        chk_talk_pastorate = (AppCompatCheckBox) findViewById(R.id.chk_talk_pastorate);
        //
        btn_select_dob = (Button) findViewById(R.id.btn_select_dob);
        btn_select_dob.setOnClickListener(this);
        btn_select_save_date = (Button) findViewById(R.id.btn_select_save_date);
        btn_select_save_date.setOnClickListener(this);
        btn_select_spiritual_rebirth = (Button) findViewById(R.id.btn_spiritual_rebirth_date);
        btn_select_spiritual_rebirth.setOnClickListener(this);
        btn_update_record = (Button) findViewById(R.id.btn_update_record);
        btn_update_record.setOnClickListener(this);
        //
        scrollViewer = (ScrollView) findViewById(R.id.mScrollView);
        //
        btn_select_dob.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 10) {
                    String yearString = s.toString().substring(0, 4);
                    int year = Integer.parseInt(yearString);
                    int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                    int age = currentYear - year;
                    //
                    if(age > 60) {
                        cmb_age_group.setSelection(8, true);
                    }
                    else if(age > 50 && age < 61) {
                        cmb_age_group.setSelection(7, true);
                    }
                    else if(age > 45 && age < 51) {
                        cmb_age_group.setSelection(6, true);
                    }
                    else if(age > 40 && age < 46) {
                        cmb_age_group.setSelection(5, true);
                    }
                    else if(age > 35 && age < 41) {
                        cmb_age_group.setSelection(4, true);
                    }
                    else if(age > 30 && age < 36) {
                        cmb_age_group.setSelection(3, true);
                    }
                    else if(age > 25 && age < 31) {
                        cmb_age_group.setSelection(2, true);
                    }
                    else if(age > 17 && age < 26) {
                        cmb_age_group.setSelection(1, true);
                    }
                    else {
                        cmb_age_group.setSelection(0, true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
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
                    btn_select_save_date.setText(date);
                    if(record.getBirthDay() == null || record.getBirthDay().isEmpty()) {
                        btn_select_dob.setText(record.getBirthDay());
                    } else {
                        btn_select_dob.setText("Select Date");
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
                    //
                    String[] decisions = record.getDecisions().split(";");
                    for(int i = 0; i < decisions.length; i++) {
                        String decision = decisions[i];
                        if(decision.contains("pastorate")) {
                            chk_talk_pastorate.setChecked(true);
                        }
                        else if(decision.contains("maturity")) {
                            chk_discover_maturity.setChecked(true);
                        }
                        else if(decision.contains("ministry")) {
                            chk_discover_ministry.setChecked(true);
                        }
                        else if(decision.contains("member")) {
                            chk_become_member.setChecked(true);
                        }
                        else if(decision.contains("commitment")) {
                            chk_renew_commitment.setChecked(true);
                        }
                        else if(decision.contains("life")) {
                            chk_commit_life.setChecked(true);
                        }
                        else if(decision.contains("baptized")) {
                            chk_be_baptized.setChecked(true);
                        }
                    }
                }
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
            case R.id.btn_select_dob:
                changeDOB();
                break;
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

    private void updateRecord() {
        try {
            Record record = new Record();
            record.setTitle(cmb_title.getSelectedItem().toString());
            record.setAgeGroup(cmb_age_group.getSelectedItem().toString());
            if(btn_select_dob.getText().toString() == "Select Date") {
                record.setBirthDay("");
            } else  {
                record.setBirthDay(btn_select_dob.getText().toString());
            }
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
            //
            record.setDecisions(decisions);
            //
            DatabaseReference membersRef = database.getReference("members");
            DatabaseReference dateRef = membersRef.child(date);
            DatabaseReference uidRef = dateRef.child(Uid);
            uidRef.removeValue();
            //
            DatabaseReference newDateRef = membersRef.child(
                    btn_select_save_date.getText().toString()
            );
            newDateRef.push().setValue(record);
            scrollViewer.fullScroll(ScrollView.FOCUS_UP);
            //
            Toast.makeText(this, "Record successfully updated!", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Toast.makeText(this, "An error occurred: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void changeDOB() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        btn_select_dob.setText(
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
}
