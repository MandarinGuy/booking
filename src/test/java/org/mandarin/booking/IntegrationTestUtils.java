package org.mandarin.booking;

import static org.mandarin.booking.fixture.MemberFixture.EmailGenerator.generateEmail;
import static org.mandarin.booking.fixture.MemberFixture.NicknameGenerator.generateNickName;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.mandarin.booking.app.persist.MemberCommandRepository;
import org.mandarin.booking.domain.member.SecurePasswordEncoder;
import org.mandarin.booking.domain.member.Member;
import org.mandarin.booking.domain.member.Member.MemberCreateCommand;
import org.springframework.boot.test.web.client.TestRestTemplate;

public class IntegrationTestUtils {
    private final TestRestTemplate testRestTemplate;
    private final MemberCommandRepository memberRepository;
    private final SecurePasswordEncoder securePasswordEncoder;
    private final ObjectMapper objectMapper;

    public IntegrationTestUtils(TestRestTemplate testRestTemplate,
                                MemberCommandRepository memberRepository,
                                SecurePasswordEncoder securePasswordEncoder,
                                ObjectMapper objectMapper) {
        this.testRestTemplate = testRestTemplate;
        this.memberRepository = memberRepository;
        this.securePasswordEncoder = securePasswordEncoder;
        this.objectMapper = objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public Member insertDummyMember(String userId, String password) {
        return memberRepository.insert(
                Member.create(new MemberCreateCommand(
                        generateNickName(),
                        userId,
                        password,
                        generateEmail()
                ), securePasswordEncoder)
        );
    }

    public <T> TestResult post(String path, T request) {
        return new TestResult(path, request)
                .setContext(testRestTemplate, objectMapper);
    }
}

