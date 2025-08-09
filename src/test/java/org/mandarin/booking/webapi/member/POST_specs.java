package org.mandarin.booking.webapi.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mandarin.booking.BookingApplication;
import org.mandarin.booking.domain.Member;
import org.mandarin.booking.domain.MemberRegisterRequest;
import org.mandarin.booking.domain.PasswordEncoder;
import org.mandarin.booking.persist.MemberJpaRepository;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(
        webEnvironment = RANDOM_PORT,
        classes = BookingApplication.class
)
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
            @Autowired MemberJpaRepository memberRepository
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
        var matchingMember = memberRepository.findAll()
                .stream()
                .filter(member -> member.getUserId().equals(request.userId()))
                .findFirst()
                .orElseThrow();

        assertThat(matchingMember).isNotNull();
    }


    @Test
    void 빈_값이나_null_값이_포함된_요청을_하면_400_Bad_Request_상태코드를_반환한다(
            @Autowired TestRestTemplate testRestTemplate
    ) {
        // Arrange
        var request = new MemberRegisterRequest(
                null, // nickName
                "testId",
                "testPassword",
                generateEMail()
        );

        // Act
        var response = testRestTemplate.postForEntity(
                "/api/members",
                request,
                Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void 이미_존재하는_userId로_회원가입_요청을_하면_400_Bad_Request_상태(
            @Autowired TestRestTemplate testRestTemplate
    ) {
        // Arrange
        var userId = "id";
        var existingRequest = new MemberRegisterRequest(
                "nickName1",
                userId,
                "password1",
                "test1@gmail.com"
        );
        var request = new MemberRegisterRequest(
                "nickName2",
                userId,
                "password2",
                "test2@gmail.com"
        );

        testRestTemplate.postForEntity(
                "/api/members",
                existingRequest,
                Void.class
        );

        // Act
        var response = testRestTemplate.postForEntity(
                "/api/members",
                request,
                Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void 이미_존재하는_email로_회원가입_요청을_하면_400_Bad_Request_상태코드를_반환한다(
            @Autowired TestRestTemplate testRestTemplate
    ) {
        // Arrange
        var email = generateEMail();
        var existingRequest = new MemberRegisterRequest(
                "nickName1",
                "testId1",
                "password1",
                email
        );
        testRestTemplate.postForEntity(
                "/api/members",
                existingRequest,
                Void.class
        );

        var request = new MemberRegisterRequest(
                "nickName2",
                "testId2",
                "password2",
                email
        );

        // Act
        var response = testRestTemplate.postForEntity(
                "/api/members",
                request,
                Void.class
        );
        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
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
            @Autowired TestRestTemplate testRestTemplate
    ) {
        // Arrange
        var request = new MemberRegisterRequest(
                "nickName1",
                UUID.randomUUID().toString(),   // userId
                "password1",
                invalidEmail
        );

        // Act
        var response = testRestTemplate.postForEntity(
                "/api/members",
                request,
                Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void 비밀번호가_올바르게_암호화_된다(
            @Autowired MemberJpaRepository memberRepository,
            @Autowired PasswordEncoder passwordEncoder,
            @Autowired TestRestTemplate testRestTemplate
    ) {
        // Arrange
        String rawPassword = "testPassword";
        var request = new MemberRegisterRequest(
                "testName",
                UUID.randomUUID().toString(), // userId
                rawPassword,
                generateEMail()
        );

        // Act
        var res = testRestTemplate.postForEntity(
                "/api/members",
                request,
                Void.class
        );
        assertThat(res.getStatusCode().value()).isEqualTo(200);

        // Assert
        List<Member> memberList = memberRepository.findAll();

        memberList.forEach(System.out::println);

        var savedMember = memberList
                .stream()
                .filter(member -> member.getUserId().equals(request.userId()))
                .findFirst()
                .orElseThrow();

        assertThat(passwordEncoder.matches(rawPassword, savedMember.getPasswordHash())).isTrue();
    }

    private static String generateEMail() {
        return UUID.randomUUID().toString() + "@gmail.com";
    }


    private static MemberRegisterRequest generateRequest() {
        return new MemberRegisterRequest(
                "testName",
                UUID.randomUUID().toString(),
                "testPassword",
                generateEMail()
        );
    }
}
