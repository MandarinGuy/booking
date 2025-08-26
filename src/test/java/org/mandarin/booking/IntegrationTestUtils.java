package org.mandarin.booking;

import static org.mandarin.booking.fixture.MemberFixture.EmailGenerator.generateEmail;
import static org.mandarin.booking.fixture.MemberFixture.NicknameGenerator.generateNickName;
import static org.mandarin.booking.fixture.MemberFixture.PasswordGenerator.generatePassword;
import static org.mandarin.booking.fixture.MemberFixture.UserIdGenerator.generateUserId;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.mandarin.booking.app.TokenUtils;
import org.mandarin.booking.app.persist.MemberCommandRepository;
import org.mandarin.booking.domain.member.Member;
import org.mandarin.booking.domain.member.Member.MemberCreateCommand;
import org.mandarin.booking.domain.member.SecurePasswordEncoder;
import org.springframework.boot.test.web.client.TestRestTemplate;

public class IntegrationTestUtils {
    private final TestRestTemplate testRestTemplate;
    private final MemberCommandRepository memberRepository;
    private final TokenUtils tokenUtils;
    private final SecurePasswordEncoder securePasswordEncoder;
    private final ObjectMapper objectMapper;

    public IntegrationTestUtils(TestRestTemplate testRestTemplate,
                                MemberCommandRepository memberRepository,
                                TokenUtils tokenUtils,
                                SecurePasswordEncoder securePasswordEncoder,
                                ObjectMapper objectMapper) {
        this.testRestTemplate = testRestTemplate;
        this.memberRepository = memberRepository;
        this.tokenUtils = tokenUtils;
        this.securePasswordEncoder = securePasswordEncoder;
        this.objectMapper = objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public Member insertDummyMember(String userId, String password) {
        var command = new MemberCreateCommand(
                generateNickName(),
                userId,
                password,
                generateEmail()
        );
        return memberRepository.insert(
                Member.create(command, securePasswordEncoder)
        );
    }

    public <T> TestResult get(String path) {
        return new TestResult(path, null)
                .setContext(testRestTemplate, objectMapper);
    }

    public <T> TestResult post(String path, T request) {
        return new TestResult(path, request)
                .setContext(testRestTemplate, objectMapper);
    }

    public String getUserToken(String userId, String nickname) {
        return tokenUtils.generateToken(userId, nickname).accessToken();
    }

    public Member insertDummyMember() {
        return this.insertDummyMember(generateUserId(), generatePassword());
    }
}

