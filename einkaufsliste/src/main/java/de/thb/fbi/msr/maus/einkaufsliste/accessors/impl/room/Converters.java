package de.thb.fbi.msr.maus.einkaufsliste.accessors.impl.room;

import android.widget.DatePicker;
import androidx.room.TypeConverter;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

public class Converters {

    @TypeConverter
    public String fromUrlToString(URL aUrl) {
        return aUrl == null ? "" : aUrl.toExternalForm();
    }

    @TypeConverter
    public URL fromStringToURL(String aUrlString) {
        URL ret = null;
        if (aUrlString == "") {
            return null;
        }
        try {
            ret = new URL(aUrlString);
        } catch(MalformedURLException e) {
            //ignore, cant't happen, right?
        }

        return ret;
    }

}
