package com.example.Modulo.global.enums;

public enum CareerYear {
    NEWCOMER("신입"),
    YEAR_1("1년차"), YEAR_2("2년차"), YEAR_3("3년차"), YEAR_4("4년차"), YEAR_5("5년차"),
    YEAR_6("6년차"), YEAR_7("7년차"), YEAR_8("8년차"), YEAR_9("9년차"), YEAR_10("10년차"),
    YEAR_11("11년차"), YEAR_12("12년차"), YEAR_13("13년차"), YEAR_14("14년차"), YEAR_15("15년차"),
    YEAR_16("16년차"), YEAR_17("17년차"), YEAR_18("18년차"), YEAR_19("19년차"),
    YEAR_20_PLUS("20년차 이상");

    private final String description;

    CareerYear(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}