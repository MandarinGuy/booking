package org.mandarin.booking.domain;

import lombok.RequiredArgsConstructor;
import org.mandarin.booking.adapter.webapi.MemberRegisterer;
import org.mandarin.booking.persist.MemberJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService implements MemberRegisterer {
    private final MemberJpaRepository memberJpaRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void register(MemberRegisterRequest request) {
        if(memberJpaRepository.existsByUserId(request.userId()))
            throw new IllegalArgumentException("이미 존재하는 회원입니다: " + request.userId());
        if(memberJpaRepository.existsByEmail(request.email()))
            throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + request.email());

        var newMember = Member.create(request, passwordEncoder);
        memberJpaRepository.save(newMember);
    }
}
