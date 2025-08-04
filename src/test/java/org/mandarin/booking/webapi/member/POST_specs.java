package org.mandarin.booking.webapi.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import org.junit.jupiter.api.Test;
import org.mandarin.booking.BookingApplication;
import org.mandarin.booking.domain.MemberRegisterRequest;
import org.mandarin.booking.persist.MemberJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

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
                "test@gmail.com"
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
    private static MemberRegisterRequest generateRequest() {
        return new MemberRegisterRequest(
                "testName",
                "testId",
                "testPassword",
                "test@gmail.com"
        );
    }
}
