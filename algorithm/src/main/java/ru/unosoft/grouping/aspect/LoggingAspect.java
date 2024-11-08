package ru.unosoft.grouping.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Аспект для логирования исключений, возникающих в методах приложения.
 * <p>
 * Использует библиотеку SLF4J для записи логов ошибок, чтобы помочь в диагностике и отслеживании проблем.
 */
@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    /**
     * Логирует исключения, возникающие в методах пакета ru.unosoft.grouping и всех его подпакетах.
     * <p>
     * Записывает информацию о методе, где произошло исключение, аргументах метода и самом исключении.
     *
     * @param joinPoint Информация о методе, в котором произошло исключение.
     * @param ex        Исключение, которое было выброшено.
     */
    @AfterThrowing(pointcut = "within(ru.unosoft.grouping..*)", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        logger.error("Исключение в методе {}.{}() с аргументами = {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                joinPoint.getArgs(),
                ex);
    }
}
