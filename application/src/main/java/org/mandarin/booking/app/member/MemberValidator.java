package org.mandarin.booking.app.member;

interface MemberValidator {
    void checkDuplicateEmail(String email);

    void checkDuplicateUserId(String userId);
}
