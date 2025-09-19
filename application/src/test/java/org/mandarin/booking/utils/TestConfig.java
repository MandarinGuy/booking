package org.mandarin.booking.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mandarin.booking.adapter.TokenUtils;
import org.mandarin.booking.app.member.MemberCommandRepository;
import org.mandarin.booking.app.show.ShowCommandRepository;
import org.mandarin.booking.app.venue.HallCommandRepository;
import org.mandarin.booking.domain.member.SecurePasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {
    @Bean
    public IntegrationTestUtils integrationTestUtils(@Autowired TestFixture testFixture,
                                                     @Autowired TokenUtils tokenUtils,
                                                     @Autowired ObjectMapper objectMapper,
                                                     @Autowired DocsUtils docsUtils) {
        return new IntegrationTestUtils(testFixture, tokenUtils, objectMapper, docsUtils);
    }

    @Bean
    public TestFixture testFixture(@Autowired MemberCommandRepository memberRepository,
                                   @Autowired ShowCommandRepository showRepository,
                                   @Autowired HallCommandRepository hallRepository,
                                   @Autowired SecurePasswordEncoder securePasswordEncoder) {
        return new TestFixture(memberRepository, showRepository, hallRepository, securePasswordEncoder);
    }
}
