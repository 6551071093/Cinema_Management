package com.cinema.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");
     // Chuyển đổi Date thành chuỗi dd/MM/yyyy (Dùng cho bảng phim)

    public static String formatDate(Date date) {
        if (date == null) return "";
        return DATE_FORMAT.format(date);
    }


    //Chuyển đổi Date thành chuỗi dd/MM/yyyy HH:mm
   
    public static String formatDateTime(Date date) {
        if (date == null) return "";
        return DATETIME_FORMAT.format(date);
    }


    //Parse chuỗi dd/MM/yyyy sang java.sql.Date để lưu DB

    public static java.sql.Date parseSqlDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        try {
            Date parsed = DATE_FORMAT.parse(dateStr);
            return new java.sql.Date(parsed.getTime());
        } catch (ParseException e) {
            return null;
        }
    }


     //Hàm hỗ trợ parse cho MovieDialog

    public static Date parseDate(String dateStr) throws Exception {
        return DATE_FORMAT.parse(dateStr);
    }
}
