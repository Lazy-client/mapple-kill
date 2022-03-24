package io.renren.common.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * @author zsc
 * @version 1.0
 * @date 2022/3/24 21:01
 */
@Aspect
@Component
public class PageAspect {
    @Pointcut("execution(* list(..))")
    public void pagePointCut() {
        // TODO document why this method is empty
    }

    @Around("pagePointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Object[] args = point.getArgs();
        HashMap<String, Object> arg = (HashMap<String, Object>) args[0];
        arg.put("limit", "10");
        //执行方法

        return point.proceed();
    }
}
