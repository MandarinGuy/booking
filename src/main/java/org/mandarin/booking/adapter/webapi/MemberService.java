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
        if(memberJpaRepository.existsByUserId(request.userId()))
            throw new IllegalArgumentException("이미 존재하는 회원입니다: " + request.userId());
        var newMember = Member.register(request);
        memberJpaRepository.save(newMember);
    }
}
