package org.mandarin.booking.app.member;

import lombok.RequiredArgsConstructor;
import org.mandarin.booking.domain.member.Member;
import org.mandarin.booking.domain.member.Member.MemberCreateCommand;
import org.mandarin.booking.domain.member.MemberException;
import org.mandarin.booking.domain.member.MemberRegisterRequest;
import org.mandarin.booking.domain.member.MemberRegisterResponse;
import org.mandarin.booking.domain.member.SecurePasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class MemberService implements MemberRegisterer, MemberValidator {
    private final MemberCommandRepository command;
    private final MemberQueryRepository queryRepository;
    private final SecurePasswordEncoder securePasswordEncoder;

    @Override
    public MemberRegisterResponse register(MemberRegisterRequest request) {
        checkDuplicateUserId(request.userId());
        checkDuplicateEmail(request.email());

        var createCommand
                = new MemberCreateCommand(request.nickName(), request.userId(), request.password(), request.email());
        var newMember = Member.create(createCommand, securePasswordEncoder);
        var savedMember = this.command.insert(newMember);
        return MemberRegisterResponse.from(savedMember);
    }

    @Override
    public void checkDuplicateEmail(String email) {
        if (queryRepository.existsByEmail(email)) {
            throw new MemberException("이미 존재하는 이메일입니다: " + email);
        }
    }

    @Override
    public void checkDuplicateUserId(String userId) {
        if (queryRepository.existsByUserId(userId)) {
            throw new MemberException("이미 존재하는 회원입니다: " + userId);
        }
    }
}
