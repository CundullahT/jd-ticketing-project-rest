package com.cybertek.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Configuration;

@Aspect
@Configuration
public class PerformanceAspect {

    @Pointcut("execution()")
    private void x(){}

}
