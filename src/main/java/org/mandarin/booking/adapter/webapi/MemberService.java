package org.mandarin.booking.adapter.webapi;

import lombok.RequiredArgsConstructor;
import org.mandarin.booking.domain.Member;
import org.mandarin.booking.domain.MemberRegisterRequest;
import org.mandarin.booking.persist.MemberJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService implements MemberRegisterer {
    private final MemberJpaRepository memberJpaRepository;

    @Override
    @Transactional
    public void register(MemberRegisterRequest request) {
        var newMember = Member.register(request);
        memberJpaRepository.save(newMember);
    }
}
