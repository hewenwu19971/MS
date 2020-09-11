package com.hww.ms.interceptor;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

public class IPInterceptor implements HandlerInterceptor {
    @Resource
    RedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        //加锁如果30秒没有操作自动释放锁
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", "value", 30, TimeUnit.SECONDS);
        if (lock) {
            try {
                String ip = this.getIpAddress(request);
                String servletPath = request.getServletPath();
                System.out.println(ip);
                String key = ip+"::"+servletPath;
                //1.先查詢ip是否在黑名單中
                boolean isMember = redisTemplate.opsForSet().isMember("blacklist", ip);
                if (!isMember) {
                    //2.查詢ip访问次数
                    Integer count = (Integer) redisTemplate.opsForValue().get(key);
                    System.out.println(count);
                    if (count == null) {
                        //为ip加计数器
                        redisTemplate.opsForValue().set(key, 1, 60, TimeUnit.SECONDS);
                    } else if (count == 100) {
                        //加入黑名单
                        redisTemplate.opsForSet().add("blacklist", ip);
                    } else {
                        //计数器加1
                        redisTemplate.opsForValue().increment(key, 1);
                        return true;
                    }
                }
            } finally {
                redisTemplate.delete("lock");
            }
        }
        return false;
    }

    public String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            //多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = ip.indexOf(",");
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        if (StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }
}
