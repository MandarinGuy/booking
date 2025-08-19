package org.mandarin.booking.webapi.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mandarin.booking.fixture.MemberFixture.EmailGenerator.generateEmail;
import static org.mandarin.booking.fixture.MemberFixture.UserIdGenerator.generateUserId;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mandarin.booking.IntegrationTest;
import org.mandarin.booking.IntegrationTestUtils;
import org.mandarin.booking.infra.persist.MemberQueryRepository;
import org.mandarin.booking.infra.webapi.dto.MemberRegisterRequest;
import org.mandarin.booking.infra.webapi.dto.MemberRegisterResponse;
import org.mandarin.booking.domain.member.SecurePasswordEncoder;
import org.mandarin.booking.fixture.MemberFixture.NicknameGenerator;
import org.mandarin.booking.fixture.MemberFixture.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;

@IntegrationTest
@DisplayName("POST /api/members")
public class POST_specs {

    @Test
    void 올바른_요청하면_200_OK_상태코드를_반환한다(
            @Autowired TestRestTemplate testRestTemplate
    ) {

        // Arrange
        var request = generateRequest();

        // Act
        var response = testRestTemplate.postForEntity(
                "/api/members",
                request,
                Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    void 올바른_회원가입_요청을_하면_데이터베이스에_회원_정보가_저장된다(
            @Autowired TestRestTemplate testRestTemplate,
            @Autowired MemberQueryRepository memberRepository
    ) {
        // Arrange
        var request = generateRequest();

        // Act
        testRestTemplate.postForEntity(
                "/api/members",
                request,
                Void.class
        );

        // Assert
        var matchingMember = memberRepository.findByUserId(request.userId()).orElseThrow();

        assertThat(matchingMember).isNotNull();
    }

    @Test
    void 빈_값이나_null_값이_포함된_요청을_하면_400_Bad_Request_상태코드를_반환한다(
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange
        var request = new MemberRegisterRequest(
                null, // nickName
                generateUserId(),
                PasswordGenerator.generatePassword(),
                generateEmail()
        );

        // Act
        var response = testUtils.post(
                        "/api/members",
                        request
                )
                .assertFailure();

        // Assert
        assertThat(response.getData()).isEqualTo("Nickname cannot be blank");
    }

    @Test
    void 이미_존재하는_userId로_회원가입_요청을_하면_400_Bad_Request_상태(
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange
        var userId = "id";
        var existingRequest = new MemberRegisterRequest(
                NicknameGenerator.generateNickName(),
                userId,
                PasswordGenerator.generatePassword(),
                generateEmail()
        );
        var request = new MemberRegisterRequest(
                NicknameGenerator.generateNickName(),
                userId,
                PasswordGenerator.generatePassword(),
                generateEmail()
        );

        testUtils.post(
                        "/api/members",
                        existingRequest
                )
                .assertSuccess(MemberRegisterResponse.class);

        // Act
        var response = testUtils.post(
                        "/api/members",
                        request
                )
                .assertFailure();

        // Assert
        assertThat(response.getData()).contains("이미 존재하는 회원입니다:");
    }

    @Test
    void 이미_존재하는_email로_회원가입_요청을_하면_400_Bad_Request_상태코드를_반환한다(
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange
        var email = generateEmail();
        var existingRequest = new MemberRegisterRequest(
                NicknameGenerator.generateNickName(),
                generateUserId(),
                PasswordGenerator.generatePassword(),
                email
        );
        testUtils.post(
                        "/api/members",
                        existingRequest
                )
                .assertSuccess(MemberRegisterResponse.class);

        var request = new MemberRegisterRequest(
                NicknameGenerator.generateNickName(),
                generateUserId(),
                PasswordGenerator.generatePassword(),
                email
        );

        // Act
        var response = testUtils.post(
                        "/api/members",
                        request
                )
                .assertFailure();
        // Assert
        assertThat(response.getData()).contains("이미 존재하는 이메일입니다:");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "test@gmail",
            "test@.com",
            "test@com",
            "test.com",
            "@gmail.com"
    })
    void 올바르지_않은_형식의_email로_회원가입을_시도하면_400_Bad_Request_상태코드를_반환한다(
            String invalidEmail,
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange
        var request = new MemberRegisterRequest(
                NicknameGenerator.generateNickName(),
                generateUserId(),   // userId
                PasswordGenerator.generatePassword(),
                invalidEmail
        );

        // Act
        var response = testUtils.post(
                        "/api/members",
                        request
                )
                .assertFailure();

        // Assert
        assertThat(response.getData()).isEqualTo("Invalid email format");
    }

    @Test
    void 비밀번호가_올바르게_암호화_된다(
            @Autowired MemberQueryRepository memberRepository,
            @Autowired SecurePasswordEncoder securePasswordEncoder,
            @Autowired TestRestTemplate testRestTemplate
    ) {
        // Arrange
        String rawPassword = PasswordGenerator.generatePassword();
        var request = new MemberRegisterRequest(
                NicknameGenerator.generateNickName(),
                generateUserId(),
                rawPassword,
                generateEmail()
        );

        // Act
        var res = testRestTemplate.postForEntity(
                "/api/members",
                request,
                Void.class
        );
        assertThat(res.getStatusCode().value()).isEqualTo(200);

        // Assert
        var savedMember = memberRepository.findByUserId(request.userId()).orElseThrow();

        assertThat(securePasswordEncoder.matches(rawPassword, savedMember.getPasswordHash())).isTrue();
    }

    @Test
    void 회원가입_후_반환된_응답에_회원_정보가_포함된다(
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange
        var request = generateRequest();

        // Act
        var response = testUtils.post(
                        "/api/members",
                        request
                )
                .assertSuccess(MemberRegisterResponse.class);

        // Assert
        assertThat(response.getData()).isNotNull();
        assertThat(response.getData().userId()).isEqualTo(request.userId());
        assertThat(response.getData().nickName()).isEqualTo(request.nickName());
        assertThat(response.getData().email()).isEqualTo(request.email());

    }

    private MemberRegisterRequest generateRequest() {
        return new MemberRegisterRequest(
                NicknameGenerator.generateNickName(),
                generateUserId(),
                PasswordGenerator.generatePassword(),
                generateEmail()
        );
    }
}
