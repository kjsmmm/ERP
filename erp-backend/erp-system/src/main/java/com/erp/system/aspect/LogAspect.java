package com.erp.system.aspect;

import com.erp.common.annotation.Log;
import com.erp.common.context.UserIdProvider;
import com.erp.system.entity.SysLog;
import com.erp.system.service.LogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

/**
 * 操作日志 AOP 切面
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {

    private final LogService logService;
    private final ObjectMapper objectMapper;
    private final UserIdProvider userIdProvider;

    @Pointcut("@annotation(com.erp.common.annotation.Log)")
    public void logPointcut() {
    }

    @Around("logPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 获取方法信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Log logAnnotation = signature.getMethod().getAnnotation(Log.class);

        // 创建日志对象
        SysLog sysLog = new SysLog();
        sysLog.setModule(logAnnotation.module());
        sysLog.setOperation(logAnnotation.operation());
        sysLog.setMethod(signature.getDeclaringTypeName() + "." + signature.getName());

        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            sysLog.setRequestUrl(request.getRequestURI());
            sysLog.setRequestMethod(request.getMethod());
            sysLog.setOperatorIp(getClientIp(request));
        }

        // 获取请求参数
        try {
            Object[] args = joinPoint.getArgs();
            sysLog.setRequestParams(objectMapper.writeValueAsString(args));
        } catch (Exception e) {
            sysLog.setRequestParams("参数序列化失败");
        }

        // 获取当前用户
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                sysLog.setOperatorName(userDetails.getUsername());
            }
            Long userId = userIdProvider.getCurrentUserId();
            if (userId != null) {
                sysLog.setOperatorId(userId);
            }
        } catch (Exception e) {
            // 忽略异常
        }

        Object result = null;
        try {
            // 执行方法
            result = joinPoint.proceed();
            sysLog.setStatus(1);
            sysLog.setResponseResult(objectMapper.writeValueAsString(result));
        } catch (Throwable e) {
            sysLog.setStatus(0);
            sysLog.setErrorMsg(e.getMessage());
            throw e;
        } finally {
            // 计算执行时间
            long executeTime = System.currentTimeMillis() - startTime;
            sysLog.setExecuteTime(executeTime);
            sysLog.setCreatedAt(LocalDateTime.now());

            // 保存日志
            logService.saveLog(sysLog);
        }

        return result;
    }

    /**
     * 获取客户端 IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理时取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
