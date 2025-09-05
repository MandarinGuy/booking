package org.mandarin.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mandarin.booking.app.TokenUtils;
import org.mandarin.booking.app.persist.MemberCommandRepository;
import org.mandarin.booking.domain.member.SecurePasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {
    @Bean
    public IntegrationTestUtils integrationTestUtils(@Autowired MemberCommandRepository memberRepository,
                                                     @Autowired TokenUtils tokenUtils,
                                                     @Autowired SecurePasswordEncoder securePasswordEncoder,
                                                     @Autowired ObjectMapper objectMapper,
                                                     @Autowired DocsUtils docsUtils) {
        return new IntegrationTestUtils(memberRepository, tokenUtils, securePasswordEncoder, objectMapper, docsUtils);
    }
}
