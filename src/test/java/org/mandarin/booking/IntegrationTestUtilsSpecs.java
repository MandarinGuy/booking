package org.mandarin.booking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mandarin.booking.adapter.webapi.ApiStatus.SUCCESS;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mandarin.booking.domain.member.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;

@Disabled
@IntegrationTest
@DisplayName("IntegrationTestUtils 동작 점검")
public class IntegrationTestUtilsSpecs {

    @Test
    @DisplayName("test-only endpoint(/test/echo)에 요청을 보내면 SUCCESS 응답을 파싱한다")
    void post_echo_success(
            @Autowired IntegrationTestUtils integrationUtils
    ) {
        // Arrange
        Map<String, Object> payload = new HashMap<>();
        payload.put("message", "hello");
        payload.put("value", 123);

        // Act
        var response = integrationUtils.post("/test/echo", payload)
                .assertSuccess(new ParameterizedTypeReference<Map<String, Object>>() {});

        // Assert
        assertThat(response.getStatus()).isEqualTo(SUCCESS);
        assertThat(response.getData()).isNotNull();
        assertThat(response.getData()).containsEntry("message", "hello");
        assertThat(response.getData()).containsEntry("value", 123);
    }

    @Test
    @DisplayName("insertDummyMember로 저장한 회원을 test-only endpoint(/test/member/exists)로 검증한다")
    void insertDummyMember_and_verify_exists(
            @Autowired IntegrationTestUtils integrationUtils
    ) {
        // Arrange
        String userId = "it_utils_user" + System.currentTimeMillis();
        String password = "P@ssw0rd!";

        // save member using utils
        Member saved = integrationUtils.insertDummyMember(userId, password);
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();

        // Act
        Map<String, String> request = Map.of("userId", userId);
        var response = integrationUtils.post("/test/member/exists", request)
                .assertSuccess(Boolean.class);

        // Assert
        assertThat(response.getStatus()).isEqualTo(SUCCESS);
        assertThat(response.getData()).isNotNull();
        assertThat(response.getData()).isTrue();
    }
}
