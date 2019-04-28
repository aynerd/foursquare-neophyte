package com.inveniotechnologies.neophyte.ui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.inveniotechnologies.neophyte.R;
import com.inveniotechnologies.neophyte.network.models.Record;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddRecord extends AppCompatActivity implements View.OnClickListener {
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

    @BindView(R.id.cmb_month)
    AppCompatSpinner cmb_month;

    @BindView(R.id.cmb_day)
    AppCompatSpinner cmb_day;

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

    @BindView(R.id.btn_save_record)
    Button btn_save_record;

    @BindView(R.id.mScrollView)
    ScrollView scrollViewer;

    private FirebaseDatabase database;

    public static String toTitleCase(String s) {
        final String DELIMITERS = " '-/";

        StringBuilder sb = new StringBuilder();
        boolean capNext = true;

        for (char c : s.toCharArray()) {
            c = (capNext)
                    ? Character.toUpperCase(c)
                    : Character.toLowerCase(c);
            sb.append(c);
            capNext = (DELIMITERS.indexOf((int) c) >= 0);
        }
        return sb.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_record);

        ButterKnife.bind(this);
        btn_save_record.setOnClickListener(this);
        database = FirebaseDatabase.getInstance();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
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
            record.setBirthDay(cmb_month.getSelectedItem().toString() + " " + cmb_day.getSelectedItem().toString());
            record.setComments(txt_comments.getText().toString());
            record.setEmail(txt_email.getText().toString());
            record.setHomeAddress(txt_home_address.getText().toString());
            record.setHomeTel(txt_home_tel.getText().toString());
            record.setInvitedBy(txt_how_you_found_us.getText().toString());
            record.setMobile(txt_mobile.getText().toString());
            record.setOfficeTel(txt_office_tel.getText().toString());
            record.setFullName(toTitleCase(txt_full_name.getText().toString()));

            String decisions = getDecisions();

            record.setDecisions(decisions);

            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = formatter.format(date);

            DatabaseReference membersRef = database.getReference("members");
            DatabaseReference dateRef = membersRef.child(currentDate);
            dateRef.push().setValue(record);

            DatabaseReference membersBackupRef = database.getReference("members_backup");
            DatabaseReference dateBackupRef = membersBackupRef.child(currentDate);
            dateBackupRef.push().setValue(record);

            clearInputs();

            Toast.makeText(this, "Record successfully saved!", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Toast.makeText(this, "An error occurred: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private String getDecisions() {
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
        return decisions;
    }

    private void clearInputs() {
        txt_comments.setText("");
        txt_how_you_found_us.setText("");
        txt_email.setText("");
        txt_full_name.setText("");
        txt_home_address.setText("");
        txt_home_tel.setText("");
        txt_mobile.setText("");
        txt_office_tel.setText("");

        cmb_age_group.setSelection(0);
        cmb_title.setSelection(0);
        cmb_day.setSelection(0);
        cmb_month.setSelection(0);

        chk_renew_commitment.setChecked(false);
        chk_become_member.setChecked(false);
        chk_commit_life.setChecked(false);
        chk_discover_ministry.setChecked(false);
        chk_discover_maturity.setChecked(false);
        chk_talk_pastorate.setChecked(false);
        chk_be_baptized.setChecked(false);

        scrollViewer.fullScroll(ScrollView.FOCUS_UP);
    }

    @Override
    public void onBackPressed() {
        if (!TextUtils.isEmpty(txt_full_name.getText().toString()) || !TextUtils.isEmpty(txt_home_address.getText().toString()) || !TextUtils.isEmpty(txt_email.getText().toString())) {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setMessage("You appear to have unsaved changes. Do you want to close this page?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AddRecord.super.onBackPressed();
                        }
                    })
                    .setNegativeButton("No", null)
                    .create();
            alertDialog.show();
        } else {
            super.onBackPressed();
        }
    }
}
