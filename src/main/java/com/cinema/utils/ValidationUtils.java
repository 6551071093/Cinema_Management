package com.cinema.utils;

import java.util.regex.Pattern;

public class ValidationUtils {

    // Regex Email: Phải chứa ký tự @ và đuôi .com 
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.(com|net|org|vn)$";
    
    /**
     * Kiểm tra Email theo định dạng chuẩn
     */
    public static String validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return "Vui lòng nhập Email!";
        }
        if (!Pattern.compile(EMAIL_REGEX).matcher(email).matches()) {
            return "Định dạng Email không hợp lệ (phải có @ và đuôi .com)!"; 
        }
        return null;
    }

    /**
     * Kiểm tra Mật khẩu theo tiêu chuẩn bảo mật nghiêm ngặt (Strict Validation)
     * Các tiêu chuẩn: 8-20 ký tự, hoa, thường, số, ký tự đặc biệt, không khoảng trắng 
     */
    public static String validateStrictPassword(String password, String confirmPassword) {
        if (password == null || password.isEmpty()) {
            return "Mật khẩu không được để trống!";
        }
        
        // 1. Kiểm tra độ dài: 8 đến 20 ký tự 
        if (password.length() < 8 || password.length() > 20) {
            return "Mật khẩu phải từ 8 đến 20 ký tự!";
        }

        // 2. Kiểm tra khoảng trắng: Tuyệt đối không chứa dấu cách
        if (password.contains(" ")) {
            return "Mật khẩu không được chứa khoảng trắng!";
        }

        // 3. Kiểm tra ký tự thường, hoa, số và đặc biệt 
        boolean hasLower = Pattern.compile("[a-z]").matcher(password).find();
        boolean hasUpper = Pattern.compile("[A-Z]").matcher(password).find();
        boolean hasDigit = Pattern.compile("[0-9]").matcher(password).find();
        boolean hasSpecial = Pattern.compile("[!@#$%^&*(),.?\":{}|<>]").matcher(password).find();

        if (!hasLower || !hasUpper || !hasDigit || !hasSpecial) {
            return "Mật khẩu phải bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt!";
        }

        // 4. Kiểm tra sự đồng nhất (Matching) 
        if (confirmPassword != null && !password.equals(confirmPassword)) {
            return "Mật khẩu xác nhận không khớp!";
        }

        return null; 
    }
}
