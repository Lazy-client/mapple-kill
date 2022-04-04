package io.renren.modules.app.utils;

import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author hxx
 * @date 2022/4/4 18:07
 */
public class HttpUtils {
    /**
     * 获取客户端真实IP
     *
     * @param request
     * @return
     */
    public static String getClientIP(HttpServletRequest request) {
        // nginx 中需要设置相关配置
        String ip = request.getHeader("X-Real-IP");
        if (StringUtils.isEmpty(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
