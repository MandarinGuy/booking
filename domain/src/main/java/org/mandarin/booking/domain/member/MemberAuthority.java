package org.mandarin.booking.domain.member;

public enum MemberAuthority {
    USER,
    DISTRIBUTOR,
    ADMIN;

    public String getAuthority() {
        return "ROLE_" + name().toUpperCase();
    }
}
