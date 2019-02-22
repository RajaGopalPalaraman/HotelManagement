package com.edot.hotelmanagement;

import java.util.Calendar;

public final class DateChooserHelper {

    public static int refYear;
    public static int refMonth;
    public static int refDay;

    public static String generateLocalDateString(int year, int month, int day)
    {
        String s = "";
        if (day < 10)
        {
            s = s+"0";
        }
        s=s+String.valueOf(day)+"-";
        if (month<10)
        {
            s = s+"0";
        }
        s=s+String.valueOf(month)+"-"+String.valueOf(year);
        return s;
    }

    public static String generateDate(int year, int month, int day)
    {
        return (validateDate(year,month,day))?
                generateLocalDateString(year,month,day)
                : generateLocalDateString(refYear,refMonth,refDay);
    }

    public static boolean validateDate(int year, int month, int day)
    {
        if (refYear > year)
        {
            return false;
        }
        else if (refYear == year && refMonth > month)
        {
            return false;
        }
        else {
            return refYear != year || refMonth != month
                    || refDay <= day;
        }

    }

    public static int[] parseDate(String date)
    {
        if (!date.matches("[0-3][0-9]-[0-1][1-9]-[0-9]{4}"))
        {
            return null;
        }

        String[] strings = date.split("-");
        int[] ints = new int[3];
        ints[0] = Integer.parseInt(strings[0]);
        if (ints[0] < 1 || ints[0] > 31)
        {
            throw new IllegalArgumentException("Date range should be between 1 and 31");
        }
        ints[1] = Integer.parseInt(strings[1]);
        if (ints[1] < 1 || ints[1] > 12)
        {
            throw new IllegalArgumentException("Month range should be between 1 and 12");
        }
        ints[2] = Integer.parseInt(strings[2]);

        return ints;
    }

}
