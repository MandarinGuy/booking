package org.mandarin.booking;

import static org.mandarin.booking.fixture.MemberFixture.EmailGenerator.generateEmail;
import static org.mandarin.booking.fixture.MemberFixture.NicknameGenerator.generateNickName;
import static org.mandarin.booking.fixture.MemberFixture.PasswordGenerator.generatePassword;
import static org.mandarin.booking.fixture.MemberFixture.UserIdGenerator.generateUserId;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.Collection;
import java.util.List;
import org.mandarin.booking.app.TokenUtils;
import org.mandarin.booking.app.persist.MemberCommandRepository;
import org.mandarin.booking.domain.member.Member;
import org.mandarin.booking.domain.member.Member.MemberCreateCommand;
import org.mandarin.booking.domain.member.MemberAuthority;
import org.mandarin.booking.domain.member.SecurePasswordEncoder;
import org.mandarin.booking.domain.member.TokenHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

public record IntegrationTestUtils(MemberCommandRepository memberRepository,
                                   TokenUtils tokenUtils,
                                   SecurePasswordEncoder securePasswordEncoder,
                                   ObjectMapper objectMapper,
                                   DocsUtils docsUtils) {
    public IntegrationTestUtils {
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public TestResult get(String path) {
        return new TestResult(path, null)
                .setContext(objectMapper)
                .setExecutor((p, req, headers) -> docsUtils.execute("GET", p, null, headers));
    }

    public <T> TestResult post(String path, T request) {
        return new TestResult(path, request)
                .setContext(objectMapper)
                .setExecutor((p, req, headers) -> docsUtils.execute("POST", p, req, headers));
    }

    public String getValidRefreshToken() {
        var member = insertDummyMember(generateUserId(), generatePassword());
        return tokenUtils.generateToken(member.getUserId(), member.getNickName(), member.getAuthorities())
                .refreshToken();
    }

    public String getAuthToken() {
        var member = this.insertDummyMember();
        return "Bearer " + this.getUserToken(member.getUserId(), member.getNickName(), member.getAuthorities())
                .accessToken();
    }

    public String getAuthToken(Member member) {
        return "Bearer " + this.getUserToken(member.getUserId(), member.getNickName(), member.getAuthorities())
                .accessToken();
    }

    public TokenHolder getUserToken(String userId, String nickname,
                                    Collection<? extends GrantedAuthority> authorities) {
        return tokenUtils.generateToken(userId, nickname, authorities);
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

    public Member insertDummyMember(String userId, String nickName, List<MemberAuthority> authorities) {
        var command = new MemberCreateCommand(
                nickName,
                userId,
                generatePassword(),
                generateEmail()
        );
        var member = Member.create(command, securePasswordEncoder);
        ReflectionTestUtils.setField(member, "authorities", authorities);
        return memberRepository.insert(
                member
        );
    }

    public Member insertDummyMember() {
        return this.insertDummyMember(generateUserId(), generatePassword());
    }

    public String getAuthToken(MemberAuthority... memberAuthority) {
        var member = this.insertDummyMember(generateUserId(), generateNickName(), List.of(memberAuthority));
        return "Bearer " + this.getUserToken(member.getUserId(), member.getNickName(), member.getAuthorities())
                .accessToken();
    }
}

