package com.hashcode.eztop_up.Utility;

import java.util.regex.Pattern;

public class InputValidation
{
    public static Boolean validateUSSD(String ussd)
    {
        return Pattern.matches("^[0-9*#]+$", ussd);
    }
}
