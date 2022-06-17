package com.iss.cloud.disk.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;


@Aspect
@Component
@Slf4j
public class LogAspect {
    @Pointcut(value = "execution(* com.iss.cloud.disk.controller.*.*(..))")
    public void log(){

    }

    @Before("log()")
    public void doBefore(JoinPoint joinPoint){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        String url = request.getRequestURI();
        String ip = request.getRemoteAddr();
        String classMethod = joinPoint.getSignature().getDeclaringTypeName() + '.' + joinPoint.getSignature().getName();
        Object[] arg = joinPoint.getArgs();

        RequestLog requestLog = new RequestLog(url, ip, arg, classMethod);
        log.info("Request:{}", requestLog);
    }


    @After("log()")
    public void doAfter(){
        log.info("--------After---------");
    }

    @AfterReturning(returning = "result", pointcut = "log()")
    public void doAfterReturning(Object result){
        log.info("Result: {}", result);
    }


    public class RequestLog{
        private String url;
        private String ip;
        private Object[] arg;
        private String classMethod;

        public RequestLog(String url, String ip, Object[] arg, String classMethod) {
            this.url = url;
            this.ip = ip;
            this.arg = arg;
            this.classMethod = classMethod;
        }

        public RequestLog() {
        }

        @Override
        public String toString() {
            return "RequestLog{" +
                    "url='" + url + '\'' +
                    ", ip='" + ip + '\'' +
                    ", arg=" + Arrays.toString(arg) +
                    ", classMethod='" + classMethod + '\'' +
                    '}';
        }
    }
}
