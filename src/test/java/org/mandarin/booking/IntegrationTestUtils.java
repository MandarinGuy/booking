package org.mandarin.booking;

import static org.mandarin.booking.fixture.MemberFixture.EmailGenerator.generateEmail;
import static org.mandarin.booking.fixture.MemberFixture.NicknameGenerator.generateNickName;
import static org.mandarin.booking.fixture.MemberFixture.PasswordGenerator.generatePassword;
import static org.mandarin.booking.fixture.MemberFixture.UserIdGenerator.generateUserId;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.Collection;
import org.mandarin.booking.app.TokenUtils;
import org.mandarin.booking.app.persist.MemberCommandRepository;
import org.mandarin.booking.domain.member.Member;
import org.mandarin.booking.domain.member.Member.MemberCreateCommand;
import org.mandarin.booking.domain.member.SecurePasswordEncoder;
import org.mandarin.booking.domain.member.TokenHolder;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.security.core.GrantedAuthority;

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

    public String getValidRefreshToken() {
        var member = insertDummyMember(generateUserId(), generatePassword());
        return tokenUtils.generateToken(member.getUserId(), member.getNickName(), member.getAuthorities()).refreshToken();
    }

    public String getAuthToken() {
        var member = this.insertDummyMember();
         return "Bearer " + this.getUserToken(member.getUserId(), member.getNickName(), member.getAuthorities()).accessToken();
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

    public TokenHolder getUserToken(String userId, String nickname, Collection<? extends GrantedAuthority> authorities) {
        return tokenUtils.generateToken(userId, nickname, authorities);
    }

    public Member insertDummyMember() {
        return this.insertDummyMember(generateUserId(), generatePassword());
    }
}

