package org.mandarin.booking.domain;

public interface PasswordEncoder {
    /**
     * Encodes the given password.
     * @param password
     * @return
     */
    String encode(String password);
    boolean matches(String rawPassword, String encodedPassword);
}
