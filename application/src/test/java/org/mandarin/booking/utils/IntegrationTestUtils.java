package org.mandarin.booking.utils;

import static org.mandarin.booking.utils.MemberFixture.EmailGenerator.generateEmail;
import static org.mandarin.booking.utils.MemberFixture.NicknameGenerator.generateNickName;
import static org.mandarin.booking.utils.MemberFixture.PasswordGenerator.generatePassword;
import static org.mandarin.booking.utils.MemberFixture.UserIdGenerator.generateUserId;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.mandarin.booking.MemberAuthority;
import org.mandarin.booking.TokenHolder;
import org.mandarin.booking.adapter.TokenUtils;
import org.mandarin.booking.app.member.MemberCommandRepository;
import org.mandarin.booking.app.show.ShowCommandRepository;
import org.mandarin.booking.app.venue.HallCommandRepository;
import org.mandarin.booking.domain.member.Member;
import org.mandarin.booking.domain.member.Member.MemberCreateCommand;
import org.mandarin.booking.domain.member.SecurePasswordEncoder;
import org.mandarin.booking.domain.show.Show;
import org.mandarin.booking.domain.show.Show.ShowCreateCommand;
import org.mandarin.booking.domain.show.ShowRegisterRequest;
import org.mandarin.booking.domain.venue.Hall;
import org.springframework.test.util.ReflectionTestUtils;

public record IntegrationTestUtils(MemberCommandRepository memberRepository,
                                   ShowCommandRepository showRepository,
                                   HallCommandRepository hallRepository,
                                   TokenUtils tokenUtils,
                                   SecurePasswordEncoder securePasswordEncoder,
                                   ObjectMapper objectMapper,
                                   DocsUtils docsUtils) {
    public IntegrationTestUtils {
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public TestResult get(String path, Object... requestParams) {
        return new TestResult(path, null, requestParams)
                .setContext(objectMapper)
                .setExecutor((p, req, headers, params) -> docsUtils.execute("GET", p, null, headers, params));
    }

    public <T> TestResult post(String path, T request) {
        return new TestResult(path, request)
                .setContext(objectMapper)
                .setExecutor((p, req, headers, params) -> docsUtils.execute("POST", p, req, headers, params));
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
                                    Collection<MemberAuthority> authorities) {
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

    public Show insertDummyShow(LocalDate performanceStartDate, LocalDate performanceEndDate) {
        var command = ShowCreateCommand.from(
                new ShowRegisterRequest(
                        UUID.randomUUID().toString().substring(0, 5),
                        "MUSICAL",
                        "AGE12",
                        "synopsis",
                        "https://example.com/poster.jpg",
                        performanceStartDate,
                        performanceEndDate
                )
        );
        var show = Show.create(command);
        return showRepository.insert(show);
    }

    public String getAuthToken(MemberAuthority... memberAuthority) {
        var member = this.insertDummyMember(generateUserId(), generateNickName(), List.of(memberAuthority));
        return "Bearer " + this.getUserToken(member.getUserId(), member.getNickName(), member.getAuthorities())
                .accessToken();
    }

    public Hall insertDummyHall() {
        var hall = Hall.create();
        return hallRepository.insert(hall);
    }
}

