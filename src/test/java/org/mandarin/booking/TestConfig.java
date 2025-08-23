package org.mandarin.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mandarin.booking.app.persist.MemberCommandRepository;
import org.mandarin.booking.domain.member.SecurePasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {
    @Bean
    public IntegrationTestUtils integrationTestUtils(@Autowired TestRestTemplate testRestTemplate,
                                                     @Autowired MemberCommandRepository memberRepository,
                                                     @Autowired SecurePasswordEncoder securePasswordEncoder,
                                                     @Autowired ObjectMapper objectMapper) {
        return new IntegrationTestUtils(testRestTemplate, memberRepository, securePasswordEncoder, objectMapper);
    }
}
