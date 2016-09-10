package com.inveniotechnologies.neophyte.ListItems;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by bolorundurowb on 23-Aug-16.
 */
public class DateListItem {
    private String Date;
    private String DayRepresentation;

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
        //
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date result = df.parse(date);
            Format formatter = new SimpleDateFormat("EEE, MMM dd");
            DayRepresentation = formatter.format(result);
        }
        catch (ParseException ex) {
            DayRepresentation = "";
        }
    }

    public  String getDayRepresentation() {
        return DayRepresentation;
    }
}
