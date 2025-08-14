package org.mandarin.booking;

import static org.mandarin.booking.fixture.MemberFixture.EmailGenerator.generateEmail;
import static org.mandarin.booking.fixture.MemberFixture.NicknameGenerator.generateNickName;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.mandarin.booking.adapter.persist.MemberCommandRepository;
import org.mandarin.booking.adapter.webapi.ApiResponse;
import org.mandarin.booking.adapter.webapi.ErrorResponse;
import org.mandarin.booking.adapter.webapi.SuccessResponse;
import org.mandarin.booking.app.SecurePasswordEncoder;
import org.mandarin.booking.domain.member.Member;
import org.mandarin.booking.domain.member.MemberCreateCommand;
import org.springframework.boot.test.web.client.TestRestTemplate;

public class IntegrationTestUtils {
    private final TestRestTemplate testRestTemplate;
    private final MemberCommandRepository memberRepository;
    private final SecurePasswordEncoder securePasswordEncoder;
    private final ObjectMapper objectMapper;

    public IntegrationTestUtils(TestRestTemplate testRestTemplate,
                                MemberCommandRepository memberRepository,
                                SecurePasswordEncoder securePasswordEncoder,
                                ObjectMapper objectMapper) {
        this.testRestTemplate = testRestTemplate;
        this.memberRepository = memberRepository;
        this.securePasswordEncoder = securePasswordEncoder;
        this.objectMapper = objectMapper;
    }

    public Member insertDummyMember(String userId, String password) {
        return memberRepository.insert(
                Member.create(new MemberCreateCommand(
                        generateNickName(),
                        userId,
                        password,
                        generateEmail()
                ), securePasswordEncoder)
        );
    }

    @SuppressWarnings("unchecked")
    public <T,R> ApiResponse post(String path, T request, Class<R> responseType) {
        Map<String, Object> res = testRestTemplate.postForObject(path, request, Map.class);

        if (res == null) {
            return new ErrorResponse("INTERNAL_CLIENT_ERROR", "empty response");
        }

        Object successObj = res.get("success");
        boolean success = successObj instanceof Boolean b ? b : true;
        String status = (String) res.getOrDefault("status", "SUCCESS");

        if (!success) {
            String message = (String) res.get("message");
            return new ErrorResponse(status, message);
        }

        Object dataObj = res.get("data");
        var data = objectMapper.convertValue(dataObj, responseType);
        return new SuccessResponse<>(status, data);
    }
}
