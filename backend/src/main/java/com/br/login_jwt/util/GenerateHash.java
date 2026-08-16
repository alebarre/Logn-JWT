package com.br.login_jwt.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerateHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        System.out.println("admin@admin.com => " + encoder.encode("admin123"));
        System.out.println("user@user.com => " + encoder.encode("user123"));
    }
}