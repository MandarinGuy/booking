package org.mandarin.booking.domain.member;

public interface SecurePasswordEncoder {
    String encode(String password);
    boolean matches(String rawPassword, String encodedPassword);
}
