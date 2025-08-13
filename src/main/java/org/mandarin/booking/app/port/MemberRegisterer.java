package org.mandarin.booking.app.port;

import org.mandarin.booking.adapter.webapi.MemberRegisterRequest;
import org.mandarin.booking.adapter.webapi.MemberRegisterResponse;

public interface MemberRegisterer {
    MemberRegisterResponse register(MemberRegisterRequest request);
}
