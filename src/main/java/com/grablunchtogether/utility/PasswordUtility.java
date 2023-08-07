package com.grablunchtogether.utility;

import lombok.experimental.UtilityClass;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@UtilityClass
public class PasswordUtility {
    public static String getEncryptPassword(String password) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder.encode(password);
    }

    public static boolean isPasswordMatch(String passwordOriginal,
                                          String passwordEncrypted) {
        try {
            return BCrypt.checkpw(passwordOriginal, passwordEncrypted);
        } catch (Exception e) {
            return false;
        }
    }
}
