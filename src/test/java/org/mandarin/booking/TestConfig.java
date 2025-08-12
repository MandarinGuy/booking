package org.mandarin.booking;

import org.mandarin.booking.domain.PasswordEncoder;
import org.mandarin.booking.persist.MemberCommandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {
    @Bean
    public IntegrationTestUtils integrationTestUtils(@Autowired TestRestTemplate testRestTemplate,
                                                     @Autowired MemberCommandRepository memberRepository,
                                                     @Autowired PasswordEncoder passwordEncoder) {
        return new IntegrationTestUtils(testRestTemplate, memberRepository, passwordEncoder);
    }
}
