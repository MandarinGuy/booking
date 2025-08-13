package org.mandarin.booking.domain.service;

public interface SecurePasswordEncoder {
    /**
     * Encodes the given password.
     * @param password
     * @return
     */
    String encode(String password);
    boolean matches(String rawPassword, String encodedPassword);
}
