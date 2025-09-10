package org.mandarin.booking;

public enum MemberAuthority {
    USER,
    DISTRIBUTOR,
    ADMIN;

    public String getAuthority() {
        return "ROLE_" + name().toUpperCase();
    }
}
