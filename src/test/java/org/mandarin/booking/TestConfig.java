package org.mandarin.booking;

import org.mandarin.booking.app.SecurePasswordEncoder;
import org.mandarin.booking.adapter.persist.MemberCommandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {
    @Bean
    public IntegrationTestUtils integrationTestUtils(@Autowired TestRestTemplate testRestTemplate,
                                                     @Autowired MemberCommandRepository memberRepository,
                                                     @Autowired SecurePasswordEncoder securePasswordEncoder) {
        return new IntegrationTestUtils(testRestTemplate, memberRepository, securePasswordEncoder);
    }
}
