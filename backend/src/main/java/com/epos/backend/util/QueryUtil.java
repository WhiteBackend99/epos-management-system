package com.epos.backend.util;

public class QueryUtil {

    public static String like(String value) {
        return "%" + value.trim().toLowerCase() + "%";
    }

}
