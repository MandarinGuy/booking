package org.mandarin.booking.utils;

import static org.mandarin.booking.utils.MemberFixture.NicknameGenerator.generateNickName;
import static org.mandarin.booking.utils.MemberFixture.PasswordGenerator.generatePassword;
import static org.mandarin.booking.utils.MemberFixture.UserIdGenerator.generateUserId;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.Collection;
import java.util.List;
import org.mandarin.booking.MemberAuthority;
import org.mandarin.booking.TokenHolder;
import org.mandarin.booking.adapter.TokenUtils;
import org.mandarin.booking.domain.member.Member;

public record IntegrationTestUtils(
        TestFixture fixture,
        TokenUtils tokenUtils,
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
        var member = fixture.insertDummyMember(generateUserId(), generatePassword());
        return tokenUtils.generateToken(member.getUserId(), member.getNickName(), member.getAuthorities())
                .refreshToken();
    }

    public String getAuthToken() {
        var member = fixture.insertDummyMember();
        return "Bearer " + this.getUserToken(member.getUserId(), member.getNickName(), member.getAuthorities())
                .accessToken();
    }

    public String getAuthToken(Member member) {
        return "Bearer " + this.getUserToken(member.getUserId(), member.getNickName(), member.getAuthorities())
                .accessToken();
    }

    public TokenHolder getUserToken(String userId, String nickname,
                                    Collection<MemberAuthority> authorities) {
        return tokenUtils.generateToken(userId, nickname, authorities);
    }

    public String getAuthToken(MemberAuthority... memberAuthority) {
        var member = fixture.insertDummyMember(generateUserId(), generateNickName(), List.of(memberAuthority));
        return "Bearer " + this.getUserToken(member.getUserId(), member.getNickName(), member.getAuthorities())
                .accessToken();
    }
}

