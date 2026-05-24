package com.epos.backend.util;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QueryUtil {

    public static String like(String value) {
        return "%" + value.trim().toLowerCase() + "%";
    }

    public static BigDecimal bd(ResultSet rs, String column) throws SQLException{
        BigDecimal value = rs.getBigDecimal(column);
        return value == null ? BigDecimal.ZERO : value;
    }

}
