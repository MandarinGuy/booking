package org.mandarin.booking.app;

import static ch.qos.logback.classic.Level.DEBUG;
import static ch.qos.logback.classic.Level.ERROR;
import static ch.qos.logback.classic.Level.INFO;
import static ch.qos.logback.classic.Level.OFF;
import static ch.qos.logback.classic.Level.TRACE;
import static ch.qos.logback.classic.Level.WARN;
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

    @Autowired
    SampleService bean;
    @Autowired
    BlankMethodOnlyService blankMethodOnlyService;
    @Autowired
    BlankClassScopeService blankClassScopeService;
    @Autowired
    WarnClassScopeService warnClassScopeService;
    @Autowired
    ErrorClassScopeService errorClassScopeService;
    private ListAppender<ILoggingEvent> la;

    @BeforeEach
    void setUp() {
        Logger logger = (Logger) LoggerFactory.getLogger(SampleLoggedService.class);
        la = new ListAppender<>();
        la.start();
        logger.addAppender(la);
        logger.setLevel(TRACE);
    }

    @AfterEach
    void tearDown() {
        Logger logger = (Logger) LoggerFactory.getLogger(SampleLoggedService.class);
        logger.detachAppender(la);
    }

    @Test
    @DisplayName("Class-level @Log는 START/END를 scope 레벨에서 제공한다")
    void classLevelLogStartEnd() {
        SampleService s = bean;
        String res = s.doWork();
        assertThat(res).isEqualTo("ok");

        List<ILoggingEvent> events = la.list;
        assertThat(events).hasSize(2);
        assertThat(events.get(0).getLevel()).isEqualTo(DEBUG);
        assertThat(events.get(0).getFormattedMessage()).contains("START").contains("doWork");
        assertThat(events.get(1).getLevel()).isEqualTo(DEBUG);
        assertThat(events.get(1).getFormattedMessage()).contains("END").contains("(");
    }

    @Test
    @DisplayName("Method-level @Log는 클래스 수준을 우선합니다")
    void methodLevelOverrides() {
        SampleService s = bean;
        String res = s.doTraced();
        assertThat(res).isEqualTo("traced");
        List<ILoggingEvent> events = la.list;
        assertThat(events).hasSize(2);
        assertThat(events.getFirst().getLevel()).isEqualTo(TRACE);
        assertThat(events.getFirst().getFormattedMessage()).contains("START").contains("doTraced");
    }

    @Test
    @DisplayName("예외적으로 END는 예외 로그로 남는다")
    void exceptionLogging() {
        SampleService s = bean;
        assertThatThrownBy(s::fail).isInstanceOf(IllegalStateException.class);
        List<ILoggingEvent> events = la.list;
        assertThat(events).hasSize(2);
        assertThat(events.get(1).getLevel()).isEqualTo(ERROR);
        assertThat(events.get(1).getFormattedMessage()).contains("IllegalStateException");
    }

    @Test
    @DisplayName("WARN scope는 WARN 로그를 출력한다")
    void warnLevelMethod() {
        SampleService s = bean;
        String res = s.doWarn();
        assertThat(res).isEqualTo("warned");
        List<ILoggingEvent> events = la.list;
        assertThat(events).hasSize(2);
        assertThat(events.getFirst().getLevel()).isEqualTo(WARN);
        assertThat(events.getFirst().getFormattedMessage()).contains("START").contains("doWarn");
        assertThat(events.get(1).getLevel()).isEqualTo(WARN);
        assertThat(events.get(1).getFormattedMessage()).contains("END").contains("(");
    }

    @Test
    @DisplayName("ERROR scope인 경우 START/END가 기제된다")
    void errorLevelMethodSuccessful() {
        SampleService s = bean;
        String res = s.doErrorLevel();
        assertThat(res).isEqualTo("erred");
        List<ILoggingEvent> events = la.list;
        assertThat(events).hasSize(2);
        assertThat(events.get(0).getLevel()).isEqualTo(ERROR);
        assertThat(events.get(0).getFormattedMessage()).contains("START").contains("doErrorLevel");
        assertThat(events.get(1).getLevel()).isEqualTo(ERROR);
        assertThat(events.get(1).getFormattedMessage()).contains("END").contains("(");
    }

    @Test
    @DisplayName("알수없는 scope인 경우 기본값인 INFO scope로 로깅된다")
    void unknownScopeDefaultsToInfo() {
        SampleService s = bean;
        String res = s.doCustom();
        assertThat(res).isEqualTo("custom");
        List<ILoggingEvent> events = la.list;
        assertThat(events).hasSize(2);
        assertThat(events.getFirst().getLevel()).isEqualTo(INFO);
    }

    @Test
    @DisplayName("trim 검증")
    void infoScopeCoversExplicitInfoCase() {
        SampleService s = bean;
        String res = s.doInfo();
        assertThat(res).isEqualTo("info");
        List<ILoggingEvent> events = la.list;
        assertThat(events).hasSize(2);
        assertThat(events.get(0).getLevel()).isEqualTo(INFO);
        assertThat(events.get(0).getFormattedMessage()).contains("START").contains("doInfo");
        assertThat(events.get(1).getLevel()).isEqualTo(INFO);
        assertThat(events.get(1).getFormattedMessage()).contains("END");
    }

    @Test
    @DisplayName("기본 scope는 INFO")
    void blankMethodScopeFallsBackToInfo() {
        ListAppender<ILoggingEvent> la = attachAppenderForBlankMethodOnly();
        try {
            String res = blankMethodOnlyService.blankOnly();
            assertThat(res).isEqualTo("blank");
            List<ILoggingEvent> events = la.list;
            assertThat(events).hasSize(2);
            assertThat(events.getFirst().getLevel()).isEqualTo(INFO);
        } finally {
            detachAppender(la);
        }
    }


    @Test
    @DisplayName("DEBUG scope일때 로그 레벨이 INFO인 경우 로그가 발생하지 않는다")
    void debugScopeButLoggerAtInfoNoLogs() {
        SampleService s = bean;
        Logger logger = (Logger) LoggerFactory.getLogger(SampleLoggedService.class);
        Level prev = logger.getLevel();
        try {
            logger.setLevel(INFO);
            la.list.clear();

            String res = s.doWork();
            assertThat(res).isEqualTo("ok");

            assertThat(la.list).isEmpty();
        } finally {
            logger.setLevel(prev);
        }
    }

    @Test
    @DisplayName("로거 레벨이 TRACE scope인 경우 INFO 로그가 없다")
    void traceScopeButLoggerAtInfoNoLogs() {
        SampleService s = bean;
        Logger logger = (Logger) LoggerFactory.getLogger(SampleLoggedService.class);
        Level prev = logger.getLevel();
        try {
            logger.setLevel(INFO);
            la.list.clear();

            String res = s.doTraced();
            assertThat(res).isEqualTo("traced");

            assertThat(la.list).isEmpty();
        } finally {
            logger.setLevel(prev);
        }
    }

    @Test
    @DisplayName("예외 로그 레벨이 꺼져 있으면 로그가 기록되지 않는다")
    void exceptionLoggingSuppressedWhenErrorDisabled() {
        SampleService s = bean;
        Logger logger = (Logger) LoggerFactory.getLogger(SampleLoggedService.class);
        Level prev = logger.getLevel();
        try {
            logger.setLevel(OFF);
            la.list.clear();

            String res = s.doErrorLevel();

            assertThat(res).isEqualTo("erred");
            assertThatThrownBy(s::fail).isInstanceOf(IllegalStateException.class);

            assertThat(la.list).isEmpty();
        } finally {
            logger.setLevel(prev);
        }
    }

    @Test
    @DisplayName("Class-level scope이 OFF면 로그가 없다")
    void finallySuccessBranchRunsButNoLogWhenOff() {
        SampleService s = bean;
        Logger logger = (Logger) LoggerFactory.getLogger(SampleLoggedService.class);
        Level prev = logger.getLevel();
        try {
            logger.setLevel(OFF);
            la.list.clear();

            String res = s.doWork();
            assertThat(res).isEqualTo("ok");

            assertThat(la.list).isEmpty();
        } finally {
            logger.setLevel(prev);
        }
    }


    @Test
    @DisplayName("Class-level scope가 blank일 때 resolveScope는 INFO로 폴백되어 START/END가 INFO로 찍힌다")
    void classBlankScopeFallsBackToInfoOnSuccess() {
        ListAppender<ILoggingEvent> la = attachAppenderForBlankClass();
        try {
            String res = blankClassScopeService.doOk();
            assertThat(res).isEqualTo("ok");

            List<ILoggingEvent> events = la.list;
            assertThat(events).hasSize(2);
            assertThat(events.get(0).getLevel()).isEqualTo(INFO);
            assertThat(events.get(0).getFormattedMessage()).contains("START").contains("doOk");
            assertThat(events.get(1).getLevel()).isEqualTo(INFO);
            assertThat(events.get(1).getFormattedMessage()).contains("END");
        } finally {
            detachAppenderForBlankClass(la);
        }
    }

    @Test
    @DisplayName("proceed에서 예외 발생 시 START(INFO) 후 ERROR 로그가 남는다 (클래스 blank scope)")
    void classBlankScopeProceedThrowsLogsError() {
        ListAppender<ILoggingEvent> la = attachAppenderForBlankClass();
        try {
            assertThatThrownBy(() -> blankClassScopeService.fail())
                    .isInstanceOf(IllegalStateException.class);

            List<ILoggingEvent> events = la.list;
            assertThat(events).hasSize(2);
            assertThat(events.get(0).getLevel()).isEqualTo(INFO);
            assertThat(events.get(0).getFormattedMessage()).contains("START").contains("fail");
            assertThat(events.get(1).getLevel()).isEqualTo(ERROR);
            assertThat(events.get(1).getFormattedMessage()).contains("END").contains("IllegalStateException");
        } finally {
            detachAppenderForBlankClass(la);
        }
    }

    @Test
    @DisplayName("Class-level ERROR scope: START/END가 ERROR로 기록된다 (성공 경로)")
    void classErrorScopeLogsAtErrorOnSuccess() {
        ListAppender<ILoggingEvent> la = attachAppenderForErrorClass();
        try {
            String res = errorClassScopeService.doOk();
            assertThat(res).isEqualTo("ok");

            List<ILoggingEvent> events = la.list;
            assertThat(events).hasSize(2);
            assertThat(events.get(0).getLevel()).isEqualTo(ERROR);
            assertThat(events.get(0).getFormattedMessage()).contains("START").contains("doOk");
            assertThat(events.get(1).getLevel()).isEqualTo(ERROR);
            assertThat(events.get(1).getFormattedMessage()).contains("END");
        } finally {
            detachAppenderForErrorClass(la);
        }
    }

    @Test
    @DisplayName("Class-level ERROR scope: proceed 예외 시 ERROR 로그가 기록된다")
    void classErrorScopeProceedThrowsLogsError() {
        ListAppender<ILoggingEvent> la = attachAppenderForErrorClass();
        try {
            assertThatThrownBy(() -> errorClassScopeService.fail())
                    .isInstanceOf(IllegalStateException.class);

            List<ILoggingEvent> events = la.list;
            assertThat(events).hasSize(2);
            assertThat(events.get(0).getLevel()).isEqualTo(ERROR);
            assertThat(events.get(0).getFormattedMessage()).contains("START").contains("fail");
            assertThat(events.get(1).getLevel()).isEqualTo(ERROR);
            assertThat(events.get(1).getFormattedMessage()).contains("END").contains("IllegalStateException");
        } finally {
            detachAppenderForErrorClass(la);
        }
    }

    @Test
    @DisplayName("Class-level WARN scope: START/END가 WARN으로 기록된다")
    void classWarnScopeLogsAtWarnOnSuccess() {
        ListAppender<ILoggingEvent> la = attachAppenderForWarnClass();
        try {
            String res = warnClassScopeService.doOk();
            assertThat(res).isEqualTo("ok");

            List<ILoggingEvent> events = la.list;
            assertThat(events).hasSize(2);
            assertThat(events.get(0).getLevel()).isEqualTo(WARN);
            assertThat(events.get(0).getFormattedMessage()).contains("START").contains("doOk");
            assertThat(events.get(1).getLevel()).isEqualTo(WARN);
            assertThat(events.get(1).getFormattedMessage()).contains("END");
        } finally {
            detachAppenderForWarnClass(la);
        }
    }

    @Test
    @DisplayName("logAtLevel: WARN/ERROR 레벨 라우팅이 직접 검증된다")
    void logAtLevelWarnAndErrorAreVerifiedDirectly() throws Exception {
        Logger logger = (Logger) LoggerFactory.getLogger(
                SampleLoggedService.class);
        logger.setLevel(TRACE);
        la.list.clear();
        var aspect = new LoggingAspect();

        var m = LoggingAspect.class.getDeclaredMethod("logAtLevel", org.slf4j.Logger.class, String.class, String.class,
                Object[].class);
        m.setAccessible(true);
        m.invoke(aspect, logger, "WARN", "warn {}", new Object[]{"x"});

        assertThat(la.list).hasSize(1);
        assertThat(la.list.getFirst().getLevel()).isEqualTo(WARN);
        assertThat(la.list.getFirst().getFormattedMessage()).contains("warn x");

        la.list.clear();
        m.invoke(aspect, logger, "ERROR", "error {}", new Object[]{"y"});

        assertThat(la.list).hasSize(1);
        assertThat(la.list.getFirst().getLevel()).isEqualTo(ERROR);
        assertThat(la.list.getFirst().getFormattedMessage()).contains("error y");
    }

    @Test
    @DisplayName("logAtLevel: WARN/INFO 비활성화 시 로그가 발생하지 않는다")
    void logAtLevelNoWarnOrInfoWhenDisabled() throws Exception {
        Logger logger = (Logger) LoggerFactory.getLogger(SampleLoggedService.class);
        la.list.clear();
        var aspect = new LoggingAspect();
        var m = LoggingAspect.class.getDeclaredMethod("logAtLevel",
                org.slf4j.Logger.class,
                String.class,
                String.class,
                Object[].class);
        m.setAccessible(true);

        logger.setLevel(ERROR);
        m.invoke(aspect, logger, "WARN", "warn {}", new Object[]{"x"});
        assertThat(la.list).isEmpty();

        logger.setLevel(WARN);
        m.invoke(aspect, logger, "INFO", "info {}", new Object[]{"y"});
        assertThat(la.list).isEmpty();
    }

    @Test
    @DisplayName("finally의 if(success)==false 분기가 명시적으로 검증된다 (END 로그 없음)")
    void finallyIfSuccessFalseBranchExplicit() {
        SampleService s = bean;
        la.list.clear();

        assertThatThrownBy(s::fail).isInstanceOf(IllegalStateException.class);

        List<ILoggingEvent> events = la.list;
        assertThat(events).hasSize(2);
        assertThat(events.get(0).getFormattedMessage()).contains("START");
        assertThat(events.get(1).getLevel()).isEqualTo(ERROR);
        assertThat(events.get(1).getFormattedMessage()).contains("END").contains("IllegalStateException");
    }

    @Test
    @DisplayName("정상 반환 시 finally의 if(success)==true 분기가 명시적으로 검증된다")
    void catchJoinPointExceptionReturnErrorMessageWithFailStatus() {
        SampleService s = bean;
        la.list.clear();

        assertThatThrownBy(s::fail).isInstanceOf(IllegalStateException.class);
        List<ILoggingEvent> events = la.list;
        assertThat(events).hasSize(2);
        assertThat(events.get(0).getFormattedMessage()).contains("START");
        assertThat(events.get(1).getLevel()).isEqualTo(ERROR);
        assertThat(events.get(1).getFormattedMessage()).contains("END").contains("IllegalStateException");
    }

    private ListAppender<ILoggingEvent> attachAppenderForBlankClass() {
        Logger logger = (Logger) LoggerFactory.getLogger(BlankClassScopeServiceImpl.class);
        ListAppender<ILoggingEvent> la = new ListAppender<>();
        la.start();
        logger.addAppender(la);
        logger.setLevel(TRACE);
        return la;
    }

    private ListAppender<ILoggingEvent> attachAppenderForBlankMethodOnly() {
        Logger logger = (Logger) LoggerFactory.getLogger(MethodBlankOnlyService.class);
        ListAppender<ILoggingEvent> la = new ListAppender<>();
        la.start();
        logger.addAppender(la);
        logger.setLevel(TRACE);
        return la;
    }

    private static void detachAppender(ListAppender<ILoggingEvent> la) {
        Logger logger = (Logger) LoggerFactory.getLogger(MethodBlankOnlyService.class);
        logger.detachAppender(la);
    }

    private static void detachAppenderForBlankClass(ListAppender<ILoggingEvent> la) {
        Logger logger = (Logger) LoggerFactory.getLogger(BlankClassScopeServiceImpl.class);
        logger.detachAppender(la);
    }

    private static ListAppender<ILoggingEvent> attachAppenderForWarnClass() {
        Logger logger = (Logger) LoggerFactory.getLogger(WarnClassScopeServiceImpl.class);
        ListAppender<ILoggingEvent> la = new ListAppender<>();
        la.start();
        logger.addAppender(la);
        logger.setLevel(WARN);
        return la;
    }

    private static void detachAppenderForWarnClass(ListAppender<ILoggingEvent> la) {
        Logger logger = (Logger) LoggerFactory.getLogger(WarnClassScopeServiceImpl.class);
        logger.detachAppender(la);
    }

    private static ListAppender<ILoggingEvent> attachAppenderForErrorClass() {
        Logger logger = (Logger) LoggerFactory.getLogger(ErrorClassScopeServiceImpl.class);
        ListAppender<ILoggingEvent> la = new ListAppender<>();
        la.start();
        logger.addAppender(la);
        logger.setLevel(ERROR);
        return la;
    }

    private static void detachAppenderForErrorClass(ListAppender<ILoggingEvent> la) {
        Logger logger = (Logger) LoggerFactory.getLogger(ErrorClassScopeServiceImpl.class);
        logger.detachAppender(la);
    }

    interface SampleService {
        String doWork();

        String doTraced();

        void fail();

        String doWarn();

        String doErrorLevel();

        String doCustom();

        String doInfo();
    }

    interface BlankMethodOnlyService {
        String blankOnly();
    }

    interface BlankClassScopeService {
        String doOk();

        void fail();

    }

    interface WarnClassScopeService {
        String doOk();

    }

    interface ErrorClassScopeService {
        String doOk();

        void fail();

    }

    @Configuration
    @Import({AopAutoConfiguration.class, LoggingAspect.class})
    static class TestConfig {
        @Bean
        SampleService sampleLoggedService() {
            return new SampleLoggedService();
        }

        @Bean
        BlankMethodOnlyService blankMethodOnlyService() {
            return new MethodBlankOnlyService();
        }

        @Bean
        BlankClassScopeService blankClassScopeService() {
            return new BlankClassScopeServiceImpl();
        }

        @Bean
        WarnClassScopeService warnClassScopeService() {
            return new WarnClassScopeServiceImpl();
        }

        @Bean
        ErrorClassScopeService errorClassScopeService() {
            return new ErrorClassScopeServiceImpl();
        }
    }

    static class MethodBlankOnlyService implements BlankMethodOnlyService {
        @Log(scope = "   ")
        public String blankOnly() {
            return "blank";
        }
    }

    @Log(scope = "   ")
    static class BlankClassScopeServiceImpl implements BlankClassScopeService {
        public String doOk() {
            return "ok";
        }

        public void fail() {
            throw new IllegalStateException("boom");
        }
    }

    @Log(scope = "WARN")
    static class WarnClassScopeServiceImpl implements WarnClassScopeService {
        public String doOk() {
            return "ok";
        }

    }

    @Log(scope = "ERROR")
    static class ErrorClassScopeServiceImpl implements ErrorClassScopeService {
        public String doOk() {
            return "ok";
        }

        public void fail() {
            throw new IllegalStateException("boom");
        }

    }

    @Log(scope = "DEBUG")
    static class SampleLoggedService implements SampleService {
        public String doWork() {
            return "ok";
        }

        @Log(scope = "TRACE")
        public String doTraced() {
            return "traced";
        }

        public void fail() {
            throw new IllegalStateException("boom");
        }

        @Log(scope = "WARN")
        public String doWarn() {
            return "warned";
        }

        @Log(scope = "ERROR")
        public String doErrorLevel() {
            return "erred";
        }

        @Log(scope = "CUSTOM")
        public String doCustom() {
            return "custom";
        }

        @Log(scope = " info ")
        public String doInfo() {
            return "info";
        }
    }
}
