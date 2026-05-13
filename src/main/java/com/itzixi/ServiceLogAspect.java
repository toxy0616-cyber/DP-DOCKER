package com.itzixi;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

/**
 * Service 层耗时日志切面。
 * 统一记录 service.impl 包下方法的执行时间。
 */
@Aspect
@Slf4j
@Component
public class ServiceLogAspect {

    /**
     * 环绕通知，统计目标方法总耗时并输出日志。
     *
     * @param joinPoint 连接点对象
     * @return 目标方法执行结果
     * @throws Throwable 目标方法可能抛出的异常
     */
    @Around("execution(* com.itzixi.service.impl.*.*(..))")
    public Object recordTimeLog(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object result = joinPoint.proceed();

        stopWatch.stop();
        log.info(stopWatch.prettyPrint());
        log.info(stopWatch.shortSummary());
        log.info("所有任务总耗时: {}", stopWatch.getTotalTimeMillis());
        log.info("任务总数: {}", stopWatch.getTaskCount());
        return result;
    }
}
