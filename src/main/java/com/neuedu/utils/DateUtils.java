package com.neuedu.utils;

import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

public class DateUtils {
    private static final String STANDARD_FORMAT="yyyy-MM-dd HH:mm:ss";
    /**
     * Date--string
     */
    public static String dateToStr(Date date,String formate){
        DateTime dateTime=new DateTime(date);
        return dateTime.toString(formate);
    }
    public static String dateToStr(Date date){
        DateTime dateTime=new DateTime(date);
        return dateTime.toString(STANDARD_FORMAT);
    }
    /**
     * string-->Date
     */

    public static Date strToDate(String str){
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(STANDARD_FORMAT);
        DateTime dateTime = dateTimeFormatter.parseDateTime(str);
        return dateTime.toDate();
    }

    public static Date strToDate(String str,String formate){
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(formate);
        DateTime dateTime = dateTimeFormatter.parseDateTime(str);
        return dateTime.toDate();
    }


}
