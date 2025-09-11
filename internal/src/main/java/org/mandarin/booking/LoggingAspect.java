package org.mandarin.booking;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
class LoggingAspect {

    @Around("@within(org.mandarin.booking.Log) || @annotation(org.mandarin.booking.Log)")
    public Object around(ProceedingJoinPoint jp) throws Throwable {
        Logger logger = selectTargetLogger(jp);
        String level = resolveScope(jp);

        LocalDateTime startAt = LocalDateTime.now();
        long startNs = System.nanoTime();
        String sig = jp.getSignature().toLongString();

        logAtLevel(logger, level, "START {} at {}", sig, formatTime(startAt));
        Object ret = jp.proceed();
        long elapsedMs = nanosToMillis(System.nanoTime() - startNs);
        LocalDateTime endAt = LocalDateTime.now();
        logAtLevel(logger, level, "END {} at {} ({} ms) status={}", sig, formatTime(endAt), elapsedMs, "SUCCESS");
        return ret;
    }

    @AfterThrowing(
            argNames = "pjp,ex",
            pointcut = "@within(org.mandarin.booking.Log) || @annotation(org.mandarin.booking.Log)",
            throwing = "ex"
    )
    public void afterThrowing(JoinPoint pjp, Throwable ex) {
        Logger logger = selectTargetLogger(pjp);
        String sig = pjp.getSignature().toLongString();
        if (logger.isErrorEnabled()) {
            logger.error("END {} status=FAIL cause={}", sig, ex.toString());
        }
    }

    private Logger selectTargetLogger(JoinPoint joinPoint) {
        Class<?> targetClass = getTargetClass(joinPoint.getTarget());
        return LoggerFactory.getLogger(targetClass);
    }

    private String resolveScope(ProceedingJoinPoint joinPoint) {
        MethodSignature ms = (MethodSignature) joinPoint.getSignature();
        Method method = ms.getMethod();
        Class<?> targetClass = getTargetClass(joinPoint.getTarget());
        try {
            Method targetMethod = targetClass.getMethod(method.getName(), method.getParameterTypes());
            Log m = targetMethod.getAnnotation(Log.class);
            if (m != null && !m.scope().isBlank()) {
                return m.scope();
            }
        } catch (NoSuchMethodException ignored) {
        }
        Log c = targetClass.getAnnotation(Log.class);
        String scope = c != null ? c.scope() : "INFO";
        return scope.isBlank() ? "INFO" : scope;
    }

    private void logAtLevel(Logger logger, String level, String message, Object... args) {
        String up = level.trim().toUpperCase();
        switch (up) {
            case "TRACE" -> {
                if (logger.isTraceEnabled()) {
                    logger.trace(message, args);
                }
            }
            case "DEBUG" -> {
                if (logger.isDebugEnabled()) {
                    logger.debug(message, args);
                }
            }
            case "WARN" -> {
                if (logger.isWarnEnabled()) {
                    logger.warn(message, args);
                }
            }
            case "ERROR" -> {
                if (logger.isErrorEnabled()) {
                    logger.error(message, args);
                }
            }
            default -> {
                if (logger.isInfoEnabled()) {
                    logger.info(message, args);
                }
            }
        }
    }

    private static Class<?> getTargetClass(Object target) {
        return target.getClass();
    }

    private static long nanosToMillis(long ns) {
        return ns / 1_000_000L;
    }

    private static String formatTime(LocalDateTime time) {
        return time.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
