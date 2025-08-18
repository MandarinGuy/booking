package org.mandarin.booking.app.port;

import org.mandarin.booking.infra.webapi.dto.MemberRegisterRequest;
import org.mandarin.booking.infra.webapi.dto.MemberRegisterResponse;

public interface MemberRegisterer {
    MemberRegisterResponse register(MemberRegisterRequest request);
}
