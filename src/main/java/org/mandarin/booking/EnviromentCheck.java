package org.mandarin.booking;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EnviromentCheck {
    public final Environment env;

    public EnviromentCheck(Environment env) {
        this.env = env;
    }

    @PostConstruct
    public void init() {

        String name = env.getProperty("name");

         log.info("name = {}", name);

    }
}