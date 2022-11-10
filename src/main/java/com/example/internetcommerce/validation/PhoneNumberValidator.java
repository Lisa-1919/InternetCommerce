package com.example.internetcommerce.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneNumberValidator {

    private final static String PHONE_NUMBER_REGEX = "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{2}[- .]?\\d{2}$";

    private static Pattern pattern;

    // non-static Matcher object because it's created from the input String
    private Matcher matcher;


    public PhoneNumberValidator() {
        // initialize the Pattern object
        pattern = Pattern.compile(PHONE_NUMBER_REGEX, Pattern.CASE_INSENSITIVE);
    }

    //This method validates the input email address with EMAIL_REGEX pattern
    public boolean validatePhone(String email) {
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
