package org.mandarin.booking.adapter;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class JacksonCustomizerConfig {
    @Bean
    Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> {
            builder.timeZone("Asia/Seoul");
            builder.simpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        };
    }
}
