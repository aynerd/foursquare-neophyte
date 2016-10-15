package com.inveniotechnologies.neophyte;

import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.inveniotechnologies.neophyte.Models.Record;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NewRecord extends AppCompatActivity implements View.OnClickListener {
    private EditText txt_dob;
    private EditText txt_full_name;
    private EditText txt_home_address;
    private EditText txt_home_tel;
    private EditText txt_office_tel;
    private EditText txt_mobile;
    private EditText txt_email;
    private EditText txt_invited_by;
    private EditText txt_comments;
    private EditText txt_spiritual_rebirth_date;
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
    private Button btn_save_record;
    //
    private ScrollView scrollViewer;
    //
    private SimpleDateFormat dateFormatter;
    //
    private FirebaseDatabase database;
    //
    private DatePickerDialog dobPickerDialog;
    private DatePickerDialog dsrPickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_record);
        //
        txt_full_name = (EditText) findViewById(R.id.txt_full_name);
        txt_home_address = (EditText) findViewById(R.id.txt_home_address);
        txt_home_tel = (EditText) findViewById(R.id.txt_home_tel);
        txt_office_tel = (EditText) findViewById(R.id.txt_office_tel);
        txt_mobile = (EditText) findViewById(R.id.txt_mobile);
        txt_email = (EditText) findViewById(R.id.txt_email);
        txt_invited_by = (EditText) findViewById(R.id.txt_invited_by);
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
        btn_save_record = (Button) findViewById(R.id.btn_save_record);
        btn_save_record.setOnClickListener(this);
        //
        scrollViewer = (ScrollView) findViewById(R.id.mScrollView);
        //
        database= FirebaseDatabase.getInstance();
        //
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        //
        txt_dob = (EditText) findViewById(R.id.txt_birthday);
        txt_dob.setInputType(InputType.TYPE_NULL);
        txt_dob.setOnClickListener(this);
        //
        Calendar calendar = Calendar.getInstance();
        dobPickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Calendar date = Calendar.getInstance();
                date.set(year, month, day);
                txt_dob.setText(dateFormatter.format(date.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        //
        dsrPickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Calendar date = Calendar.getInstance();
                date.set(year, month, day);
                txt_spiritual_rebirth_date.setText(dateFormatter.format(date.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        //
        txt_dob.addTextChangedListener(new TextWatcher() {
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
        txt_spiritual_rebirth_date = (EditText) findViewById(R.id.txt_spiritual_rebirth_date);
        txt_spiritual_rebirth_date.setInputType(InputType.TYPE_NULL);
        txt_spiritual_rebirth_date.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_birthday :
                dobPickerDialog.show();
                break;
            case R.id.txt_spiritual_rebirth_date:
                dsrPickerDialog.show();
                break;
            case R.id.btn_save_record:
                saveRecord();
                break;
        }
    }

    private void saveRecord() {
        try {
            Record record = new Record();
            record.setTitle(cmb_title.getSelectedItem().toString());
            record.setAgeGroup(cmb_age_group.getSelectedItem().toString());
            record.setBirthDay(txt_dob.getText().toString());
            record.setComments(txt_comments.getText().toString());
            record.setEmail(txt_email.getText().toString());
            record.setHomeAddress(txt_home_address.getText().toString());
            record.setHomeTel(txt_home_tel.getText().toString());
            record.setInvitedBy(txt_invited_by.getText().toString());
            record.setMobile(txt_mobile.getText().toString());
            record.setOfficeTel(txt_office_tel.getText().toString());
            record.setFullName(txt_full_name.getText().toString());
            record.setDateOfSpiritualRebirth(txt_spiritual_rebirth_date.getText().toString());
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
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = formatter.format(date);
            //
            DatabaseReference membersRef = database.getReference("members");
            DatabaseReference dateRef = membersRef.child(currentDate);
            dateRef.push().setValue(record);
            //
            DatabaseReference membersBackupRef = database.getReference("members_backup");
            DatabaseReference dateBackupRef = membersBackupRef.child(currentDate);
            dateBackupRef.push().setValue(record);
            //
            //Clear the input boxes
            txt_comments.setText("");
            txt_dob.setText("");
            txt_invited_by.setText("");
            txt_email.setText("");
            txt_full_name.setText("");
            txt_home_address.setText("");
            txt_home_tel.setText("");
            txt_mobile.setText("");
            txt_office_tel.setText("");
            txt_spiritual_rebirth_date.setText("");
            //
            chk_renew_commitment.setChecked(false);
            chk_become_member.setChecked(false);
            chk_commit_life.setChecked(false);
            chk_discover_ministry.setChecked(false);
            chk_discover_maturity.setChecked(false);
            chk_talk_pastorate.setChecked(false);
            chk_be_baptized.setChecked(false);
            //
            scrollViewer.fullScroll(ScrollView.FOCUS_UP);
            //
            Toast.makeText(this, "Record successfully saved!", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Toast.makeText(this, "An error occurred: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
