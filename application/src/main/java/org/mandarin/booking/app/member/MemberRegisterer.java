package org.mandarin.booking.app.member;

import org.mandarin.booking.domain.member.MemberRegisterRequest;
import org.mandarin.booking.domain.member.MemberRegisterResponse;

public interface MemberRegisterer {
    MemberRegisterResponse register(MemberRegisterRequest request);
}
