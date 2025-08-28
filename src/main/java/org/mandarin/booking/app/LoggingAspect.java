package org.mandarin.booking.app;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
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
public class LoggingAspect {

    @Around("@within(org.mandarin.booking.app.Log) || @annotation(org.mandarin.booking.app.Log)")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        Logger targetLogger = selectTargetLogger(joinPoint);
        String level = resolveScope(joinPoint);

        LocalDateTime startAt = LocalDateTime.now();
        long startNs = System.nanoTime();
        String signature = joinPoint.getSignature().toLongString();

        logAtLevel(targetLogger, level, "START {} at {}", signature, formatTime(startAt));

        boolean success = false;
        try {
            Object result = joinPoint.proceed();
            success = true;
            return result;
        } catch (Throwable t) {
            long elapsedMs = nanosToMillis(System.nanoTime() - startNs);
            LocalDateTime endAt = LocalDateTime.now();
            if (targetLogger.isErrorEnabled()) {
                targetLogger.error("END {} at {} ({} ms) with exception: {}", signature, formatTime(endAt), elapsedMs, t.toString());
            }
            throw t;
        } finally {
            if (success) {
                long elapsedMs = nanosToMillis(System.nanoTime() - startNs);
                LocalDateTime endAt = LocalDateTime.now();
                logAtLevel(targetLogger, level, "END {} at {} ({} ms)", signature, formatTime(endAt), elapsedMs);
            }
        }
    }

    private Logger selectTargetLogger(ProceedingJoinPoint joinPoint) {
        MethodSignature ms = (MethodSignature) joinPoint.getSignature();
        Method method = ms.getMethod();
        Class<?> targetClass = joinPoint.getTarget() != null ? joinPoint.getTarget().getClass() : method.getDeclaringClass();
        return LoggerFactory.getLogger(targetClass);
    }

    private String resolveScope(ProceedingJoinPoint joinPoint) {
        MethodSignature ms = (MethodSignature) joinPoint.getSignature();
        Method method = ms.getMethod();
        Class<?> targetClass = joinPoint.getTarget() != null ? joinPoint.getTarget().getClass() : method.getDeclaringClass();
        try {
            Method targetMethod = targetClass.getMethod(method.getName(), method.getParameterTypes());
            Log m = targetMethod.getAnnotation(Log.class);
            if (m != null && !m.scope().isBlank()) return m.scope();
        } catch (NoSuchMethodException ignored) {}
        Log c = targetClass.getAnnotation(Log.class);
        String scope = c != null ? c.scope() : "INFO";
        return scope.isBlank() ? "INFO" : scope;
    }

    private void logAtLevel(Logger logger, String level, String message, Object... args) {
        String up = level.trim().toUpperCase();
        switch (up) {
            case "TRACE" -> { if (logger.isTraceEnabled()) logger.trace(message, args); }
            case "DEBUG" -> { if (logger.isDebugEnabled()) logger.debug(message, args); }
            case "WARN"  -> { if (logger.isWarnEnabled())  logger.warn(message, args); }
            case "ERROR" -> { if (logger.isErrorEnabled()) logger.error(message, args); }
            case "INFO"  -> { if (logger.isInfoEnabled())  logger.info(message, args); }
            default       -> { if (logger.isInfoEnabled())  logger.info(message, args); }
        }
    }

    private static long nanosToMillis(long ns) {
        return ns / 1_000_000L;
    }

    private static String formatTime(LocalDateTime time) {
        return time.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
