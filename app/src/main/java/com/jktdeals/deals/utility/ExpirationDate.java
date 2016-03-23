package com.jktdeals.deals.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpirationDate {

    public static String formatExpirationDate(String expirationDate) {
        Pattern p = Pattern.compile("^\\d\\d/\\d\\d/\\d\\d$");
        Matcher m = p.matcher(expirationDate);
        if (m.matches()) {
            // if the expiration date uses the yy/mm/dd format from Jose's
            // CreatDealActivity, parse it
            Date date;
            long dateMillis = 0;
            date = new Date(dateMillis);
            String yymmddFormat = "yy/MM/dd";
            SimpleDateFormat sf = new SimpleDateFormat(yymmddFormat, Locale.ENGLISH);
            sf.setLenient(true);

            try {
                dateMillis = sf.parse(expirationDate).getTime();
                date = new Date(dateMillis);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            String moreFriendlyDateFormat = "MMM d, yyyy";
            SimpleDateFormat mfdf = new SimpleDateFormat(moreFriendlyDateFormat, Locale.ENGLISH);
            mfdf.setLenient(true);

            return mfdf.format(date);
        } else {
            // otherwise just put whatever value is there
            return expirationDate;
        }

    }
}
