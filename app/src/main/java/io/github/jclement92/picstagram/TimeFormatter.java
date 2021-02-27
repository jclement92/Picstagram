package io.github.jclement92.picstagram;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Given a date String of the format given by the Back4App backend, returns a display-formatted
 * String representing the relative time difference,
 * e.g. "2 minutes ago", "6 days ago", "May 23", "January 1, 2014"
 * depending on how great the time difference between now and the given date is.
 */
public class TimeFormatter {
    public static String getTimeDifference(String rawJsonDate) {
        String time = "";
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat format = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        format.setLenient(true);
        try {
            long diff = (System.currentTimeMillis() - format.parse(rawJsonDate).getTime()) / 1000;
            if (diff < 5)
                time = "Just now";
            else if (diff < 60)
                time = String.format(Locale.ENGLISH, "%d seconds ago", diff);
            else if (diff < 60 * 60)
                time = String.format(Locale.ENGLISH, "%d minutes ago", diff / 60);
            else if (diff < 60 * 60 * 24)
                time = String.format(Locale.ENGLISH, "%d hours ago", diff / (60 * 60));
            else if (diff < 60 * 60 * 24 * 8)
                time = String.format(Locale.ENGLISH, "%d days ago", diff / (60 * 60 * 24));
            else {
                Calendar now = Calendar.getInstance();
                Calendar then = Calendar.getInstance();
                then.setTime(format.parse(rawJsonDate));
                if (now.get(Calendar.YEAR) == then.get(Calendar.YEAR)) {
                    time = then.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US) + " "
                            + then.get(Calendar.DAY_OF_MONTH);
                } else {
                    time = then.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US) + " "
                            + then.get(Calendar.DAY_OF_MONTH)
                            + ", " + (then.get(Calendar.YEAR));
                }
            }
        }  catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }
}

