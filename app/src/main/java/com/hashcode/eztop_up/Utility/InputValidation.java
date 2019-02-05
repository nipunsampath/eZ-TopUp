package com.hashcode.eztop_up.Utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputValidation
{
    public static Boolean validateUSSD(String ussd)
    {
        return Pattern.matches("^[0-9*#]+$", ussd);
    }

    public static Boolean validateRechargeCode(String code)
    {
        return Pattern.matches("^[0-9 \\s]+$", code);
    }
    public static Boolean validateDigit(String code)
    {
        return Pattern.matches("^[0-9]+$", code);
    }

    public static String getNumbers(String string)
    {
       Pattern pattern = Pattern.compile("\\d+");
       Matcher matcher = pattern.matcher(string);
       StringBuilder builder = new StringBuilder();
        int i = 0;
        while (matcher.find()) {
            for (int j = 0; j <= matcher.groupCount(); j++) {

                builder.append(matcher.group(j));
                i++;
            }
        }
        return builder.toString();
    }

}
