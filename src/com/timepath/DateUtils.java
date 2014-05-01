package com.timepath;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author TimePath
 */
public class DateUtils {

    /**
     *
     * @param dateStr
     *
     * @return null if parsing failed
     */
    public static String parse(String dateStr) {
        String str = null;
        Calendar cal;
        try {
            cal = DatatypeConverter.parseDateTime(dateStr);
            str = cal.getTime().toString();
        } catch(java.lang.IllegalArgumentException ex) {
        }
        return str;
    }

    /**
     *
     * @param time in seconds since the epoch
     *
     * @return null if parsing failed
     */
    public static String parse(long time) {
        DateFormat df = new SimpleDateFormat("EEE dd MMM yyyy, hh:mm:ss a z");
//        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        String str = df.format(new Date(time * 1000));
        return str;
    }

    public static String timePeriod(long diffInSeconds) {
        StringBuilder sb = new StringBuilder();

        long sec = (diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds);
        long min = (diffInSeconds = (diffInSeconds / 60)) >= 60 ? diffInSeconds % 60 : diffInSeconds;
        long hrs = (diffInSeconds = (diffInSeconds / 60)) >= 24 ? diffInSeconds % 24 : diffInSeconds;
        long days = (diffInSeconds = (diffInSeconds / 24)) >= 30 ? diffInSeconds % 30 : diffInSeconds;
        //<editor-fold defaultstate="collapsed" desc="approximate months/years">
        //                long months = (diffInSeconds = (diffInSeconds / 30)) >= 12 ? diffInSeconds % 12 : diffInSeconds;
        //                long years = (diffInSeconds = (diffInSeconds / 12));

        //                if(years > 0) {
        //                    if(years == 1) {
        //                        sb.append("a year");
        //                    } else {
        //                        sb.append(years + " years");
        //                    }
        //                    if(years <= 6 && months > 0) {
        //                        if(months == 1) {
        //                            sb.append(" and a month");
        //                        } else {
        //                            sb.append(" and " + months + " months");
        //                        }
        //                    }
        //                } else if(months > 0) {
        //                    if(months == 1) {
        //                        sb.append("a month");
        //                    } else {
        //                        sb.append(months + " months");
        //                    }
        //                    if(months <= 6 && days > 0) {
        //                        if(days == 1) {
        //                            sb.append(" and a day");
        //                        } else {
        //                            sb.append(" and " + days + " days");
        //                        }
        //                    }
        //                } else 
        //</editor-fold>
        if(days > 0) {
            if(days == 1) {
                sb.append("a day");
            } else {
                sb.append(days).append(" days");
            }
            if(days <= 3 && hrs > 0) {
                if(hrs == 1) {
                    sb.append(" and an hour");
                } else {
                    sb.append(" and ").append(hrs).append(" hours");
                }
            }
        } else if(hrs > 0) {
            if(hrs == 1) {
                sb.append("an hour");
            } else {
                sb.append(hrs).append(" hours");
            }
            if(min > 1) {
                sb.append(" and ").append(min).append(" minutes");
            }
        } else if(min > 0) {
            if(min == 1) {
                sb.append("a minute");
            } else {
                sb.append(min).append(" minutes");
            }
            if(sec > 1) {
                sb.append(" and ").append(sec).append(" seconds");
            }
        } else {
            if(sec <= 1) {
                sb.append("about a second");
            } else {
                sb.append("about ").append(sec).append(" seconds");
            }
        }

        sb.append(" ago");

        return sb.toString();
    }

    private DateUtils() {
    }

    private static final Logger LOG = Logger.getLogger(DateUtils.class.getName());

}
