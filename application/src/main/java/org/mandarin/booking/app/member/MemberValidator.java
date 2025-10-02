package org.mandarin.booking.app.member;

public interface MemberValidator {
    void checkDuplicateEmail(String email);

    void checkDuplicateUserId(String userId);
}
