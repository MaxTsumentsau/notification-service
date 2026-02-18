package com.max2ba.notification_service.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

     @Around("@annotation(com.max2ba.notification_service.annotation.Loggable) || " +
             "@within(com.max2ba.notification_service.annotation.Loggable)")
     public Object logAround(ProceedingJoinPoint pjp) throws Throwable {
          long start = System.currentTimeMillis();

          MethodSignature signature = (MethodSignature) pjp.getSignature();
          String className = pjp.getTarget().getClass().getSimpleName();
          String methodName = signature.getName();
          Object[] args = pjp.getArgs();

          log.info("The method {}.{}({}) starts",
                  className, methodName, Arrays.toString(args));

          try {
               Object result = pjp.proceed();

               long time = System.currentTimeMillis() - start;
               log.info("The method {}.{}({}) succeeded ends with time = {}ms, returned result = {}",
                       className, methodName, Arrays.toString(args), time, result);

               return result;
          } catch (Throwable ex) {
               long time = System.currentTimeMillis() - start;
               log.error("The method {}.{}({}) ends with error, time = {}ms, exception={}: {}",
                       className, methodName, Arrays.toString(args), time,
                       ex.getClass().getSimpleName(), ex.getMessage());
               throw ex;
          }
     }
}

