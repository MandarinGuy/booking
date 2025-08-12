package org.mandarin.booking.webapi.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mandarin.booking.fixture.MemberFixture.EmailGenerator.generateEmail;
import static org.mandarin.booking.fixture.MemberFixture.NicknameGenerator.generateNickName;
import static org.mandarin.booking.fixture.MemberFixture.PasswordGenerator.generatePassword;
import static org.mandarin.booking.fixture.MemberFixture.UserIdGenerator.generateUserId;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mandarin.booking.BookingApplication;
import org.mandarin.booking.adapter.webapi.AuthRequest;
import org.mandarin.booking.adapter.webapi.MemberRegisterRequest;
import org.mandarin.booking.adapter.webapi.TokenHolder;
import org.mandarin.booking.domain.Member;
import org.mandarin.booking.domain.PasswordEncoder;
import org.mandarin.booking.persist.MemberCommandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

@SpringBootTest(
        webEnvironment = RANDOM_PORT,
        classes = BookingApplication.class
)
public class POST_specs {
    @Test
    void 올바른_요청을_보내면_200_OK_상태코드를_반환한다(
            @Autowired TestRestTemplate testRestTemplate
    ) {
        // Arrange
        var request = new AuthRequest("testUser", "testPassword");

        // Act
        var response = testRestTemplate.postForEntity(
                "/api/auth/login",
                request,
                TokenHolder.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

    @ParameterizedTest
    @MethodSource("org.mandarin.booking.webapi.auth.POST_specs#blankUserIdRequests")
    void 요청_본문의_userId가_누락된_경우_400_Bad_Request_상태코드를_반환한다(
            // Arrange
            AuthRequest request,
            @Autowired TestRestTemplate testRestTemplate
    ) {
        // Act
        var response = testRestTemplate.postForEntity(
                "/api/auth/login",
                request,
                TokenHolder.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @ParameterizedTest
    @MethodSource("org.mandarin.booking.webapi.auth.POST_specs#blankPasswordRequests")
    void 요청_본문의_password가_누락된_경우_400_Bad_Request_상태코드를_반환한다(
            // Arrange
            AuthRequest request,
            @Autowired TestRestTemplate testRestTemplate
    ) {
        // Act
        var response = testRestTemplate.postForEntity(
                "/api/auth/login",
                request,
                TokenHolder.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void 존재하지_않는_userId_비밀번호로_요청하면_401_Unauthorized_상태코드를_반환한다(
            @Autowired TestRestTemplate testRestTemplate
    ) {
        // Arrange
        var request = new AuthRequest("nonExistentUser", generatePassword());

        // don't need to save member in the database

        // Act
        var response = testRestTemplate.postForEntity(
                "/api/auth/login",
                request,
                TokenHolder.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(401);
    }

    @Test
    void 요청_본문의_password가_userId에_해당하는_password가_일치하지_않으면_401_Unauthorized_상태코드를_반환한다(
            @Autowired TestRestTemplate testRestTemplate,
            @Autowired MemberCommandRepository memberRepository,
            @Autowired PasswordEncoder passwordEncoder
    ) {
        // Arrange
        var userId = generateUserId();

        var invalidPassword = generatePassword();
        var request = new AuthRequest(userId, invalidPassword);

        //save member
        insertDummyMember(memberRepository, passwordEncoder, userId, generatePassword());

        // Act
        var response = testRestTemplate.postForEntity(
                "/api/auth/login",
                request,
                TokenHolder.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(401);
    }

    @Test
    void 성공적인_로그인_후_응답에_accessToken과_refreshToken가_포함되어야_한다(
            @Autowired TestRestTemplate testRestTemplate,
            @Autowired MemberCommandRepository memberRepository,
            @Autowired PasswordEncoder passwordEncoder
    ) {
        // Arrange
        var request = new AuthRequest(generateUserId(), generatePassword());
        insertDummyMember(memberRepository, passwordEncoder, request.userId(), request.password());

        // Act
        var response = testRestTemplate.postForEntity(
                "/api/auth/login",
                request,
                TokenHolder.class
        );

        // Assert
        assertThat(response.getBody().accessToken()).isNotBlank();
        assertThat(response.getBody().refreshToken()).isNotBlank();

    }

    private static AuthRequest[] blankUserIdRequests() {
        return new AuthRequest[]{
                new AuthRequest(null, generatePassword()),
                new AuthRequest("", generatePassword()),
                new AuthRequest(" ", generatePassword())
        };
    }

    private static AuthRequest[] blankPasswordRequests() {
        return new AuthRequest[]{
                new AuthRequest("testUser", null),
                new AuthRequest("testUser", ""),
                new AuthRequest("testUser", " ")
        };
    }

    private static void insertDummyMember(MemberCommandRepository memberRepository,
                                          PasswordEncoder passwordEncoder,
                                          String userId, String password) {
        memberRepository.insert(
                Member.create(new MemberRegisterRequest(
                        generateNickName(),
                        userId,
                        password,
                        generateEmail()
                ), passwordEncoder)
        );
    }
}
