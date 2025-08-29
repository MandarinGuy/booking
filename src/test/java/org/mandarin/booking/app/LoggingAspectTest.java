package org.mandarin.booking.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mandarin.booking.IntegrationTest;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@IntegrationTest
@Import(LoggingAspectTest.TestConfig.class)
class LoggingAspectTest {

    private ListAppender<ILoggingEvent> listAppender;

    @Autowired
    LoggingAspectTest.SampleService bean;

    @Autowired
    LoggingAspectTest.BlankMethodOnlyService blankMethodOnlyService;

    @BeforeEach
    void setUp() {
        Logger logger = (Logger) LoggerFactory.getLogger(SampleLoggedService.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
        logger.setLevel(Level.TRACE);
    }

    @AfterEach
    void tearDown() {
        Logger logger = (Logger) LoggerFactory.getLogger(SampleLoggedService.class);
        logger.detachAppender(listAppender);
    }

    private static ListAppender<ILoggingEvent> attachAppender(Class<?> target) {
        Logger logger = (Logger) LoggerFactory.getLogger(target);
        ListAppender<ILoggingEvent> la = new ListAppender<>();
        la.start();
        logger.addAppender(la);
        logger.setLevel(Level.TRACE);
        return la;
    }

    private static void detachAppender(Class<?> target, ListAppender<ILoggingEvent> la) {
        Logger logger = (Logger) LoggerFactory.getLogger(target);
        logger.detachAppender(la);
    }

    @Configuration
    @Import({AopAutoConfiguration.class, LoggingAspect.class})
    static class TestConfig {
        @Bean
        SampleService sampleLoggedService() {
            return new SampleLoggedService();
        }
        @Bean
        BlankMethodOnlyService blankMethodOnlyService() { return new MethodBlankOnlyService(); }
    }

    interface SampleService {
        String doWork();
        String doTraced();
        String fail();
        String doWarn();
        String doErrorLevel();
        String doCustom();
    }

    interface BlankMethodOnlyService { String blankOnly(); }

    static class MethodBlankOnlyService implements BlankMethodOnlyService {
        @Log(scope = "   ")
        public String blankOnly() { return "blank"; }
    }

    @Log(scope = "DEBUG")
    static class SampleLoggedService implements SampleService {
        public String doWork() { return "ok"; }
        @Log(scope = "TRACE")
        public String doTraced() { return "traced"; }
        public String fail() { throw new IllegalStateException("boom"); }
        @Log(scope = "WARN")
        public String doWarn() { return "warned"; }
        @Log(scope = "ERROR")
        public String doErrorLevel() { return "erred"; }
        @Log(scope = "CUSTOM")
        public String doCustom() { return "custom"; }
    }

    @Test
    @DisplayName("Class-level @Log produces START and END logs at configured level, method inherits when not annotated")
    void classLevelLog_startEnd() {
        SampleService s = bean;
        String res = s.doWork();
        assertThat(res).isEqualTo("ok");

        List<ILoggingEvent> events = listAppender.list;
        assertThat(events).hasSize(2);
        assertThat(events.get(0).getLevel()).isEqualTo(Level.DEBUG);
        assertThat(events.get(0).getFormattedMessage()).contains("START").contains("doWork");
        assertThat(events.get(1).getLevel()).isEqualTo(Level.DEBUG);
        assertThat(events.get(1).getFormattedMessage()).contains("END").contains("(");
    }

    @Test
    @DisplayName("Method-level @Log overrides class level")
    void methodLevelOverrides() {
        SampleService s = bean;
        String res = s.doTraced();
        assertThat(res).isEqualTo("traced");
        List<ILoggingEvent> events = listAppender.list;
        assertThat(events).hasSize(2);
        assertThat(events.get(0).getLevel()).isEqualTo(Level.TRACE);
        assertThat(events.get(0).getFormattedMessage()).contains("START").contains("doTraced");
    }

    @Test
    @DisplayName("On exception, END is logged at error with exception info")
    void exceptionLogging() {
        SampleService s = bean;
        assertThatThrownBy(s::fail).isInstanceOf(IllegalStateException.class);
        List<ILoggingEvent> events = listAppender.list;
        assertThat(events).hasSize(2);
        assertThat(events.get(1).getLevel()).isEqualTo(Level.ERROR);
        assertThat(events.get(1).getFormattedMessage()).contains("with exception").contains("IllegalStateException");
    }

    @Test
    @DisplayName("Method annotated with WARN logs at WARN")
    void warnLevelMethod() {
        SampleService s = bean;
        String res = s.doWarn();
        assertThat(res).isEqualTo("warned");
        List<ILoggingEvent> events = listAppender.list;
        assertThat(events).hasSize(2);
        assertThat(events.get(0).getLevel()).isEqualTo(Level.WARN);
    }

    @Test
    @DisplayName("Method annotated with ERROR logs START/END at ERROR on success path")
    void errorLevelMethod_successful() {
        SampleService s = bean;
        String res = s.doErrorLevel();
        assertThat(res).isEqualTo("erred");
        List<ILoggingEvent> events = listAppender.list;
        assertThat(events).hasSize(2);
        assertThat(events.get(0).getLevel()).isEqualTo(Level.ERROR);
        assertThat(events.get(1).getLevel()).isEqualTo(Level.ERROR);
    }

    @Test
    @DisplayName("Unknown scope falls back to INFO (default branch)")
    void unknownScopeDefaultsToInfo() {
        SampleService s = bean;
        String res = s.doCustom();
        assertThat(res).isEqualTo("custom");
        List<ILoggingEvent> events = listAppender.list;
        assertThat(events).hasSize(2);
        assertThat(events.get(0).getLevel()).isEqualTo(Level.INFO);
    }

    @Test
    @DisplayName("Blank method scope with no class annotation falls back to INFO")
    void blankMethodScopeFallsBackToInfo() {
        // Attach specific appender for MethodBlankOnlyService class
        ListAppender<ILoggingEvent> la = attachAppender(MethodBlankOnlyService.class);
        try {
            String res = blankMethodOnlyService.blankOnly();
            assertThat(res).isEqualTo("blank");
            List<ILoggingEvent> events = la.list;
            assertThat(events).hasSize(2);
            assertThat(events.get(0).getLevel()).isEqualTo(Level.INFO);
        } finally {
            detachAppender(MethodBlankOnlyService.class, la);
        }
    }
}
