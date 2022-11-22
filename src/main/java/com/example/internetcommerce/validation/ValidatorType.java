package com.example.internetcommerce.validation;

public enum ValidatorType {
    PHONE_NUMBER("^(\\+\\d{1,3}( )?)?((\\(\\d{2}\\))|\\d{2})[- .]?\\d{3}[- .]?\\d{2}[- .]?\\d{2}$"),
    EMAIL("^[\\w-+]+(\\.\\w+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$");

    String validatorValue;
    ValidatorType(String validatorValue) {
        this.validatorValue = validatorValue;
    }

    public String getValidatorValue() {
        return validatorValue;
    }

    public void setValidatorValue(String validatorValue) {
        this.validatorValue = validatorValue;
    }
}
