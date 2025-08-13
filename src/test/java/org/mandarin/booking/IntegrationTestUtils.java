package org.mandarin.booking;

import static org.mandarin.booking.fixture.MemberFixture.EmailGenerator.generateEmail;
import static org.mandarin.booking.fixture.MemberFixture.NicknameGenerator.generateNickName;

import org.mandarin.booking.adapter.webapi.MemberRegisterRequest;
import org.mandarin.booking.domain.Member;
import org.mandarin.booking.domain.SecurePasswordEncoder;
import org.mandarin.booking.persist.MemberCommandRepository;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

public class IntegrationTestUtils {
    private final TestRestTemplate testRestTemplate;
    private final MemberCommandRepository memberRepository;
    private final SecurePasswordEncoder securePasswordEncoder;

    public IntegrationTestUtils(TestRestTemplate testRestTemplate,
                               MemberCommandRepository memberRepository,
                               SecurePasswordEncoder securePasswordEncoder) {
        this.testRestTemplate = testRestTemplate;
        this.memberRepository = memberRepository;
        this.securePasswordEncoder = securePasswordEncoder;
    }

    public Member insertDummyMember(String userId, String password) {
        return memberRepository.insert(
                Member.create(new MemberRegisterRequest(
                        generateNickName(),
                        userId,
                        password,
                        generateEmail()
                ), securePasswordEncoder)
        );
    }

    public <T,R> ResponseEntity<R> post(String path, T request, Class<R> responseType) {
        return testRestTemplate.postForEntity(path, request, responseType);
    }
}
