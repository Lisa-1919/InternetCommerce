package com.example.internetcommerce.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {
    private static Pattern pattern;

    // non-static Matcher object because it's created from the input String
    private Matcher matcher;

    public Validator() {
    }

    //This method validates the input email address with EMAIL_REGEX pattern
    public boolean validate(String str, ValidatorType type) {
        pattern = Pattern.compile(String.valueOf(type), Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(str);
        return matcher.matches();
    }

}
