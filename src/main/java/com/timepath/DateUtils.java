package com.timepath;

import javax.xml.bind.DatatypeConverter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

/**
 * @author TimePath
 */
public class DateUtils {

    private static final Logger LOG = Logger.getLogger(DateUtils.class.getName());

    private DateUtils() {
    }

    /**
     * @param dateStr
     * @return null if parsing failed
     */
    public static String parse(String dateStr) {
        String str = null;
        try {
            Calendar cal = DatatypeConverter.parseDateTime(dateStr);
            str = cal.getTime().toString();
        } catch (IllegalArgumentException ignored) {
        }
        return str;
    }

    /**
     * @param time in seconds since the epoch
     * @return null if parsing failed
     */
    public static String parse(long time) {
        DateFormat df = new SimpleDateFormat("EEE dd MMM yyyy, hh:mm:ss a z");
        //        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        return df.format(new Date(time * 1000));
    }

    public static String timePeriod(long diffInSeconds) {
        StringBuilder sb = new StringBuilder();
        long sec = (diffInSeconds >= 60) ? (diffInSeconds % 60) : diffInSeconds;
        long min = ((diffInSeconds /= 60) >= 60) ? (diffInSeconds % 60) : diffInSeconds;
        long hrs = ((diffInSeconds /= 60) >= 24) ? (diffInSeconds % 24) : diffInSeconds;
        long days = ((diffInSeconds /= 24) >= 30) ? (diffInSeconds % 30) : diffInSeconds;
        if (days > 0) {
            if (days == 1) {
                sb.append("a day");
            } else {
                sb.append(days).append(" days");
            }
            if ((days <= 3) && (hrs > 0)) {
                if (hrs == 1) {
                    sb.append(" and an hour");
                } else {
                    sb.append(" and ").append(hrs).append(" hours");
                }
            }
        } else if (hrs > 0) {
            if (hrs == 1) {
                sb.append("an hour");
            } else {
                sb.append(hrs).append(" hours");
            }
            if (min > 1) {
                sb.append(" and ").append(min).append(" minutes");
            }
        } else if (min > 0) {
            if (min == 1) {
                sb.append("a minute");
            } else {
                sb.append(min).append(" minutes");
            }
            if (sec > 1) {
                sb.append(" and ").append(sec).append(" seconds");
            }
        } else {
            if (sec <= 1) {
                sb.append("about a second");
            } else {
                sb.append("about ").append(sec).append(" seconds");
            }
        }
        sb.append(" ago");
        return sb.toString();
    }
}
