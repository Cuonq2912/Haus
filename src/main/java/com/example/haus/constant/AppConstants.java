package com.example.haus.constant;

public class AppConstants {
    private AppConstants() {
    }

    public static final String SORT_BY = "(\\w+?)(:)(.*)";

    public static final String STR_FORMAT = "%%%s%%";

    public static final String SEARCH_OPERATOR = "(\\w+?)(:|<|>)(.*)";

    public static final String SPEC_SEARCH_OPERATOR = "(\\w+?)([<:>~!])(.*)(\\p{Punct}?)(\\p{Punct}?)";

    public static final String FORMAT_DIGIT_PDF = "%,.0f";
}
