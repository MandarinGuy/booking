package org.mandarin.booking.adapter.webapi;

import org.mandarin.booking.domain.MemberRegisterRequest;

public interface MemberRegisterer {
    void register(MemberRegisterRequest request);
}
